#include <stdio.h>
#include <stdlib.h>

/* Function declarations */
void foodCourt(void);
void displayMenu(void);
float calculateTotalBill(int burgerQty, int pizzaQty,
                         int friedChickenQty, int friesQty);

int main(void)
{
    int cat;
    int shop;

    printf("========================================\n");
    printf("       MINI SHOPPING MALL\n");
    printf("========================================\n");

    printf("1 : Shopping\n");
    printf("2 : Food Court\n");
    printf("Enter choice number: ");
    scanf("%d", &cat);

    switch (cat)
    {
        /* ---------------- SHOPPING ---------------- */
        case 1:
        {
            printf("\n========================================\n");
            printf("              SHOPPING\n");
            printf("========================================\n");

            printf("1 : Grocery and Staples\n");
            printf("2 : Home Appliances\n");
            printf("Enter choice number: ");
            scanf("%d", &shop);

            switch (shop)
            {
                /* ------------ GROCERY ------------ */
                case 1:
                {
                    int sugar, tea, coffee, rice, wheat;
                    int s, t, c, r, w;
                    int grocery_total;

                    printf("\n----------------------------------------\n");
                    printf("              GROCERIES\n");
                    printf("----------------------------------------\n");

                    printf("Sugar/g (Rs. 100) : ");
                    scanf("%d", &sugar);

                    printf("Tea powder/g (Rs. 15) : ");
                    scanf("%d", &tea);

                    printf("Coffee powder/g (Rs. 50) : ");
                    scanf("%d", &coffee);

                    printf("Rice/kg (Rs. 150) : ");
                    scanf("%d", &rice);

                    printf("Wheat/kg (Rs. 160) : ");
                    scanf("%d", &wheat);

                    /* Calculate individual costs */
                    s = 100 * sugar;
                    t = 15 * tea;
                    c = 50 * coffee;
                    r = 150 * rice;
                    w = 160 * wheat;

                    /* Calculate total */
                    grocery_total = s + t + c + r + w;

                    printf("\n----------------------------------------\n");
                    printf("              YOUR BILL\n");
                    printf("----------------------------------------\n");

                    printf("Sugar  : %d Rs\n", s);
                    printf("Tea    : %d Rs\n", t);
                    printf("Coffee : %d Rs\n", c);
                    printf("Rice   : %d Rs\n", r);
                    printf("Wheat  : %d Rs\n", w);

                    printf("----------------------------------------\n");
                    printf("Total Grocery Price : %d Rs\n", grocery_total);
                    printf("----------------------------------------\n");

                    printf("\nProduct       Quantity       Price\n");
                    printf("----------------------------------------\n");
                    printf("Sugar         %d g            %d\n", sugar, s);
                    printf("Tea           %d g            %d\n", tea, t);
                    printf("Coffee        %d g            %d\n", coffee, c);
                    printf("Rice          %d kg           %d\n", rice, r);
                    printf("Wheat         %d kg           %d\n", wheat, w);

                    printf("----------------------------------------\n");
                    printf("Grocery Total Price : %d Rs\n", grocery_total);

                    break;
                }

                /* -------- HOME APPLIANCES -------- */
                case 2:
                {
                    struct HomeAppliance
                    {
                        char name[50];
                        float price;
                    };

                    /* Array of home appliances */
                    struct HomeAppliance appliances[5] =
                    {
                        {"Refrigerator", 1000.0},
                        {"Washing Machine", 800.0},
                        {"Air Conditioner", 1200.0},
                        {"Television", 700.0},
                        {"Microwave Oven", 300.0}
                    };

                    int selection[5] = {0};
                    int quantity[5] = {0};
                    int choice;
                    float totalCost = 0.0;

                    printf("\n========================================\n");
                    printf("          HOME APPLIANCES\n");
                    printf("========================================\n");

                    do
                    {
                        printf("\nList of Home Appliances:\n");
                        printf("----------------------------------------\n");

                        for (int i = 0; i < 5; i++)
                        {
                            printf("%d. %s - Rs. %.2f\n",
                                   i + 1,
                                   appliances[i].name,
                                   appliances[i].price);
                        }

                        printf("0. Checkout\n");
                        printf("----------------------------------------\n");

                        printf("Enter item number to select (1-5)\n");
                        printf("OR Enter 0 to checkout: ");
                        scanf("%d", &choice);

                        if (choice >= 1 && choice <= 5)
                        {
                            int qty;

                            printf("Enter the quantity: ");
                            scanf("%d", &qty);

                            selection[choice - 1] = 1;
                            quantity[choice - 1] = qty;
                        }
                        else if (choice == 0)
                        {
                            printf("\n========================================\n");
                            printf("            SHOPPING BILL\n");
                            printf("========================================\n");

                            totalCost = 0.0;

                            for (int i = 0; i < 5; i++)
                            {
                                if (selection[i] == 1)
                                {
                                    float cost =
                                        appliances[i].price * quantity[i];

                                    printf("%s - Quantity: %d - Cost: Rs. %.2f\n",
                                           appliances[i].name,
                                           quantity[i],
                                           cost);

                                    totalCost += cost;
                                }
                            }

                            printf("----------------------------------------\n");
                            printf("Total Cost: Rs. %.2f\n", totalCost);
                            printf("Thank you for shopping with us!\n");
                        }
                        else
                        {
                            printf("Invalid selection! Please try again.\n");
                        }

                    } while (choice != 0);

                    break;
                }

                default:
                    printf("\nInvalid shopping choice.\n");
                    break;
            }

            break;
        }

        /* ---------------- FOOD COURT ---------------- */
        case 2:
            foodCourt();
            break;

        default:
            printf("\nNo matching choice.\n");
            break;
    }

    return 0;
}


/* Function to display the food court menu */
void displayMenu(void)
{
    printf("\n========================================\n");
    printf("            FOOD COURT MENU\n");
    printf("========================================\n");

    printf("1. Burger - Rs. 5\n");
    printf("2. Pizza - Rs. 8\n");
    printf("3. Fried Chicken - Rs. 7\n");
    printf("4. French Fries - Rs. 3\n");
    printf("5. Exit\n");
}


/* Function to calculate total food bill */
float calculateTotalBill(int burgerQty,
                         int pizzaQty,
                         int friedChickenQty,
                         int friesQty)
{
    float burgerCost = burgerQty * 5.0;
    float pizzaCost = pizzaQty * 8.0;
    float friedChickenCost = friedChickenQty * 7.0;
    float friesCost = friesQty * 3.0;

    float totalBill = burgerCost +
                      pizzaCost +
                      friedChickenCost +
                      friesCost;

    return totalBill;
}


/* Food court program */
void foodCourt(void)
{
    int burgerQty = 0;
    int pizzaQty = 0;
    int friedChickenQty = 0;
    int friesQty = 0;
    int choice;

    printf("\n========================================\n");
    printf("            WELCOME TO FOOD COURT\n");
    printf("========================================\n");

    do
    {
        displayMenu();

        printf("\nEnter your choice (1-4): ");
        printf("\nEnter 5 to know your bill: ");
        scanf("%d", &choice);

        switch (choice)
        {
            case 1:
                printf("Enter quantity of burgers: ");
                scanf("%d", &burgerQty);
                break;

            case 2:
                printf("Enter quantity of pizzas: ");
                scanf("%d", &pizzaQty);
                break;

            case 3:
                printf("Enter quantity of fried chickens: ");
                scanf("%d", &friedChickenQty);
                break;

            case 4:
                printf("Enter quantity of french fries: ");
                scanf("%d", &friesQty);
                break;

            case 5:
                printf("\nThank you for your order!\n");
                break;

            default:
                printf("Invalid choice. Please try again.\n");
                break;
        }

    } while (choice != 5);

    {
        float totalBill =
            calculateTotalBill(
                burgerQty,
                pizzaQty,
                friedChickenQty,
                friesQty
            );

        printf("\n========================================\n");
        printf("Total Bill: Rs. %.2f\n", totalBill);
        printf("========================================\n");
    }
}
