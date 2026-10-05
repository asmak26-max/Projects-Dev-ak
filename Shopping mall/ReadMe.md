# Shopping-mall_CORE

🛒 Mini Shopping Mall — C Mini Project

A simple menu-driven C program that simulates a mini shopping mall.

It allows the user to choose:

- 🛍️ Shopping
  - Grocery & Staples
  - Home Appliances
- 🍔 Food Court

The program takes quantities, calculates prices, and displays the final bill.

---

🎯 Main Idea 

Start
  ↓
Choose
  ├── 1. Shopping
  │     ├── Grocery
  │     └── Home Appliances
  │
  └── 2. Food Court
        ├── Burger
        ├── Pizza
        ├── Fried Chicken
        └── French Fries

The program mainly demonstrates switch-case, loops, arrays, structures and functions.

---

🔄 How the Program Works

1. Main Menu

printf("1 : Shopping");
printf("2 : Food Court");
scanf("%d", &cat);

User selects a category.

A "switch(cat)" decides where to go.

---

2. Shopping → Grocery

The user enters quantities of:

Item| Price
Sugar| Rs. 100
Tea| Rs. 15
Coffee| Rs. 50
Rice| Rs. 150
Wheat| Rs. 160

Cost is calculated as:

item cost = price × quantity

Then:

Total = Sugar + Tea + Coffee + Rice + Wheat

---

3. Shopping → Home Appliances

The program stores appliance information using a structure:

struct HomeAppliance
{
    char name[50];
    float price;
};

Example:

Refrigerator       Rs. 1000
Washing Machine    Rs. 800
Air Conditioner    Rs. 1200
Television         Rs. 700
Microwave Oven     Rs. 300

An array of structures stores all 5 appliances.

struct HomeAppliance appliances[5];

Two arrays track:

selection[]   // Was item selected?
quantity[]    // How many?

A "for" loop calculates the final cost.

---

🍔 Food Court

The food court uses separate functions.

"displayMenu()"

Displays the food menu.

void displayMenu(void)

"calculateTotalBill()"

Calculates the total:

Burger × 5
Pizza × 8
Fried Chicken × 7
French Fries × 3

"foodCourt()"

Controls the complete food-ordering process.

A "do-while" loop repeatedly shows the menu until the user chooses "5".

---

🧠 Important C Concepts Used

Concept| Where Used
"switch"| Main menu and food menu
Nested "switch"| Shopping → Grocery/Appliances
"if-else"| Appliance selection validation
"for" loop| Display/calculation of appliances
"do-while"| Repeated menu operations
Arrays| Quantities and selections
Structure| Appliance name + price
Functions| Food court operations
Pointers| "scanf("%d", &value)"
"float"| Appliance/food prices

---

🎤 Some Questions 

1. Why did you use "switch"?

Because the program has fixed menu choices such as 1, 2, 3, etc.

---

2. Why use a nested "switch"?

First:

Shopping / Food Court

Then inside Shopping:

Grocery / Home Appliances

So a second "switch" handles the sub-menu.

---

3. Why use a structure?

A structure groups different types of data belonging to one appliance:

name  → string
price → float

---

4. Why use an array of structures?

Instead of creating separate variables for every appliance, one array stores multiple appliance records.

---

5. Why use "do-while"?

The menu should execute at least once before checking whether the user wants to exit.

---

6. What does "&" mean in "scanf()"?

Example:

scanf("%d", &choice);

"&choice" gives the memory address where "scanf()" should store the input.

---

7. What is a function?

A function is a reusable block of code.

Example:

calculateTotalBill()

It receives quantities and returns the calculated bill.

---

8. Why are function declarations written before "main()"?

So the compiler knows about the functions before they are called inside "main()".

---

9. What does "return 0" mean?

It indicates that the program ended successfully.

---

10. What is the overall logic?

Input → Decision → Calculation → Output

Take choice
   ↓
Select category
   ↓
Take quantity
   ↓
Calculate price
   ↓
Display bill

---

⚠️ Small Limitations in Current Code

For interview honesty, know these:

- Input validation for non-numeric input is not handled.
- Negative quantities are not prevented.
- Money is stored/calculated using "float".
- Selecting the same appliance again replaces its quantity instead of adding to it.
- The project is console-based; it does not use files/database for storing orders.

---

🗣️ 30-Second  Explanation

«“I developed a menu-driven Mini Shopping Mall application in C. It has Shopping and Food Court modules. Shopping contains groceries and home appliances, while the food court handles different food items. I used switch-case for menus, structures and arrays for appliance data, loops for repeated operations, and functions for modular food-bill calculation. The program accepts quantities, calculates item-wise costs, and generates the final bill.”»

---

⭐ Important 

switch       → menu decisions
nested switch→ sub-menu
struct       → appliance data
array        → multiple items
for          → process items
do-while     → repeat menu
function     → reusable code
scanf + &    → take input
