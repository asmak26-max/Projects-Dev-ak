"""
STEP 4 — Formula normalization: compare by PATTERN, not exact cell address.

Critical edge case handled here: grade formulas contain string literals
that LOOK like cell references, e.g.
    =IF(M7>=46,"A1",IF(M7>=41,"A2",...))
"A1" and "A2" are grade-label TEXT, not cell refs -- a naive regex over
the whole formula would corrupt them. So this splits the formula into
quoted-string segments vs code segments first, and only touches code
segments.

Also normalizes the cosmetic leading "+" some teachers add out of old
Lotus/Excel habit (=+AD7/300*100 vs =AD7/300*100 -- identical to Excel)
so it's never mistaken for a real inconsistency.
"""
import re

# Matches A1, $A$1, B12, AA7, etc. -- 1-3 letters + 1-7 digits, optional $ on either part.
CELL_REF_RE = re.compile(r"\$?[A-Za-z]{1,3}\$?\d{1,7}")
STRING_LITERAL_RE = re.compile(r'("[^"]*")')  # capturing group -- re.split must keep the
                                               # matched quotes in the output, or they vanish
                                               # entirely instead of alternating with code


def normalize_formula(formula):
    """
    formula: the raw string from openpyxl (starts with '=') or None/non-formula.
    Returns a normalized pattern string, or the original value if it isn't
    a formula (so blank/plain-number cells pass through unchanged and are
    simply never flagged by the formula checks).
    """
    if not isinstance(formula, str) or not formula.startswith("="):
        return formula

    f = formula
    # Cosmetic leading unary "+" right after "=" -- not a real difference.
    if f.startswith("=+"):
        f = "=" + f[2:]

    # Split into alternating [code, "string", code, "string", ...] so cell-ref
    # substitution only ever touches the code parts, never text inside quotes.
    parts = STRING_LITERAL_RE.split(f)
    for i in range(0, len(parts), 2):  # even indices = code segments
        parts[i] = CELL_REF_RE.sub("REF", parts[i])

    return "".join(parts)


def normalize_sheet_formulas(ws, columns=None):
    """
    Returns {(row, col): normalized_pattern_or_value} for every cell in the
    sheet (or just the given column indices, if provided). Non-formula
    cells pass through as-is via normalize_formula's fallback.
    """
    result = {}
    max_row = ws.max_row
    cols = columns if columns is not None else range(1, ws.max_column + 1)
    for row in range(1, max_row + 1):
        for col in cols:
            val = ws.cell(row=row, column=col).value
            result[(row, col)] = normalize_formula(val)
    return result
