"""
STEP 8a — generate a corrected formula for a flagged cell.

Two independent fix strategies, chosen by which rule flagged the cell:

  - 5C (rule violation): surgical regex fix on the cell's OWN formula --
    e.g. flip a bare "REF=31" comparison to "REF>=31" -- everything else
    in that row's formula (its own cell references, other thresholds)
    is left untouched. This is the strategy that matters for the real
    bug in this file (see detect.py docstring: 5C is the only rule that
    fires on it).

  - 5A/5B (consensus mismatch): re-point a correct PEER formula at the
    flagged row. Take one real formula from a row/column that matches
    the majority pattern, then renumber every cell reference whose row
    number equals the template's row to the target row's number instead
    (column letters are left as-is -- rows in the same column reference
    the same sibling columns, just at their own row).
    Caveat: if a threshold number happens to equal the template row
    number, this could over-match; acceptable for now, documented here
    rather than silently assumed safe.
"""
import re

BARE_EQ_RE = re.compile(r"(?<![<>])(\$?[A-Za-z]{1,3}\$?\d{1,7})\s*=\s*(-?\d+(?:\.\d+)?)")
CELL_REF_ROW_RE = re.compile(r"([A-Za-z]{1,3})(\d{1,7})")


def fix_bare_equals_to_gte(formula):
    """Flips 'REF=31' style bare-equality comparisons to 'REF>=31'. Skips
    text inside quoted string literals (grade labels like "B2")."""
    from .normalize import STRING_LITERAL_RE
    parts = STRING_LITERAL_RE.split(formula)
    for i in range(0, len(parts), 2):
        parts[i] = BARE_EQ_RE.sub(lambda m: f"{m.group(1)}>={m.group(2)}", parts[i])
    return "".join(parts)


def fix_via_template(template_formula, template_row, target_row):
    """Renumbers cell refs in template_formula from template_row to target_row."""
    def repl(m):
        col_letters, row_num = m.group(1), m.group(2)
        if int(row_num) == template_row:
            return f"{col_letters}{target_row}"
        return m.group(0)
    return CELL_REF_ROW_RE.sub(repl, template_formula)


def find_template_formula(ws, col, majority_pattern, data_start_row):
    """Finds one real (row, formula) pair in `col` whose normalized pattern
    matches majority_pattern -- used as the source for fix_via_template."""
    from .normalize import normalize_formula
    for row in range(data_start_row, ws.max_row + 1):
        raw = ws.cell(row=row, column=col).value
        if isinstance(raw, str) and raw.startswith("=") and normalize_formula(raw) == majority_pattern:
            return row, raw
    return None, None


def generate_fix(ws, row, col, flag_list, col_majority, data_start_row):
    """
    flag_list: the list of flag-dicts for this (row, col) from detect.run_detection.
    col_majority: {col: majority_pattern} from within_column_majority (Step 5A),
                  reused here as the source of a good template for 5A/5B fixes.
    Returns the new formula string, or None if no safe fix could be generated.
    """
    rules_fired = {f["rule"] for f in flag_list}
    original = ws.cell(row=row, column=col).value

    # Priority: a 5C bare-equals violation has a precise, minimal fix.
    for f in flag_list:
        if f["rule"] == "5C" and "bare '='" in f.get("reason", ""):
            return fix_bare_equals_to_gte(original)

    # Otherwise (5A/5B/5D, or a 5C shape violation we can't safely auto-rewrite):
    # fall back to the peer/majority template approach if we have one.
    # 5D (blank cell) uses the exact same fix -- "paste the correct formula"
    # IS "point the majority pattern at this row", no separate logic needed.
    if rules_fired & {"5A", "5B", "5D"}:
        majority = col_majority.get(col)
        if majority:
            template_row, template_formula = find_template_formula(ws, col, majority, data_start_row)
            if template_formula:
                return fix_via_template(template_formula, template_row, row)

    return None  # no safe automatic fix -- leave flagged, don't guess
