import java.util.Scanner;
public class ExpenseTrackerLastName {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        displayTitle();

        System.out.print("Enter your name: ");
        String name = scanner.nextLine().trim();

        System.out.print("Enter your budget: ");
        double budget = scanner.nextDouble();

        System.out.print("Enter food expense: ");
        double food = scanner.nextDouble();

        System.out.print("Enter transportation expense: ");
        double transportation = scanner.nextDouble();

        System.out.print("Enter entertainment expense: ");
        double entertainment = scanner.nextDouble();

        System.out.print("Enter other expenses: ");
        double other = scanner.nextDouble();

        double total = calculateTotal(food, transportation, entertainment, other);
        double remaining = budget - total;
        String status = checkBudget(total, budget);
        displayResults(name, total, budget, remaining, status);
        scanner.close();
    }

    static void displayTitle() {
        System.out.println("===== Personal Expense Tracker =====");
    }

    static double calculateTotal(double food, double transportation, double entertainment, double other) {
        return food + transportation + entertainment + other;
    }

    static String checkBudget(double total, double budget) {
        if (total > budget) {
            return "Over budget";
        }
        if (total == budget) {
            return "Budget exactly met";
        }
        return "Within budget";
    }

    static void displayResults(String name, double total, double budget, double remaining, String status) {
        System.out.println();
        System.out.println("Expense summary for " + name + ":");
        System.out.println("Total expenses: " + String.format("%.2f", total));
        System.out.println("Budget: " + String.format("%.2f", budget));
        System.out.println("Remaining budget: " + String.format("%.2f", remaining));
        System.out.println("Status: " + status);
        if (remaining >= 0) {
            System.out.println("Great job, " + name + "! You stayed within your budget.");
        } else {
            System.out.println("Watch spending closely, " + name + ". You exceeded your budget.");
        }
    }
}
