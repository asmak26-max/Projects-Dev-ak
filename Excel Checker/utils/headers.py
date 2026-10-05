"""
STEP 2 — Header reading + column map per sheet.

Marksheets like this one have a MULTI-ROW header (exam title, class
label, subject name, W/O/T/G sub-label, max-marks row) built out of
merged cells, before the actual data rows start. This module flattens
that into one label per column, generically enough to survive columns
being added/removed/reordered next year.

Design decisions (reasoned):
  1. Data start row is auto-detected as the first row where column A
     holds a plain integer (Roll No sequence 1,2,3...). Header rows
     are everything above that. This is a marksheet-specific heuristic,
     not a fully general one -- documented as a v1 limitation.
  2. Wide merges (title/class-label rows that span most of the sheet,
     e.g. "Ist Unit Test Examination" across all subject columns) are
     structural noise, not per-column labels -- excluded by a width
     threshold, or they'd get glued onto every column's header.
  3. Only STRING header cells are used to build a label. Numeric header
     rows (max-marks row: 40/10/50) are metadata, not descriptive text,
     and would only add noise to Step 3's fuzzy type-matching.
"""
from collections import namedtuple
from openpyxl.utils import get_column_letter

ColumnInfo = namedtuple("ColumnInfo", ["index", "letter", "label"])

TITLE_MERGE_WIDTH_THRESHOLD = 10  # merges wider than this = section title, not a column label


def find_data_start_row(ws, id_col=1, max_scan=40):
    """
    First row where the id_col (default: column A, the Roll No column)
    holds a plain int -- that's the first real data row. Everything
    above it is header.
    """
    for r in range(1, max_scan + 1):
        val = ws.cell(row=r, column=id_col).value
        if isinstance(val, (int, float)) and not isinstance(val, bool):
            return r
    # Fallback: no numeric id column found in the scanned range.
    # Rather than silently guessing, surface this so it can be handled
    # explicitly (a differently-shaped sheet would need a real rule).
    raise ValueError(
        f"Could not auto-detect data start row in sheet '{ws.title}' "
        f"(no integer found in column {id_col} within first {max_scan} rows)."
    )


def build_column_map(ws, data_start_row=None):
    """
    Returns {col_index: ColumnInfo(index, letter, label)} for every
    column in the sheet, with merged/multi-row headers flattened into
    one label string (e.g. "English T", "Avg. Attendance ( 2 months )").
    """
    if data_start_row is None:
        data_start_row = find_data_start_row(ws)

    header_rows = range(1, data_start_row)
    max_col = ws.max_column

    skip_cells = set()          # cells belonging to wide "title" merges
    merged_value = {}           # (row, col) -> top-left value, for real per-column merges

    for mr in ws.merged_cells.ranges:
        if mr.min_row > data_start_row - 1:
            continue  # merge lives entirely in the data area, irrelevant here
        width = mr.max_col - mr.min_col + 1
        cells = [(r, c) for r in range(mr.min_row, mr.max_row + 1)
                         for c in range(mr.min_col, mr.max_col + 1)]
        if width > TITLE_MERGE_WIDTH_THRESHOLD:
            skip_cells.update(cells)
            continue
        top_left_val = ws.cell(row=mr.min_row, column=mr.min_col).value
        for cell in cells:
            merged_value[cell] = top_left_val

    column_map = {}
    for col in range(1, max_col + 1):
        parts, seen = [], set()
        for row in header_rows:
            if (row, col) in skip_cells:
                continue
            val = merged_value.get((row, col), ws.cell(row=row, column=col).value)
            if not isinstance(val, str):
                continue  # numeric header rows (max marks) deliberately excluded
            val = val.strip()
            if val and val not in seen:
                seen.add(val)
                parts.append(val)
        label = " ".join(parts) if parts else None
        column_map[col] = ColumnInfo(col, get_column_letter(col), label)

    return column_map, data_start_row
