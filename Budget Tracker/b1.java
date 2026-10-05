import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class b1 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Map<String, Double> budget = new HashMap<>();
        double savingsGoal = 0.0;

        System.out.println("Welcome to Personal Budget Tracker!");

        while (true) {
            System.out.println("\nBudget Tracker Menu:");
            System.out.println("1. Add Income");
            System.out.println("2. Add Expense");
            System.out.println("3. View Budget Summary");
            System.out.println("4. Set Savings Goal");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    addIncome(scanner, budget);
                    break;
                case 2:
                    addExpense(scanner, budget);
                    break;
                case 3:
                    viewBudgetSummary(budget, savingsGoal);
                    break;
                case 4:
                    savingsGoal = setSavingsGoal(scanner);
                    break;
                case 5:
                    System.out.println("Exiting Budget Tracker. Goodbye!");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void addIncome(Scanner scanner, Map<String, Double> budget) {
        System.out.print("Enter income amount: $");
        double amount = scanner.nextDouble();
        scanner.nextLine(); // Consume newline
        budget.put("Income", budget.getOrDefault("Income", 0.0) + amount);
        System.out.println("Income added successfully!");
    }

    private static void addExpense(Scanner scanner, Map<String, Double> budget) {
        System.out.print("Enter expense category: ");
        String category = scanner.nextLine();
        System.out.print("Enter expense amount: $");
        double amount = scanner.nextDouble();
        scanner.nextLine(); // Consume newline
        budget.put(category, budget.getOrDefault(category, 0.0) - amount);
        System.out.println("Expense added successfully!");
    }

    private static void viewBudgetSummary(Map<String, Double> budget, double savingsGoal) {
        System.out.println("\nBudget Summary:");
        
        double totalAmount = calculateTotalAmount(budget);
        
        if (totalAmount >= 0) {
            System.out.println("Income Added: $" + budget.getOrDefault("Income", 0.0));
            System.out.println("Expenses: ");
            for (Map.Entry<String, Double> entry : budget.entrySet()) {
                if (!entry.getKey().equals("Income") && entry.getValue() < 0) {
                    System.out.println(entry.getKey() + ": $" + (-entry.getValue()));
                }
            }
            System.out.println("Total Amount Left: $" + totalAmount);
            
            if (savingsGoal > 0) {
                double remainingToSave = savingsGoal - totalAmount;
                if (remainingToSave <= 0) {
                    System.out.println("You have reached your savings goal!");
                } else {
                    System.out.println("You are " + remainingToSave + " away from reaching your savings goal.");
                }
            } else if(savingsGoal == 0){
                System.out.println("No savings goal has been set.");
            }
        } else {
            System.out.println("No budget information available.");
        }
    }

    private static double calculateTotalAmount(Map<String, Double> budget) {
        double total = budget.getOrDefault("Income", 0.0);
        for (Map.Entry<String, Double> entry : budget.entrySet()) {
            if (!entry.getKey().equals("Income")) {
                total += entry.getValue();
            }
        }
        return total;
    }

    private static double setSavingsGoal(Scanner scanner) {
        System.out.print("Enter savings goal amount: $");
        double goal = scanner.nextDouble();
        scanner.nextLine(); // Consume newline
        System.out.println("Savings goal set successfully!");
        return goal;
    }
}
