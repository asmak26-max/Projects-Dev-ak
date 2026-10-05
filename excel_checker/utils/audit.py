"""
STEP 9/10 — local audit log.

One JSON file per Apply-Fixes action (not one per day -- a teacher could
run Apply Fixes more than once in a day, and each run needs its own
independent keep/auto-delete choice, per Step 9's spec: "ask after EACH
apply action"). Never merged into or downloaded with the corrected
workbook -- this lives only in logs/, read back in-app by Step 10.
"""
import json
from pathlib import Path
from datetime import datetime, timedelta

LOG_DIR = Path(__file__).parent.parent / "logs"
RETENTION_DAYS = 7


def _ensure_dir():
    LOG_DIR.mkdir(parents=True, exist_ok=True)


def write_log(entries, keep_forever, workbook_filename):
    """
    entries: list of (sheet, cell, flag_types, old_formula, new_formula) tuples.
    keep_forever: bool from the user's checkbox at Apply-Fixes time.
    Writes logs/audit_<timestamp>.json and returns its path.
    """
    _ensure_dir()
    now = datetime.now()
    path = LOG_DIR / f"audit_{now:%Y%m%d_%H%M%S_%f}.json"
    payload = {
        "created": now.isoformat(),
        "workbook": workbook_filename,
        "retention": "keep_forever" if keep_forever else "auto_delete",
        "entries": [
            {"sheet": s, "cell": c, "flag_types": ft, "old_formula": old, "new_formula": new}
            for (s, c, ft, old, new) in entries
        ],
    }
    with open(path, "w") as f:
        json.dump(payload, f, indent=2)
    return path


def cleanup_old_logs():
    """
    STEP 9 (startup task): delete any 'auto_delete'-tagged log older than
    RETENTION_DAYS. Never touches 'keep_forever' logs. Run once at app
    startup. Corrupt/unreadable log files are skipped, not deleted --
    a parse failure isn't evidence a file is safe to remove.
    """
    _ensure_dir()
    cutoff = datetime.now() - timedelta(days=RETENTION_DAYS)
    for path in LOG_DIR.glob("audit_*.json"):
        try:
            with open(path) as f:
                data = json.load(f)
            if data.get("retention") == "auto_delete":
                created = datetime.fromisoformat(data["created"])
                if created < cutoff:
                    path.unlink()
        except (json.JSONDecodeError, KeyError, ValueError, OSError):
            continue


def load_all_logs():
    """
    STEP 10: reads every remaining log file into one flat list of rows
    for in-app display (st.dataframe) -- no download.
    """
    _ensure_dir()
    rows = []
    for path in sorted(LOG_DIR.glob("audit_*.json")):
        try:
            with open(path) as f:
                data = json.load(f)
        except (json.JSONDecodeError, OSError):
            continue
        for e in data.get("entries", []):
            rows.append({
                "Timestamp": data.get("created", ""),
                "Workbook": data.get("workbook", ""),
                "Retention": data.get("retention", ""),
                "Sheet": e.get("sheet", ""),
                "Cell": e.get("cell", ""),
                "Flag type(s)": e.get("flag_types", ""),
                "Old formula": e.get("old_formula", ""),
                "New formula": e.get("new_formula", ""),
            })
    return rows
