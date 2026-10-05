# File-Organizer-Core

# 📂 Smart File Organizer

A simple **Python automation tool** that automatically organizes files into folders based on their file extensions.

## 💡 What Problem Does It Solve?

When a folder contains many mixed files like:

```text
photo.jpg
resume.pdf
notes.txt
song.mp3
```

finding files becomes messy.

This program **automatically scans, categorizes, and moves files** into suitable folders.

## ✨ Main Features

- Organizes files by extension
- Can also scan **subfolders**
- Creates folders automatically
- Sends files with unique extensions to `Misc_Files`
- Creates an **undo log**
- Can restore the previous organization

## ⚙️ How It Works — ELI5

Think of the program as a **digital librarian**:

```text
Folder
  ↓
Scan files
  ↓
Check extensions
  ↓
Group similar files
  ↓
Create folders
  ↓
Move files
  ↓
Save undo information
```

### Example

Before:

```text
MyFolder/
├── photo1.jpg
├── photo2.jpg
├── notes.txt
├── resume.pdf
└── song.mp3
```

After:

```text
MyFolder/
├── JPG_Files/
│   ├── photo1.jpg
│   └── photo2.jpg
├── Misc_Files/
│   ├── notes.txt
│   ├── resume.pdf
│   └── song.mp3
```

**Why?**

- More than one `.jpg` → `JPG_Files`
- Only one `.txt`, `.pdf`, `.mp3` → `Misc_Files`

## 🧠 Main Functions

| Function | Simple Meaning |
|---|---|
| `scan_directory()` | Finds files |
| `classify_files()` | Groups files by extension |
| `move_files()` | Creates folders and moves files |
| `undo_last_action()` | Restores moved files |
| `organize_files()` | Controls the complete process |

## 🛠️ Python Concepts Used

- `os` → file/folder operations
- `shutil` → moving files
- `json` → storing undo information
- Functions → modular code
- `if-else` → decision making
- Dictionary → storing categorized data
- `os.walk()` → recursive folder scanning
- Exception handling → prevents the program from crashing easily

## 🔄 Undo System

After moving files, the program creates:

```text
undo_log.json
```

It stores:

```text
New Location → Original Location
```

When **Undo** is selected, the program reads this file and moves the files back.

## ▶️ How to Run

### 1. Requirement

Install **Python 3**.

Check:

```bash
python --version
```

### 2. Save the Code

Save it as:

```text
file_organizer.py
```

### 3. Run

```bash
python file_organizer.py
```

### 4. Choose

```text
1. Organize Files
2. Undo Last Action
```

For organization, enter the folder path and choose whether subfolders should also be scanned.

## 🎯 Explanation

**“This is a Python file-organization automation project. It scans a directory, classifies files by extension, dynamically creates suitable folders, and moves the files. It also maintains a JSON-based undo log that stores original and new paths, allowing the last organization operation to be reversed.”**

## ⚠️ Current Limitations

- It is a **command-line application**.
- Only the **last organization action** is tracked through `undo_log.json`.
- No graphical interface is included.
- Basic input validation is used.
- Files with the same extension are grouped based on the current folder structure.

## 🚀 Possible Future Improvements

- GUI using Tkinter
- Preview changes before moving files
- Multiple undo levels
- File-name conflict handling
- Detailed operation history
- Custom categories such as Images, Documents, Videos, etc.

## 📌 One-Line Summary

**A Python automation tool that organizes mixed files by extension and provides an undo feature to restore the previous organization.**
