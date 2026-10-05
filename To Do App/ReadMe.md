# To-Do-app_CORE

✅ To-Do List Application

A simple C++ command-line application for managing daily tasks.

💡 What Does It Do?

The program lets the user:

- Add a task
- View all tasks
- Mark a task as completed
- Exit the application

Example:

1. [ ] do homework
2. [ ] play
3. [X] eat
4. [ ] paint

"[ ]" = incomplete
"[X]" = completed

---

🧠  How It Works

Think of it as a small digital notebook.

Start
  ↓
Show Menu
  ↓
User chooses option
  ↓
Add / View / Complete
  ↓
Repeat
  ↓
Quit

1. Store Tasks

Each task has:

struct Task {
    std::string description;
    bool completed;
};

So every task contains:

Task name + Completed/Not completed

All tasks are stored in:

vector<Task> tasks;

---

2. Add Task

The user enters a task description.

Example:

do homework

A new "Task" is created with:

completed = false;

Then it is added using:

tasks.push_back(task);

---

3. List Tasks

The program loops through the vector:

for (size_t i = 0; i < tasks.size(); ++i)

and displays each task with:

[ ] → pending
[X] → completed

---

4. Complete a Task

The user enters a task number.

Example:

Enter task number: 3

The program uses:

tasks[taskNumber - 1].completed = true;

"-1" is needed because users count from 1, while vector indexes start from 0.

---

🗂️ Main Components

Component| Purpose
"Task"| Stores task information
"vector<Task>"| Stores all tasks
"switch"| Handles menu choices
"do-while"| Keeps menu running
"getline()"| Reads complete task text
"push_back()"| Adds task to vector
"fstream"| Intended for file handling

---

🛠️ C++ Concepts Used

- Structures ("struct")
- "vector"
- Strings
- Boolean values
- "switch-case"
- "do-while" loop
- "for" loop
- Functions from standard library
- File streams
- Input validation

---

▶️ How to Run

1. Save the Code

Save as:

todo.cpp

2. Compile

Using g++:

g++ todo.cpp -o todo

3. Run

Linux/macOS:

./todo

Windows:

todo.exe

---

📊 Sample Flow

To-Do List Application
1. Add Task
2. List Tasks
3. Mark Task as Completed
4. Quit

Enter choice: 1
Enter task description: do homework
Task added!

Enter choice: 3
Enter task number: 1
Task marked as completed!

Enter choice: 2

Tasks:
1. [X] do homework

---

⚠️ Important Current Limitations

1. File persistence is NOT actually implemented

The code contains:

std::ofstream outputFile("todolist.txt");

but it never writes the tasks into the file.

So although "todolist.txt" is opened, the task list is not saved.

Therefore:

Run program
   ↓
Add tasks
   ↓
Quit
   ↓
Run again
   ↓
Tasks are gone

2. Edit and Delete are not implemented

The comment says the application should support:

Add + Edit + Delete

but the current code only supports:

Add + List + Mark Completed

3. File is opened at startup

"std::ofstream" can create/truncate the file, but since nothing is written, it does not provide useful persistence.

---

🚀 Possible Improvements

- Save tasks to "todolist.txt"
- Load tasks when the program starts
- Add Edit Task
- Add Delete Task
- Add task priority/deadline
- Improve input validation
- Use functions instead of putting everything in "main()"

---

🎤 30-Second Explanation

«“I created a C++ command-line To-Do List application using a "struct" to represent each task and a "vector" to store multiple tasks. The user can add tasks, display them, and mark them as completed. A "switch-case" handles the menu, while loops keep the application running until the user exits. I also explored file handling with "ofstream", although persistence is not fully implemented in the current version.”»

---

❓ Some Questions

Why use "struct Task"?

To group related data:

description
completed

into one object.

Why use "vector"?

Because the number of tasks can grow dynamically.

Why "taskNumber - 1"?

Users enter task numbers starting from 1, but vector indexing starts from 0.

Why use "getline()"?

It can read a complete sentence, including spaces.

Why use "do-while"?

The menu must appear at least once and continue until the user selects Quit.

What is the biggest limitation?

Tasks are not actually persisted, because the program opens "todolist.txt" but never writes or reads task data from it.

---

🧠 Quick Revision

Task = description + completed
        ↓
vector<Task>
        ↓
Add → List → Complete
        ↓
switch + loops
        ↓
Quit

 summary:
A C++ console To-Do List using "struct", "vector", loops, and "switch-case" to add, display, and complete tasks.
