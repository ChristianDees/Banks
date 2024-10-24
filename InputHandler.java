/*
// Authors: Christian Dees & Aitiana Mondragon & Crystina Rivera
// Date: October 23, 2024
// Course: CS 3331 - Advanced Object-Oriented Programming - Fall 2024
// Instructor: Dr. Bhanukiran Gurijala
// Assignment: Programming Assignment 1 (Project Part 1)
// Lab Description: This lab is meant to demonstrate our knowledge in object-oriented concepts such as inheritance, polymorphism, UML diagrams, and more through coding our own implementation of a bank system of which deposits, withdraws, transfers, and pays. This lab also included concepts of logging, testing, debugging, file reading, and JavaDoc.
// Honesty Statement: We affirm that we have completed this assignment entirely on our own, without any assistance from outside sources, including peers, experts, online resources, or other means. All code and ideas were that of our own work and we have followed proper academic integrity.
 */
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Represents an object to handle input for the bank
 */
public class InputHandler extends UserInterface{

    /**
     * Displays a welcome message to the user
     */
    public void displayWelcomeMessage(){
        String message = "El Paso Miner's Bank";
        int length = message.length();
        System.out.println("+" + "-".repeat(length + 2) + "+");
        System.out.println("| " + message + " |");
        System.out.println("+" + "-".repeat(length + 2) + "+");
        System.out.println("Type 'exit' to quit.\n");
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
            System.out.print("What is the account type? (checking/savings/credit):\n> ");
            String accType = scan.nextLine().trim();
            if (checkExit(accType)) return null;
            // get type of account the user entered
            if ("checking".equalsIgnoreCase(accType) || "savings".equalsIgnoreCase(accType) || "credit".equalsIgnoreCase(accType)){
                fh.appendLog("EPMB_Transactions", "Manager accessed a " + accType + " account.");
                return accType;
            }
            // error logging
            System.out.println("Invalid account type. Please enter 'checking', 'savings', or 'credit'.");
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
            System.out.print("What is the account number?\n> ");
            String input = scan.nextLine().trim();
            if (checkExit(input)) return null;
            try {
                // access account based on type and number
                int accNum = Integer.parseInt(input);
                TransactionInputHandler th = new TransactionInputHandler();
                Account account = th.getAccount(accType, accNum);
                if (account != null){
                    fh.appendLog("EPMB_Transactions", "Manager accessed account " + account.getType() + " " + "[Account Number:" + account.getAccountNumber() + "]");
                    return account;
                } else {
                    // error logging
                    fh.appendLog("EPMB_Error_Log", "Manager attempted to access an account. Reason for failure: Attempt to access a nonexistent account.");
                    System.out.println("That account does not exist.");
                }
            } catch (NumberFormatException e) {
                // error logging
                fh.appendLog("EPMB_Error_Log", "Manager attempted to access an account. Reason for failure: Invalid format of account number.");
                System.out.println("Invalid input. Please enter a valid account number.");
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

    /**
     * Handle a transaction with one account.
     *
     * @param scan          The scanner object to continue taking input.
     * @param viewBalance   Flag to show balance.
     */
    public Customer getUserName(Scanner scan, boolean viewAccounts, boolean viewBalance, FileHandler fh) {
        // three attempts
        for (int attempts = 0; attempts < 3; attempts++) {
            if(this.leave())return null;
            System.out.print("Enter customer id and name (id, name):\n> ");
            String input = scan.nextLine().trim();
            if (checkExit(input)) return null;
            String[] parts = input.split(",");
            if (parts.length != 2) {
                // error logging
                fh.appendLog("EPMB_Error_Log", "Attempted to ask for user's name. Reason for failure: Invalid format of id, name.");
                if (attempts < 2)
                    System.out.println("Invalid format. Use 'id, name'.");
                continue;
            }
            String formattedName = parts[0].trim() + parts[1].trim().toLowerCase().replace(" ", "");
            if (!checkExit(formattedName)){
                Customer customer = customers.get(formattedName);
                if (customer != null){
                    if (viewAccounts)
                        // print accounts if given
                        customer.viewAccounts(viewBalance);
                    return customer;
                }
                // error logging
                fh.appendLog("EPMB_Error_Log", "Attempted to ask for user's name. Reason for failure: Attempted to get nonexistent customer.");
                System.out.println("There is no account under that name associated with this bank.");
            }
        }
        return null;
    }

    /**
     * Query for user's role to log in
     *
     * @param scan required for input
     */
    private void getUserRole(Scanner scan){
        FileHandler fh = new FileHandler();
        // check if user logged out
        if (logout){
            fh.appendLog("EPMB_Transactions", "User logged out.");
            logout = false;
        }
        System.out.print("Please enter your role (customer/manager):\n> ");
        String role = scan.nextLine().trim().toLowerCase();
        switch (role) {
            case "customer":
                handleCustomer(scan, fh);
                break;
            case "manager":
                handleManager(scan, fh);
                break;
            case "exit":
                exit = true;
                break;
            default:
                // error logging
                fh.appendLog("EPMB_Error_Log", "User attempted to log in. Reason for failure: Invalid option of login.");
                System.out.println("Invalid option. Please choose 'customer' or 'manager'.");
        }
    }

    /**
     * Query for the total transactions the user will perform.
     *
     * @param scan          The scanner object to continue taking input.
     */
    private void handleCustomer(Scanner scan, FileHandler fh) {
        int attempts = 0;
        // get their username
        Customer customer = this.getUserName(scan, false, false, fh);
        if (customer != null){
            // three attempts or they log out
            while(attempts < 3 || logout){
                if (this.leave()) return;
                System.out.print("Choose a transaction:\nA. Transaction between single person.\nB. Transaction between two people.\n> ");
                String input = scan.nextLine().trim().toLowerCase();
                if (checkExit(input)) return;
                TransactionInputHandler transaction = new TransactionInputHandler();
                switch (input) {
                    case "a":
                        transaction.oneAccountTransaction(scan, customer, fh);
                        break;
                    case "b":
                        transaction.TwoAccountTransaction(scan, customer, null, false, fh);
                        break;
                    default:
                        // error logging
                        fh.appendLog("EPMB_Error_Log", customer.getFullName() + " [ID:" + customer.getId() + "] attempted to specify transaction parties. Reason for failure: Invalid option when specifying transaction party.");
                        System.out.println("Invalid option. Please choose 'A' or 'B'.");
                        attempts++;
                }
            }
        }
        System.out.println("Logging out.\n");
    }

    /**
     * Handle the interaction for a manager.
     *
     * @param scan The scanner object to continue taking input.
     */
    private void handleManager(Scanner scan, FileHandler fh) {
        int attempts = 0;
        // three attempts
        while(attempts < 3){
            if (this.leave()) return;
            // provide options
            System.out.print("A. Inquire accounts by customer name and id.\nB. Inquire account by type/number.\n> ");
            String input = scan.nextLine().trim().toLowerCase();
            if (checkExit(input)) return;
            switch (input) {
                case "a":
                    getUserName(scan, true, true, fh);
                    break;
                case "b":
                    inquireByAccount(scan, fh);
                    break;
                default:
                    // error logging
                    fh.appendLog("EPMB_Error_Log", "Manager attempted to inquire about an account. Reason for failure: Invalid option for how to inquire for account.");
                    System.out.println("Invalid option. Please choose 'A' or 'B'.");
                    attempts++;
            }
        }
        System.out.println("Too many attempts. Please log in again.\n");
    }

    /**
     * Ask user for an account and return it.
     *
     * @param scan          The scanner object to continue taking input.
     * @param customer      The customer who is answering the question.
     */
    public Account getAccountForTransaction(Scanner scan, Customer customer, FileHandler fh) {
        // three attempts
        for (int attempts = 0; attempts < 3; attempts++) {
            System.out.print("Specify the account (type, number):\n> ");
            String input = scan.nextLine().trim().toLowerCase();
            String[] parts = input.split(",");
            if (checkExit(input)) return null;
            if (parts.length != 2) {
                // error logging
                fh.appendLog("EPMB_Error_Log", customer.getFullName() + " [ID:" + customer.getId() + "] Reason for failure: Invalid format when specifying account.");
                System.out.println("Invalid format.");
                continue;
            }
            String accType = parts[0].trim();
            try {
                int accNum = Integer.parseInt(parts[1].trim());
                TransactionInputHandler th = new TransactionInputHandler();
                Account account = th.getAccount(accType, accNum);
                if (account != null) {
                    ArrayList<Account> accounts = customer.accounts;
                    if (accounts.contains(account)) {
                        // access account
                        fh.appendLog("EPMB_Transactions", customer.getFullName() + " [ID:" + customer.getId() + "] accessed account " + account.getType() + " " + "[Account Number:" + account.getAccountNumber() + "]");
                        return account;
                    }else {
                        // error logging
                        fh.appendLog("EPMB_Error_Log", customer.getFullName() + " [ID:" + customer.getId() + "] Reason for failure: Attempt to access unauthorized account.");
                        System.out.println("You don't own that account! Please try again.");
                    }
                } else {
                    // error logging
                    fh.appendLog("EPMB_Error_Log", customer.getFullName() + " [ID:" + customer.getId() + "] Reason for failure: Attempt to access nonexistent account.");
                    System.out.println("Account not found. Please try again.");
                }
            } catch (NumberFormatException e) {
                // error logging
                fh.appendLog("EPMB_Error_Log", customer.getFullName() + " [ID:" + customer.getId() + "] Reason for failure: Invalid format when specifying account number.");
                System.out.println("Invalid account number. Please try again.");
            }
        }
        return null;
    }

    /**
     *
     * @return if the user requests to exit or logout
     */
    private boolean leave(){
        return exit || logout;
    }

    /**
     * Ask for input until program is exited
     */
    public void handleInput(){
        Scanner scan = new Scanner(System.in);
        while (!exit) {
            this.getUserRole(scan);
        }
        System.out.println("Exiting...");
    }
}
