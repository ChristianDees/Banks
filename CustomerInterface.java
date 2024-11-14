/*
// Authors: Christian Dees & Aitiana Mondragon & Cristina Rivera
// Date: November 12, 2024
// Course: CS 3331 - Advanced Object-Oriented Programming - Fall 2024
// Instructor: Dr. Bhanukiran Gurijala
// Assignment: Programming Assignment 2 (Project Part 2)
// Lab Description: This lab is meant to demonstrate our knowledge in object-oriented concepts such as inheritance, polymorphism, UML diagrams, interfaces, design patterns, and more through coding our own implementation of a bank system of which deposits, withdraws, transfer, pays, and generates various files. This lab also included concepts of logging, testing with JUnit, debugging, file reading, error handling and JavaDoc.
// Honesty Statement: We affirm that we have completed this assignment entirely on our own, without any assistance from outside sources, including peers, experts, online resources, or other means. All code and ideas were that of our own work, and we have followed proper academic integrity.
*/
import java.util.*;
import static java.lang.System.out;

/**
 * Represents the interface between the customer and the system.
 */
public class CustomerInterface extends UserInterface{

    /**
     * Query for the total transactions the user will perform.
     *
     * @param scan          The scanner object to continue taking input.
     * @param fh            File handler object to read/write to files.
     */
    public void handleCustomer(Scanner scan, FileHandler fh) {
        // get their username
        for (int i = 0; i < 3; i++){
            out.print("Are you an existing customer, or a new one?\nA. Existing customer\nB. New customer\n> ");
            String input = scan.nextLine().trim().toLowerCase();
            // check if logout request
            if (logout(input)) return;
            switch (input){
                case "a":
                    // get user's name, then handle that user
                    Customer customer = this.getUserName(scan, true,false, false, fh);
                    handleExistingCustomer(customer, scan, fh);
                    return;
                case "b":
                    // request user to set up their account
                    handleNewCustomer(scan, fh);
                    return;
                default:
                    out.println("Please enter a valid option ('a' or 'b').");
            }
        }
        out.println("Logging out.\n");
    }

    /**
     * Handles an existing customer.
     *
     * @param customer customer that wants to make a bank request.
     * @param scan     scanner object.
     * @param fh       file handler to read/write files.
     */
    private void handleExistingCustomer(Customer customer, Scanner scan, FileHandler fh){
        int attempts = 0;
        String input;
        if (customer != null){
            // three attempts
            while(attempts < 3){
                if (leave()) return;
                out.print("Choose a transaction:\nA. Transaction between single person.\nB. Transaction between two people.\nC. Generate transactions file (for specific account).\nD. Generate all user transactions.\n> ");
                input = scan.nextLine().trim().toLowerCase();
                if (logout(input)) return;
                TransactionInterface transaction = new TransactionInterface();
                switch (input) {
                    case "a":
                        // single transactions
                        transaction.oneAccountTransaction(scan, customer, fh);
                        break;
                    case "b":
                        // two people transaction
                        transaction.twoAccountTransaction(scan, customer, null, false, fh);
                        break;
                    case "c":
                        // generate transactions file
                        getTimeRange(scan, customer, fh, false, "UserTransactions", "Transactions");
                        break;
                    case "d":
                        // generate transactions file
                        getTimeRange(scan, customer, fh, true, "UserTransactions", "Transactions");
                        break;
                    default:
                        // error logging
                        fh.appendLog("EPMB_Error_Log", customer.getFullName() + " [ID:" + customer.getId() + "] attempted to specify transaction parties. Reason for failure: Invalid option when specifying transaction party.");
                        out.println("Invalid option. Please choose 'A' or 'B'.");
                        attempts++;
                }
            }
        }
        out.println("Returning to home...\n");
    }
}
