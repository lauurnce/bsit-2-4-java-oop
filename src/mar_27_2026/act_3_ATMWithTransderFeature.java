package mar_27_2026;
import java.util.Scanner;
public class act_3_ATMWithTransderFeature {


	    public static void main(String[] args) {
	        Scanner input = new Scanner(System.in);
	        
	        double balance = 9000.0;
	        int choice;
	        double amount;
	        do {
	        	System.out.print("\n==================================");
	        	System.out.print("\n============ ATM MENU ============");
	        	System.out.println("\n==================================");
	            System.out.println("1. Check Balance");
	            System.out.println("2. Deposit");
	            System.out.println("3. Withdraw");
	            System.out.println("4. Exit");
	            System.out.print("Enter your choice: ");
	            choice = input.nextInt();
	            
	            switch (choice) {
	                case 1:
	                    System.out.println("Current Balance: " + balance + " pesos");
	                    break;

	                case 2:
	                    System.out.print("Enter deposit amount: ");
	                    amount = input.nextDouble();
	                    if (amount > 0) {
	                        balance += amount;
	                        System.out.println("Successfully deposited " + amount + " pesos.");
	                    } else {
	                        System.out.println("Invalid amount.");
	                    }
	                    break;

	                case 3:
	                    System.out.print("Enter withdrawal amount: ");
	                    amount = input.nextDouble();

	                 
	                    if (amount > 3000) {
	                        System.out.println("Withdrawal Limit Exceeded");
	                    } else if (amount > balance) {
	                        System.out.println("Insufficient Balance");
	                    } else if (amount <= 0) {
	                        System.out.println("Invalid amount.");
	                    } else {
	                        balance -= amount;
	                        System.out.println("Please take your cash: " + amount + " pesos.");
	                    }
	                    break;

	                case 4:
	                    System.out.println("Thank you for using the ATM. Goodbye!");
	                    break;

	                default:
	                    System.out.println("Invalid choice. Please try again.");
	            }

	        } while (choice != 4);

	        input.close();
	    }
	}

