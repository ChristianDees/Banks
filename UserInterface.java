/*
// Authors: Christian Dees & Aitiana Mondragon & Crystina Rivera
// Date: October 23, 2024
// Course: CS 3331 - Advanced Object-Oriented Programming - Fall 2024
// Instructor: Dr. Bhanukiran Gurijala
// Assignment: Programming Assignment 1 (Project Part 1)
// Lab Description: This lab is meant to demonstrate our knowledge in object-oriented concepts such as inheritance, polymorphism, UML diagrams, and more through coding our own implementation of a bank system of which deposits, withdraws, transfer, and pays. This lab also included concepts of logging, testing, debugging, file reading, and JavaDoc.
// Honesty Statement: We affirm that we have completed this assignment entirely on our own, without any assistance from outside sources, including peers, experts, online resources, or other means. All code and ideas were that of our own work, and we have followed proper academic integrity.
 */
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import static java.lang.System.out;

public abstract class UserInterface {
    public static boolean logout = false;

    /**
     * Displays a welcome message to the user
     */
    public void displayWelcomeMessage(){
        String message = "El Paso Miner's Bank";
        int length = message.length();
        out.println("+" + "-".repeat(length + 2) + "+");
        out.println("| " + message + " |");
        out.println("+" + "-".repeat(length + 2) + "+");
        out.println("Type 'exit' to quit.\n");
    }

    /**
     * Determine if input is of 'exit' and set global exit variable.
     *
     * @param input         The scanner object to continue taking input.
     * @return              The status of if the program should be exited.
     */
    public static boolean logout(String input) {
        return (input.equalsIgnoreCase("logout") && (logout = true));
    }

    /**
     * Determine if user requests to leave the program.
     *
     * @return if the user requests to exit or logout
     */
    public boolean leave(){
        return logout;
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
            if(leave())return null;
            out.print("Customer's first name: ");
            String firstName = scan.nextLine().trim().toLowerCase();
            if (logout(firstName)) return null;
            out.print("Customer's last name: ");
            String lastName = scan.nextLine().trim().toLowerCase();
            if (logout(lastName)) return null;
            if (firstName.isEmpty() || lastName.isEmpty()) {
                // error logging
                fh.appendLog("EPMB_Error_Log", "Attempted to ask for user's name. Reason for failure: Invalid format of id, name.");
                if (attempts < 2)
                    out.println("Invalid format. Use 'first last'.");
                continue;
            }
            String formattedName = firstName+lastName;
            if (!logout(formattedName)){
                Dictionary<String, Customer> customers = BankDatabase.getInstance().getCustomers();
                Customer customer = customers.get(formattedName);
                if (customer != null){
                    if (viewAccounts)
                        // print accounts if given
                        customer.viewAccounts(viewBalance);
                    return customer;
                }
                // error logging
                fh.appendLog("EPMB_Error_Log", "Attempted to ask for user's name. Reason for failure: Attempted to get nonexistent customer.");
                out.println("There is no account under that name associated with this bank.");
            }
        }
        return null;
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
            out.print("Specify the account (type, number):\n> ");
            String input = scan.nextLine().trim().toLowerCase();
            String[] parts = input.split(",");
            if (logout(input)) return null;
            if (parts.length != 2) {
                // error logging
                fh.appendLog("EPMB_Error_Log", customer.getFullName() + " [ID:" + customer.getId() + "] Reason for failure: Invalid format when specifying account.");
                out.println("Error: Invalid format. Please try again.");
                continue;
            }
            String accType = parts[0].trim();
            try {
                int accNum = Integer.parseInt(parts[1].trim());
                TransactionInterface th = new TransactionInterface();
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
                        out.println("Error: The customer does not own this account. Please try again.");
                    }
                } else {
                    // error logging
                    fh.appendLog("EPMB_Error_Log", customer.getFullName() + " [ID:" + customer.getId() + "] Reason for failure: Attempt to access nonexistent account.");
                    out.println("Error: Account not found. Please try again.");
                }
            } catch (NumberFormatException e) {
                // error logging
                fh.appendLog("EPMB_Error_Log", customer.getFullName() + " [ID:" + customer.getId() + "] Reason for failure: Invalid format when specifying account number.");
                out.println("Error: Invalid account number. Please try again.");
            }
        }
        return null;
    }

    /**
     * Get the time range the user wants the statement within.
     *
     * @param scan              Scanner object for input.
     * @param customer          Which customer is being inquired.
     * @param fh                File handler object,
     * @param allTransactions   Flag for if just one account or all.
     * @param dir               Directory file should be in.
     * @param type              Statement or User Transaction.
     */
    public void getTimeRange(Scanner scan, Customer customer, FileHandler fh, boolean allTransactions, String dir, String type){
        int attempts = 0;
        // get the date from start and end
        Account account = null;
        if (!allTransactions) account = getAccountForTransaction(scan, customer, fh);
        for (int i = 0; i < 3; i++){
            if (account == null && !allTransactions)return;
            out.print("Enter date range (comma separated, mm-dd-yyyy format)\n> ");
            String input = scan.nextLine().trim().toLowerCase();
            if (logout(input)) return;
            String[] dates = input.split(",");
            if (dates.length == 2) {
                String startDate = dates[0].trim();
                String endDate = dates[1].trim();
                SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
                sdf.setLenient(false);
                try {
                    sdf.parse(startDate);
                    sdf.parse(endDate);
                    String filename = dir+"/"+customer.getFullName().replace(" ", "");
                    boolean rc = false;
                    if (allTransactions){
                        filename+=type+".txt";
                        for (Account account1: customer.getAccounts())
                            rc = fh.generateUserTransactionsFile(filename,customer, account1, startDate, endDate);
                    }
                    else{
                        filename+=account.getType()+type+".txt";
                        rc = fh.generateUserTransactionsFile(filename, customer, account, startDate, endDate);
                    }
                    if (rc) System.out.println("\n* * * Successfully exported transactions to " + filename + " * * *\n");
                    break;
                } catch (ParseException e) {
                    out.println("Incorrect date format, please try again.");
                }
            } else if (i<2) {
                out.println("Incorrect format, please try again. Make sure to enter two dates separated by a comma.");
            } else {
                out.println("Too many attempts, returning to main...");
            }
        }
    }

}

