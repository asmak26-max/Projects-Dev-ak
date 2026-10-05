import os
import shutil
import json

UNDO_LOG = "undo_log.json"

def scan_directory(path, recursive=False):
    """Scan the directory and return a list of files (with or without recursion)."""
    all_files = []
    if recursive:
        for root, _, files in os.walk(path):
            for file in files:
                all_files.append(os.path.join(root, file))  # Full path for safe movement
    else:
        all_files = [os.path.join(path, file) for file in os.listdir(path) if os.path.isfile(os.path.join(path, file))]

    return all_files

def classify_files(file_list):
    """Classify files dynamically based on their extensions."""
    categorized = {}
    
    for file in file_list:
        ext = file.split('.')[-1].lower() if '.' in file else 'no_extension'
        dir_path = os.path.dirname(file)

        if dir_path not in categorized:
            categorized[dir_path] = {}

        if ext not in categorized[dir_path]:
            categorized[dir_path][ext] = []
        
        categorized[dir_path][ext].append(file)

    return categorized

def move_files(categorized_files):
    """Move files with smart folder creation logic."""
    undo_data = {}

    for dir_path, extensions in categorized_files.items():
        misc_folder = os.path.join(dir_path, "Misc_Files")
        os.makedirs(misc_folder, exist_ok=True)

        for ext, files in extensions.items():
            if len(files) > 1:
                folder_name = ext.upper() + "_Files"
                folder_path = os.path.join(dir_path, folder_name)
            else:
                folder_path = misc_folder  # Send unique files to 'Misc_Files'

            os.makedirs(folder_path, exist_ok=True)

            for file in files:
                new_path = os.path.join(folder_path, os.path.basename(file))
                undo_data[new_path] = file
                shutil.move(file, new_path)

    with open(UNDO_LOG, 'w') as log_file:
        json.dump(undo_data, log_file, indent=4)

    print("Files organized successfully! Undo log created.")

def undo_last_action():
    """Restore files to their original locations using the undo log."""
    if not os.path.exists(UNDO_LOG):
        print("No undo log found. Undo not possible.")
        return

    try:
        with open(UNDO_LOG, 'r') as log_file:
            undo_data = json.load(log_file)

        for new_path, original_path in undo_data.items():
            if os.path.exists(new_path):
                os.makedirs(os.path.dirname(original_path), exist_ok=True)
                shutil.move(new_path, original_path)

        os.remove(UNDO_LOG)
        print("Undo successful! Files restored to their original locations.")
    
    except Exception as e:
        print(f"Error during undo process: {e}")

def organize_files(path):
    """Main function to organize files with user confirmation for recursion."""
    try:
        recursive_choice = input("Do you want to organize files inside subfolders too? (yes/no): ").strip().lower()
        recursive = recursive_choice == 'yes'

        files = scan_directory(path, recursive)
        if not files:
            print("No files found in the specified directory.")
            return
        
        categorized_files = classify_files(files)
        move_files(categorized_files)
    
    except Exception as e:
        print(f"An error occurred: {e}")

if __name__ == "__main__":
    choice = input("Choose an option: \n1. Organize Files \n2. Undo Last Action \nEnter your choice (1/2): ").strip()

    if choice == '1':
        directory = input("Enter the directory path: ").strip()
        if os.path.exists(directory):
            organize_files(directory)
        else:
            print("The specified directory does not exist.")
    
    elif choice == '2':
        undo_last_action()
    
    else:
        print("Invalid choice. Exiting.")
