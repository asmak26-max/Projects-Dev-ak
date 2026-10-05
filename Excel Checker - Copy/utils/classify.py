"""
STEP 3 — Keyword dictionary + fuzzy header matching -> column TYPE.

Loaded from column_types.json so it's editable/saveable at runtime
(Step 7's "quick add to dictionary" UI just appends to this file --
no code changes needed for future headers).

Matching strategy (in order), reasoned:
  1. LONGEST substring alias wins. Compound aliases like "final total"
     must be checked before the single word "total", or a "Final Total"
     header would be misread as plain "Total" (both words are literally
     present). Longest-alias-first resolves that without special-casing.
  2. Exact short-token match. Real headers here compress to single
     letters after flattening (e.g. "English T", "English G" -- the W/O/T/G
     sub-labels). A 1-2 char alias like "t"/"g" would fuzzy-match all kinds
     of unrelated short tokens if run through difflib, so these are matched
     by exact token equality only, never fuzzily.
  3. Fuzzy fallback (difflib) for anything longer that's merely misspelled
     or abbreviated differently than the dictionary expects (e.g. "Attendence").
  4. No match -> "Unrecognized".
"""
import json
import re
import difflib
from pathlib import Path

DEFAULT_DICT_PATH = Path(__file__).parent.parent / "column_types.json"
SUBSTRING_MIN_LEN = 3
FUZZY_CUTOFF = 0.75


def load_keyword_dict(path=DEFAULT_DICT_PATH):
    with open(path) as f:
        return json.load(f)


def save_keyword_dict(keyword_dict, path=DEFAULT_DICT_PATH):
    with open(path, "w") as f:
        json.dump(keyword_dict, f, indent=2)


def _tokenize(label):
    return [t for t in re.split(r"[^a-zA-Z%]+", label.lower()) if t]


def classify_column(label, keyword_dict=None):
    """
    Returns (type_name, variant) or ("Unrecognized", None).

    `variant` distinguishes two columns of the same TYPE that are not
    expected to share one formula shape -- e.g. "Grade" covers both a
    per-subject grade (marks-scale, header "English G") and the overall
    final grade (percentage-scale, bare header "Grade"); "Total" covers
    both a per-subject total and the aggregate total. Voting these
    against each other in 5B would falsely flag the correct one just for
    using different threshold numbers / a different SUM shape.

    variant = "aggregate"  -> label matched via the full alias WORD
                              itself (e.g. label IS "Grade"/"Total") --
                              a standalone, sheet-level column.
    variant = "component"  -> label matched via a short single-letter
                              shorthand (e.g. "G"/"T" from the W/O/T/G
                              sub-header) -- a per-subject column.
    variant = "fuzzy"      -> matched only by fuzzy fallback; treated
                              like "component" (conservative default).

    `label` may be None (a column with no header text at all, e.g. a
    stray trailing column) -> always ("Unrecognized", None).
    """
    if not label:
        return "Unrecognized", None
    if keyword_dict is None:
        keyword_dict = load_keyword_dict()

    norm = label.lower()

    # 1. Longest substring alias wins (handles "final total" vs "total").
    #    A full-word alias match means the header itself IS that word
    #    (possibly with minor extra text) -> aggregate-level column.
    candidates = [
        (len(alias), type_name)
        for type_name, aliases in keyword_dict.items()
        for alias in aliases
        if len(alias) >= SUBSTRING_MIN_LEN and alias in norm
    ]
    if candidates:
        candidates.sort(key=lambda x: -x[0])
        return candidates[0][1], "aggregate"

    # 2. Exact short-token match (single letters like "t"/"g" from W/O/T/G)
    #    -> always a per-subject component column.
    tokens = _tokenize(label)
    for token in tokens:
        for type_name, aliases in keyword_dict.items():
            if token in aliases:
                return type_name, "component"

    # 3. Fuzzy fallback for longer, differently-worded/misspelled headers.
    all_aliases = [(alias, t) for t, aliases in keyword_dict.items() for alias in aliases if len(alias) >= SUBSTRING_MIN_LEN]
    alias_list = [a for a, _ in all_aliases]
    for token in tokens:
        if len(token) < SUBSTRING_MIN_LEN:
            continue
        match = difflib.get_close_matches(token, alias_list, n=1, cutoff=FUZZY_CUTOFF)
        if match:
            return dict(all_aliases)[match[0]], "fuzzy"

    return "Unrecognized", None


def classify_all_columns(column_map, keyword_dict=None):
    """column_map: {col_index: ColumnInfo} from headers.build_column_map.
    Returns {col_index: (type_name, variant)}."""
    if keyword_dict is None:
        keyword_dict = load_keyword_dict()
    return {idx: classify_column(info.label, keyword_dict) for idx, info in column_map.items()}
