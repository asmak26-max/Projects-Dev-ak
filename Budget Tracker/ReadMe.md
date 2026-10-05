# Personal-Budget-Tracker-CORE

# 💰 Personal Budget Tracker

A simple **Java console application** that helps users track their income, expenses, available money, and savings goal.

## 📌 About

The **Personal Budget Tracker** allows users to manage a basic personal budget through a menu-driven console interface.

Users can:
- Add income
- Add expenses by category
- View their budget summary
- Set a savings goal
- Check how much money is left
- Check progress toward their savings goal

## ⚙️ How It Works

The application uses a simple menu:

```text
1. Add Income
2. Add Expense
3. View Budget Summary
4. Set Savings Goal
5. Exit
```

### Example

Suppose the user enters:

```text
Income = $5000
Food = $1000
Travel = $500
Savings Goal = $2000
```

The application calculates:

```text
Money Left = $3500
```

and shows the user's progress toward the savings goal.

## 🧠 Main Concepts Used

| Concept | Usage |
|---|---|
| `Scanner` | Takes user input |
| `HashMap` | Stores income and expenses |
| Methods | Separates different operations |
| `if-else` | Makes decisions |
| `switch` | Handles menu choices |
| `while` loop | Keeps the application running |
| `getOrDefault()` | Gets existing amount or starts from `0` |
| `Map.Entry` | Reads stored expense categories |

## 🗂️ Program Structure

- `main()` → Controls the application and displays the menu
- `addIncome()` → Adds income
- `addExpense()` → Adds an expense
- `viewBudgetSummary()` → Displays the budget summary
- `calculateTotalAmount()` → Calculates remaining money
- `setSavingsGoal()` → Sets the savings target

## ▶️ How to Run

### Prerequisite

Install **Java JDK** and verify:

```bash
java -version
javac -version
```

### Save the File

Save the program as:

```text
b1.java
```

### Compile

```bash
javac b1.java
```

### Run

```bash
java b1
```

Then select an option from the menu and follow the instructions.

## 📊 Sample Usage

```text
Welcome to Personal Budget Tracker!

Budget Tracker Menu:
1. Add Income
2. Add Expense
3. View Budget Summary
4. Set Savings Goal
5. Exit

Enter your choice: 1
Enter income amount: $5000
Income added successfully!

Enter your choice: 2
Enter expense category: Food
Enter expense amount: $1000
Expense added successfully!

Enter your choice: 4
Enter savings goal amount: $2000
Savings goal set successfully!

Enter your choice: 3

Budget Summary:
Income Added: $5000
Expenses:
Food: $1000
Total Amount Left: $4000
```

## ⚠️ Current Limitations

This is a **basic console-based learning project**.

- Data is stored only while the program is running.
- Data is lost when the program exits.
- No database is used.
- No graphical interface is included.
- Input validation is basic.
- **If the calculated balance becomes negative due to overspending, `viewBudgetSummary()` currently displays “No budget information available” instead of showing the negative balance.**

## 🎯 Learning Purpose

This mini-project demonstrates how Java's **collections, methods, loops, conditions, user input, and basic program structure** can be combined to build a small real-world application.

## 👤 Project Type

**Java Mini Project — Console Application**
