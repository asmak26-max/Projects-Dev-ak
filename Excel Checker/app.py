"""
Excel Formula Consistency Checker & Auto-Fixer
------------------------------------------------
Full pipeline: Steps 1-5D (detection), 6 (preview), 7 (quick-add
dictionary), 8a (apply fixes), 9/10 (audit log), 11 (download).
"""
import streamlit as st
import pandas as pd
from io import BytesIO

from utils.loader import load_both
from utils.headers import build_column_map
from utils.classify import classify_all_columns, load_keyword_dict, save_keyword_dict
from utils.detect import run_detection, within_column_majority, PEER_VOTE_TYPES
from utils.fix import generate_fix
from utils.audit import write_log, cleanup_old_logs, load_all_logs

st.set_page_config(page_title="Formula Consistency Checker", layout="wide")

# STEP 9 (startup task): purge expired auto-delete logs once per session.
cleanup_old_logs()

st.title("📊 Excel Formula Consistency Checker & Auto-Fixer")
st.caption("Upload a marksheet workbook to check every subject/total/grade formula for silent inconsistencies.")

HIGHLIGHT = "background-color: #C9A400"


def analyze_sheet(ws):
    col_map, data_start = build_column_map(ws)
    types = classify_all_columns(col_map)
    flags = run_detection(ws, types, data_start)
    peer_vote_cols = [c for c, (t, v) in types.items() if t in PEER_VOTE_TYPES | {"Total"}]
    col_majority, _ = within_column_majority(ws, peer_vote_cols, data_start)
    return col_map, data_start, types, flags, col_majority


def build_preview_df(ws_values, col_map, data_start, flags):
    cols = [c for c in col_map if col_map[c].label]
    data, row_labels = [], []
    for row in range(data_start, ws_values.max_row + 1):
        row_labels.append(row)
        data.append([ws_values.cell(row=row, column=c).value for c in cols])
    df = pd.DataFrame(data, index=row_labels, columns=[col_map[c].label for c in cols])
    flagged_positions = {(r, col_map[c].label) for (r, c) in flags if c in cols}
    return df, flagged_positions


def style_flagged(df, flagged_positions):
    def highlight(row):
        return [HIGHLIGHT if (row.name, col) in flagged_positions else "" for col in df.columns]
    return df.style.apply(highlight, axis=1)


# ---------------- Sidebar: Step 10, audit log viewer ----------------
with st.sidebar:
    st.header("Audit log")
    if st.button("📜 View Audit Log"):
        st.session_state["show_audit_log"] = True
    if st.session_state.get("show_audit_log"):
        rows = load_all_logs()
        if rows:
            st.dataframe(pd.DataFrame(rows), use_container_width=True, height=400)
        else:
            st.write("No log entries yet.")

uploaded_file = st.file_uploader("Upload marksheet (.xlsx)", type=["xlsx"])

if uploaded_file is not None:
    file_bytes = uploaded_file.read()
    try:
        formula_wb, value_wb = load_both(file_bytes)
    except Exception as e:
        st.error(f"Couldn't read this as a valid .xlsx workbook: {e}")
        st.stop()

    st.success(f"Loaded **{uploaded_file.name}** — {len(formula_wb.sheetnames)} sheet(s): "
               f"{', '.join(formula_wb.sheetnames)}")

    per_sheet = {}  # sheet -> (col_map, data_start, types, flags, col_majority)
    keyword_dict = load_keyword_dict()

    for sheet_name in formula_wb.sheetnames:
        ws_f = formula_wb[sheet_name]
        ws_v = value_wb[sheet_name]
        col_map, data_start, types, flags, col_majority = analyze_sheet(ws_f)
        per_sheet[sheet_name] = (col_map, data_start, types, flags, col_majority)

        recognized_flagged = sum(1 for (r, c) in flags if types[c][0] != "Unrecognized")
        unrecognized_flagged = sum(1 for (r, c) in flags if types[c][0] == "Unrecognized")

        st.subheader(f"Sheet: {sheet_name}")
        st.write(f"🟡 **{recognized_flagged}** cells flagged in recognized columns (auto-fixable) · "
                 f"**{unrecognized_flagged}** in unrecognized columns (flag-only)")

        df, flagged_positions = build_preview_df(ws_v, col_map, data_start, flags)
        st.dataframe(style_flagged(df, flagged_positions), use_container_width=True, height=350)

        # ---------------- STEP 7: quick-add-to-dictionary UI ----------------
        unrecognized_cols = [c for c in col_map if types[c][0] == "Unrecognized" and col_map[c].label]
        if unrecognized_cols:
            with st.expander(f"🏷️ Teach the tool — {len(unrecognized_cols)} unrecognized column(s) in {sheet_name}"):
                existing_types = list(keyword_dict.keys())
                for c in unrecognized_cols:
                    label = col_map[c].label
                    key_base = f"{sheet_name}_{c}"
                    col1, col2, col3 = st.columns([2, 2, 2])
                    with col1:
                        st.write(f"**{label}**")
                    with col2:
                        choice = st.selectbox("Assign to type", existing_types + ["+ New type"],
                                               key=f"sel_{key_base}", label_visibility="collapsed")
                    with col3:
                        new_type_name = ""
                        if choice == "+ New type":
                            new_type_name = st.text_input("New type name", key=f"txt_{key_base}",
                                                           label_visibility="collapsed", placeholder="New type name")
                    if st.button("Add", key=f"btn_{key_base}"):
                        target_type = new_type_name.strip() if choice == "+ New type" else choice
                        if not target_type:
                            st.warning("Enter a type name first.")
                        else:
                            keyword_dict.setdefault(target_type, [])
                            alias = label.lower()
                            if alias not in keyword_dict[target_type]:
                                keyword_dict[target_type].append(alias)
                            save_keyword_dict(keyword_dict)
                            st.success(f"'{label}' → {target_type}. Re-analyzing on next action.")
                            st.rerun()

    st.divider()
    st.info("Preview shows the sheet's own cached calculated values (last saved in Excel), "
            "not live recalculation — this tool checks and edits formula text, it doesn't run Excel's engine.")

    # ---------------- STEP 9: ask retention BEFORE apply, to avoid a second round-trip ----------------
    keep_log = st.checkbox("Keep the audit log for this Apply-Fixes run permanently "
                            "(otherwise it auto-deletes after 7 days)")

    if st.button("🔧 Apply Fixes", type="primary"):
        applied, skipped = [], 0
        for sheet_name, (col_map, data_start, types, flags, col_majority) in per_sheet.items():
            ws_f = formula_wb[sheet_name]
            for (row, col), flag_list in flags.items():
                if types[col][0] == "Unrecognized":
                    continue  # 5E: flag-only, never auto-fix
                old = ws_f.cell(row=row, column=col).value
                new = generate_fix(ws_f, row, col, flag_list, col_majority, data_start)
                if new and new != old:
                    ws_f.cell(row=row, column=col).value = new
                    flag_types = ",".join(sorted({f["rule"] for f in flag_list}))
                    applied.append((sheet_name, ws_f.cell(row=row, column=col).coordinate, flag_types, old, new))
                else:
                    skipped += 1

        st.success(f"Applied {len(applied)} fixes." +
                   (f" ({skipped} flagged cells had no safe automatic fix.)" if skipped else ""))
        with st.expander("Show every fix applied"):
            st.dataframe(pd.DataFrame(applied, columns=["Sheet", "Cell", "Flag type(s)", "Old formula", "New formula"]))

        # STEP 9: write the audit log for this run (skipped-only runs still log nothing, by design)
        if applied:
            write_log(applied, keep_forever=keep_log, workbook_filename=uploaded_file.name)

        out_buffer = BytesIO()
        formula_wb.save(out_buffer)
        st.download_button(
            "⬇️ Download corrected workbook",
            data=out_buffer.getvalue(),
            file_name=f"corrected_{uploaded_file.name}",
            mime="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
        )
else:
    st.write("⬆️ Upload a .xlsx marksheet to begin.")
