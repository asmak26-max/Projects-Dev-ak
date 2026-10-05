"""
STEP 5A / 5B / 5C — detection engine.

5A: within-column majority vote (row-level outliers).
5B: cross-column peer comparison (whole-column-wrong bugs) -- but only
    for types where every peer column is expected to share ONE shape.
    "Total" is deliberately excluded: it legitimately contains two valid
    shapes (a per-subject SUM over a contiguous range, and the aggregate
    SUM over a non-contiguous list), so voting columns against each other
    would flag the correct aggregate Total as an "outlier". That case is
    exactly what 5C's shape-aware rule is for instead.
5C: rule-based structural validation, independent of any peer/majority --
    the only check that fires when a bug is copied into EVERY peer column
    (as verified in this real file: all 6 grade columns share the same
    "=31" bug, so 5A and 5B both find nothing to flag; only 5C catches it).
"""
import re
from collections import Counter
from .normalize import normalize_formula

# Types where all peer columns are expected to share one identical pattern.
# "Total" is excluded on purpose -- see module docstring.
PEER_VOTE_TYPES = {"Grade", "Deduction", "Attendance", "Final Total", "Percentage"}


def _is_formula(val):
    return isinstance(val, str) and val.startswith("=")


def _data_rows(ws, data_start_row):
    return range(data_start_row, ws.max_row + 1)


def find_real_data_rows(ws, data_start_row, id_col=1):
    """
    Rows where the id column (Roll No, column A) actually has a value --
    distinguishes real student rows from trailing blank rows Excel's
    max_row sometimes includes (stray formatting beyond the last student).
    Used by 5D so it doesn't flag blank padding rows as "missing formulas".
    """
    return [r for r in _data_rows(ws, data_start_row) if ws.cell(row=r, column=id_col).value is not None]


# ---------- 5A: within-column majority vote ----------

def within_column_majority(ws, columns, data_start_row):
    """
    columns: iterable of column indices to check.
    Returns:
      col_majority: {col: majority_pattern_or_None}
      flags: {(row, col): {"rule": "5A", "own": pattern, "majority": majority}}
    """
    col_majority = {}
    flags = {}
    for col in columns:
        patterns = []
        for row in _data_rows(ws, data_start_row):
            raw = ws.cell(row=row, column=col).value
            if _is_formula(raw):
                patterns.append(normalize_formula(raw))
        if not patterns:
            col_majority[col] = None
            continue
        majority, _count = Counter(patterns).most_common(1)[0]
        col_majority[col] = majority
        for row in _data_rows(ws, data_start_row):
            raw = ws.cell(row=row, column=col).value
            if _is_formula(raw):
                norm = normalize_formula(raw)
                if norm != majority:
                    flags[(row, col)] = {"rule": "5A", "own": norm, "majority": majority}
    return col_majority, flags


# ---------- 5B: cross-column peer comparison ----------

def cross_column_peer_check(col_majority, column_types, data_start_row, ws):
    """
    col_majority: from within_column_majority (reused, not recomputed).
    column_types: {col: (type_name, variant)} from classify.classify_all_columns.
    Returns flags: {(row, col): {"rule": "5B", "own": ..., "peer_majority": ..., "type": ...}}

    Grouped by (type, variant) -- not type alone -- so e.g. per-subject
    Grade columns are only ever voted against OTHER per-subject Grade
    columns, never against the differently-scaled final/aggregate Grade
    column. See classify.classify_column for why.
    """
    flags = {}
    by_type = {}
    for col, (t, variant) in column_types.items():
        if t in PEER_VOTE_TYPES and col_majority.get(col):
            by_type.setdefault((t, variant), []).append(col)

    for (t, variant), cols in by_type.items():
        if len(cols) < 2:
            continue  # nothing to vote against -- 5C is the safety net here
        pattern_votes = Counter(col_majority[c] for c in cols)
        peer_majority, _ = pattern_votes.most_common(1)[0]
        for col in cols:
            own = col_majority[col]
            if own != peer_majority:
                for row in _data_rows(ws, data_start_row):
                    raw = ws.cell(row=row, column=col).value
                    if _is_formula(raw):
                        flags[(row, col)] = {
                            "rule": "5B", "type": t, "own": own, "peer_majority": peer_majority
                        }
    return flags


# ---------- 5C: rule-based structural validation ----------

NUM_RE = re.compile(r"-?\d+(?:\.\d+)?")


def _check_grade_or_deduction(norm_formula):
    """
    Nested IF chain, every comparison against REF must be '>=' (never bare '=').
    Also checks thresholds are strictly descending as a secondary check.
    Does NOT verify the final else is literally the lowest label (documented
    simplification -- catching the >= vs = operator bug is the priority).
    """
    if not norm_formula.startswith("=IF("):
        return False, "not a nested IF() chain"

    # Every REF-comparison operator used in the formula.
    ops = re.findall(r"REF\s*(>=|<=|<>|>|<|=)\s*-?\d", norm_formula)
    if not ops:
        return False, "no REF comparisons found"
    if any(op == "=" for op in ops):
        return False, "uses bare '=' instead of '>=' in a threshold comparison"
    if any(op not in (">=",) for op in ops):
        return False, f"uses comparison operator(s) other than '>=': {set(ops)}"

    thresholds = [float(m) for m in re.findall(r"REF\s*>=\s*(-?\d+(?:\.\d+)?)", norm_formula)]
    if thresholds != sorted(thresholds, reverse=True):
        return False, "thresholds are not strictly descending"

    return True, ""


def _check_total(norm_formula):
    m = re.match(r"^=SUM\((.+)\)$", norm_formula)
    if not m:
        return False, "not a =SUM(...) formula"
    inner = m.group(1)
    if re.fullmatch(r"REF:REF", inner):
        return True, ""  # component total: contiguous range
    if re.fullmatch(r"REF(,REF){1,}", inner):
        return True, ""  # aggregate total: comma-separated list
    return False, f"SUM argument shape not recognized: {inner}"


def _check_final_total(norm_formula):
    if re.match(r"^=MAX\(0,IF\(", norm_formula):
        return True, ""
    return False, "expected =MAX(0, IF(...)) shape"


def _check_percentage(norm_formula):
    if re.match(r"^=REF/\d+(\.\d+)?\*100$", norm_formula):
        return True, ""
    return False, "expected =REF/<max>*100 shape"


def _check_attendance(norm_formula):
    # No fixed shape specified for Attendance in the rules dictionary --
    # it's often a plain typed-in average rather than a formula. Skip.
    return True, ""


RULES = {
    "Grade": _check_grade_or_deduction,
    "Deduction": _check_grade_or_deduction,
    "Total": _check_total,
    "Final Total": _check_final_total,
    "Percentage": _check_percentage,
    "Attendance": _check_attendance,
}


def rule_based_check(ws, column_types, data_start_row):
    """
    Returns flags: {(row, col): {"rule": "5C", "type": ..., "reason": ..., "formula": ...}}
    Only runs for RECOGNIZED types with a rule defined (never "Unrecognized" -- 5E).
    """
    flags = {}
    for col, t in column_types.items():
        checker = RULES.get(t)
        if checker is None:
            continue
        for row in _data_rows(ws, data_start_row):
            raw = ws.cell(row=row, column=col).value
            if not _is_formula(raw):
                continue
            norm = normalize_formula(raw)
            ok, reason = checker(norm)
            if not ok:
                flags[(row, col)] = {"rule": "5C", "type": t, "reason": reason, "formula": raw}
    return flags


# ---------- 5D: blank/missing-formula detection ----------

MOSTLY_FORMULA_THRESHOLD = 0.6  # a column counts as "formula-filled" if this fraction of
                                 # real data rows hold a formula; raw mark-entry columns
                                 # (typed numbers, never formulas) naturally sit at 0% and
                                 # are skipped, so this needs no per-type/column config.


def missing_formula_check(ws, columns, data_start_row):
    """
    Runs on ALL given columns regardless of recognized/Unrecognized type
    (per Step 5E: 5A/5B/5D still run on Unrecognized columns -- only 5C
    and auto-fix are type-gated). Flags a blank cell as "Missing Formula"
    when its column is mostly formula-filled elsewhere.
    Returns {(row, col): {"rule": "5D", "reason": ...}}.
    """
    flags = {}
    real_rows = find_real_data_rows(ws, data_start_row)
    if not real_rows:
        return flags
    for col in columns:
        formula_count = sum(1 for r in real_rows if _is_formula(ws.cell(row=r, column=col).value))
        ratio = formula_count / len(real_rows)
        if ratio < MOSTLY_FORMULA_THRESHOLD:
            continue  # not a formula column (e.g. raw marks entry) -- nothing to check
        for r in real_rows:
            if ws.cell(row=r, column=col).value is None:
                flags[(r, col)] = {
                    "rule": "5D",
                    "reason": f"blank cell in a column that is {ratio:.0%} formula-filled",
                }
    return flags


# ---------- Orchestration ----------

def run_detection(ws, column_types, data_start_row):
    """
    Runs 5A -> 5B -> 5C -> 5D for one sheet and merges results.
    column_types: {col: (type_name, variant)}.
    Returns {(row, col): [list of flag-dicts, one per rule that fired]}.
    A cell can be flagged by more than one rule; all are kept.
    """
    peer_vote_cols = [c for c, (t, v) in column_types.items() if t in PEER_VOTE_TYPES | {"Total"}]
    col_majority, flags_5a = within_column_majority(ws, peer_vote_cols, data_start_row)
    flags_5b = cross_column_peer_check(col_majority, column_types, data_start_row, ws)
    type_only = {c: t for c, (t, v) in column_types.items()}
    flags_5c = rule_based_check(ws, type_only, data_start_row)
    # 5D runs on every column that has a real header (recognized or not --
    # 5E only exempts Unrecognized columns from 5C and from auto-fix,
    # not from this check).
    flags_5d = missing_formula_check(ws, list(column_types.keys()), data_start_row)

    merged = {}
    for source in (flags_5a, flags_5b, flags_5c, flags_5d):
        for key, val in source.items():
            merged.setdefault(key, []).append(val)
    return merged
