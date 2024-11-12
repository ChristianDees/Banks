/*
// Authors: Christian Dees & Aitiana Mondragon & Cristina Rivera
// Date: November 12, 2024
// Course: CS 3331 - Advanced Object-Oriented Programming - Fall 2024
// Instructor: Dr. Bhanukiran Gurijala
// Assignment: Programming Assignment 2 (Project Part 2)
// Lab Description: This lab is meant to demonstrate our knowledge in object-oriented concepts such as inheritance, polymorphism, UML diagrams, interfaces, design patterns, and more through coding our own implementation of a bank system of which deposits, withdraws, transfer, pays, and generates various files. This lab also included concepts of logging, testing with JUnit, debugging, file reading, error handling and JavaDoc.
// Honesty Statement: We affirm that we have completed this assignment entirely on our own, without any assistance from outside sources, including peers, experts, online resources, or other means. All code and ideas were that of our own work, and we have followed proper academic integrity.
*/
import java.util.Scanner;
import static java.lang.System.out;

public class ManagerInterface extends UserInterface{

    /**
     * Handle the interaction for a manager.
     *
     * @param scan The scanner object to continue taking input.
     * @param fh   File handler object to read/write files.
     */
    public void handleManager(Scanner scan, FileHandler fh) {
        int attempts = 0;
        // three attempts
        while(attempts < 3){
            if (this.leave()) return;
            // provide options
            out.print("A. Inquire accounts by customer name.\nB. Inquire account by type/number.\nC. Perform transaction from file.\nD. Generate Bank Statement for Customer's Account. \nE. Generate Bank Statement for all Customer Accounts.\n> ");
            String input = scan.nextLine().trim().toLowerCase();
            if (logout(input)) return;
            Manager manager = new Manager("fake", "fake");
            switch (input) {
                case "a":
                    getUserName(scan, true, true, fh);
                    break;
                case "b":
                    inquireByAccount(scan, fh);
                    break;
                case "c":
                    System.out.print("Enter the filename:\n> ");
                    String filename = scan.nextLine().trim().toLowerCase();
                    manager.transactFromFile(filename);
                    break;
                case "d":
                    Customer customerOneTrans = getUserName(scan, false, false, fh);
                    getTimeRange(scan, customerOneTrans, fh, false, "BankStatements", "Statement");
                    break;
                case "e":
                    Customer customerAllTrans = getUserName(scan, false, false, fh);
                    getTimeRange(scan, customerAllTrans, fh, true, "BankStatements", "Statement");
                    break;
                default:
                    // error logging
                    fh.appendLog("EPMB_Error_Log", "Manager attempted to inquire about an account. Reason for failure: Invalid option for how to inquire for account.");
                    out.println("Invalid option. Please choose 'A' or 'B'.");
                    attempts++;
            }
        }
        out.println("Too many attempts. Please log in again.\n");
    }

    /**
     * Query user for the type of account to handle with.
     *
     * @param scan          The scanner object to continue taking input.
     * @return              The account type if it exists.
     */
    private String getAccountType(Scanner scan, FileHandler fh) {
        // 3 attempts
        for (int attempts = 0; attempts < 3; attempts++) {
            out.print("What is the account type? (checking/savings/credit):\n> ");
            String accType = scan.nextLine().trim();
            if (logout(accType)) return null;
            // get type of account the user entered
            if ("checking".equalsIgnoreCase(accType) || "savings".equalsIgnoreCase(accType) || "credit".equalsIgnoreCase(accType)){
                fh.appendLog("EPMB_Transactions", "Manager accessed a " + accType + " account.");
                return accType;
            }
            // error logging
            out.println("Invalid account type. Please enter 'checking', 'savings', or 'credit'.");
            fh.appendLog("EPMB_Error_Log", "Manager attempted to enter account type. Reason for failure: Attempt to access an invalid account type.");
        }
        return null;
    }

    /**
     * Query user for an account number and return the respective account.
     *
     * @param scan          The scanner object to continue taking input.
     * @return              The account that has the provided account type and account number.
     */
    private static Account getAccountInfo(Scanner scan, String accType, FileHandler fh) {
        // for three times
        for (int attempts = 0; attempts < 3; attempts++) {
            out.print("What is the account number?\n> ");
            String input = scan.nextLine().trim();
            if (logout(input)) return null;
            try {
                // access account based on type and number
                int accNum = Integer.parseInt(input);
                TransactionInterface th = new TransactionInterface();
                Account account = th.getAccount(accType, accNum);
                if (account != null){
                    fh.appendLog("EPMB_Transactions", "Manager accessed account " + account.getType() + " " + "[Account Number:" + account.getAccountNumber() + "]");
                    return account;
                } else {
                    // error logging
                    fh.appendLog("EPMB_Error_Log", "Manager attempted to access an account. Reason for failure: Attempt to access a nonexistent account.");
                    out.println("That account does not exist.");
                }
            } catch (NumberFormatException e) {
                // error logging
                fh.appendLog("EPMB_Error_Log", "Manager attempted to access an account. Reason for failure: Invalid format of account number.");
                out.println("Invalid input. Please enter a valid account number.");
            }
        }
        return null;
    }

    /**
     * Query user for type of account and print the account information.
     *
     * @param scan The scanner object to continue taking input.
     */
    private void inquireByAccount(Scanner scan, FileHandler fh) {
        String accType = getAccountType(scan, fh);
        if (accType != null) {
            Account account = getAccountInfo(scan, accType, fh);
            if (account != null) {
                account.printAccount(true, true);
            }
        }
    }
}
