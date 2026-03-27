package mar_27_2026;
import java.util.Scanner;
public class act4_StudentPaymentSystemwithValidationCounter {


	    public static void main(String[] args) {
	        Scanner input = new Scanner(System.in);

	        String studentName, studentId;
	        double totalTuition, balance;
	        int choice;
	        int transactionCounter = 0;
	        boolean isDiscountApplied = false;

	        System.out.print("Enter Student Name: ");
	        studentName = input.nextLine();
	        System.out.print("Enter Student ID: ");
	        studentId = input.nextLine();
	        System.out.print("Enter Total Tuition Fee: ");
	        totalTuition = input.nextDouble();

	        balance = totalTuition;

	        do {
	            System.out.print("\n===========================================");
                System.out.print("\n==============  PAYMENT MENU ==============");
                System.out.println("\n===========================================");
	            System.out.println("1. Pay Tuition");
	            System.out.println("2. Check Balance");
	            System.out.println("3. Apply Discount");
	            System.out.println("4. Exit");
	            System.out.print("Select an option: ");
	            choice = input.nextInt();

	            switch (choice) {
	                case 1:
	                    if (balance <= 0) {
	                        System.out.println("No remaining balance. You are already fully paid.");
	                    } else {
	                        System.out.print("Enter payment amount: ");
	                        double payment = input.nextDouble();

	                        if (payment < 0) {
	                            System.out.println("Invalid Payment: Cannot enter a negative amount.");
	                        } else if (payment > balance) {
	                            System.out.println("Invalid Payment");
	                        } else {
	                            balance -= payment;
	                            System.out.println("Payment successful!");
	                            transactionCounter++;
	                        }
	                    }
	                    break;

	                case 2:
	                    System.out.printf("Remaining Balance: Php %.2f\n", balance);
	                    if (balance <= 0) {
	                        System.out.println("No remaining balance");
	                    }
	                    transactionCounter++; 
	                    break;

	                case 3:
	                    if (isDiscountApplied) {
	                        System.out.println("Discount has already been applied. Cannot apply multiple discounts.");
	                    } else {
	                        System.out.println("Are you a:");
	                        System.out.println("1. Regular Student");
	                        System.out.println("2. Scholar");
	                        System.out.print("Select type (1 or 2): ");
	                        int studentType = input.nextInt();

	                        // Nested if for discount logic
	                        if (studentType == 2) {
	                            double discountAmount = totalTuition * 0.20;
	                            
	                            // Prevent negative balance
	                            if (balance - discountAmount < 0) {
	                                balance = 0; 
	                            } else {
	                                balance -= discountAmount;
	                            }
	                            System.out.println("20% Scholar discount applied.");
	                            isDiscountApplied = true;
	                        } else if (studentType == 1) {
	                            System.out.println("Regular student: No discount applied.");
	                            isDiscountApplied = true;
	                        } else {
	                            System.out.println("Invalid input. Returning to menu.");
	                        }
	                    }
	                    break;

	                case 4:
	                	System.out.print("\n===========================================");
	                    System.out.print("\n============== FINAL SUMMARY ==============\n");
	                    System.out.println("\n===========================================");
	                    System.out.println("Student Name: " + studentName);
	                    System.out.println("Total Transactions: " + transactionCounter);
	                    System.out.printf("Final Balance: Php %.2f\n", balance);
	                    System.out.println("System exiting. Have a great day!");
	                    break;

	                default:
	                    System.out.println("Invalid option. Please choose between 1 and 4.");
	            }

	        } while (choice != 4);

	        input.close();
	    }
	}

