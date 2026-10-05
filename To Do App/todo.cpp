/* To-Do List Application:
   Create a command-line to-do list application 
   that allows users to add, edit, and delete 
   tasks. You can save tasks in a text file 
   for persistence.
  */
#include <iostream>
#include <fstream>
#include <vector>
#include <string>

struct Task {
    std::string description;
    bool completed;
};

int main() {
    std::vector<Task> tasks;
    char choice;
    Task task; // Declare task outside the switch

    std::ofstream outputFile("todolist.txt");

    do {
        std::cout << "\nTo-Do List Application\n";
        std::cout << "1. Add Task\n";
        std::cout << "2. List Tasks\n";
        std::cout << "3. Mark Task as Completed\n";
        std::cout << "4. Quit\n";
        std::cout << "Enter your choice: ";
        std::cin >> choice;

        switch (choice) {
            case '1':
                std::cin.ignore(); // Clear the newline character in the input buffer
                std::cout << "Enter task description: ";
                std::getline(std::cin, task.description);
                task.completed = false;
                tasks.push_back(task);
                std::cout << "Task added!\n";
                break;

            case '2':
                std::cout << "\nTasks:\n";
                for (size_t i = 0; i < tasks.size(); ++i) {
                    std::cout << (i + 1) << ". ";
                    if (tasks[i].completed)
                        std::cout << "[X] ";
                    else
                        std::cout << "[ ] ";
                    std::cout << tasks[i].description << "\n";
                }
                break;

            case '3':
                int taskNumber;
                std::cout << "Enter the task number to mark as completed: ";
                std::cin >> taskNumber;
                if (taskNumber >= 1 && taskNumber <= tasks.size()) {
                    tasks[taskNumber - 1].completed = true;
                    std::cout << "Task marked as completed!\n";
                } else {
                    std::cout << "Invalid task number.\n";
                }
                break;

            case '4':
                std::cout << "Goodbye!\n";
                break;

            default:
                std::cout << "Invalid choice. Please try again.\n";
        }

    } while (choice != '4');

    outputFile.close();

    return 0;
}


/*output 
To-Do List Application
1. Add Task
2. List Tasks
3. Mark Task as Completed
4. Quit
Enter your choice: 1
Enter task description: do homework
Task added!

To-Do List Application
1. Add Task
2. List Tasks
3. Mark Task as Completed
4. Quit
Enter your choice: 1
Enter task description: play 
Task added!

To-Do List Application
1. Add Task
2. List Tasks
3. Mark Task as Completed
4. Quit
Enter your choice: 1
Enter task description: eat
Task added!

To-Do List Application
1. Add Task
2. List Tasks
3. Mark Task as Completed
4. Quit
Enter your choice: 1
Enter task description: paint
Task added!

To-Do List Application
1. Add Task
2. List Tasks
3. Mark Task as Completed
4. Quit
Enter your choice: 2

Tasks:
1. [ ] do homework
2. [ ] play 
3. [ ] eat
4. [ ] paint

To-Do List Application
1. Add Task
2. List Tasks
3. Mark Task as Completed
4. Quit
Enter your choice: 3
Enter the task number to mark as completed: 3
Task marked as completed!

To-Do List Application
1. Add Task
2. List Tasks
3. Mark Task as Completed
4. Quit
Enter your choice: 2

Tasks:
1. [ ] do homework
2. [ ] play 
3. [X] eat
4. [ ] paint

To-Do List Application
1. Add Task
2. List Tasks
3. Mark Task as Completed
4. Quit
Enter your choice: 4
Goodbye!

Process finished.*/
