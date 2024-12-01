/*
// Authors: Christian Dees & Aitiana Mondragon & Cristina Rivera
// Date: November 12, 2024
// Course: CS 3331 - Advanced Object-Oriented Programming - Fall 2024
// Instructor: Dr. Bhanukiran Gurijala
// Assignment: Programming Assignment 2 (Project Part 2)
// Lab Description: This lab is meant to demonstrate our knowledge in object-oriented concepts such as inheritance, polymorphism, UML diagrams, interfaces, design patterns, and more through coding our own implementation of a bank system of which deposits, withdraws, transfer, pays, and generates various files. This lab also included concepts of logging, testing with JUnit, debugging, file reading, error handling and JavaDoc.
// Honesty Statement: We affirm that we have completed this assignment entirely on our own, without any assistance from outside sources, including peers, experts, online resources, or other means. All code and ideas were that of our own work, and we have followed proper academic integrity.
*/
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import static java.lang.System.out;

/**
 * Represents the user interface between the user and the system.
 */
public abstract class UserInterface {

    /**
     * Flag if user has requested to log out.
     */
    public static boolean logout = false;

    /**
     * Displays a welcome message to the user
     */
    public void displayWelcomeMessage(){
        // default display message
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
     * @param scan           The scanner object to continue taking input.
     * @param promptPassword Flag to prompt user for password.
     * @param viewAccounts   Flag to show accounts.
     * @param viewBalance    Flag to show balance.
     * @param fh             File handler object to read/write to files
     *
     * @return               Customer based on the username given.
     */
    public Customer getUserName(Scanner scan, boolean promptPassword, boolean viewAccounts, boolean viewBalance, FileHandler fh) {
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
                fh.appendLog("EPMB_Error_Log", "Attempted to ask for user's name. Reason for failure: Invalid format of name.");
                if (attempts < 2)
                    out.println("Invalid format.");
                continue;
            }
            String formattedName = firstName+lastName;
            if (!logout(formattedName)){
                // get customer object based on username
                Dictionary<String, Customer> customers = BankDatabase.getInstance().getCustomers();
                Customer customer = customers.get(formattedName);
                if (customer != null) {
                    boolean rc = true;
                    // require password to login if customer, not for manager
                    if (promptPassword) rc = this.requestPassword(customer, scan);
                    if (rc) {
                        if (viewAccounts)
                            // print accounts if given
                            customer.viewAccounts(viewBalance);
                        fh.appendLog("EPMB_Transactions", customer.getFullName() + " logged in.");
                        return customer;
                    } else continue;
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
     * @param fh            File handler object to read/write to files.
     *
     * @return              Account inquired from customer.
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
                // get account number
                int accNum = Integer.parseInt(parts[1].trim());
                TransactionInterface th = new TransactionInterface();
                Account account = th.getAccount(accType, accNum);
                if (account != null) {
                    ArrayList<Account> accounts = customer.accounts;
                    // if account exists within database
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
        // get the date from start and end
        Account account = null;
        if (customer == null){
            out.println("Customer does not exist. Returning...");
            return;
        }
        if (!allTransactions) account = getAccountForTransaction(scan, customer, fh);
        for (int i = 0; i < 3; i++){
            if (account == null && !allTransactions)return;
            // ask for time range
            out.print("Enter date range (comma separated, mm-dd-yyyy format)\n> ");
            String input = scan.nextLine().trim().toLowerCase();
            if (logout(input)) return;
            String[] dates = input.split(",");
            // continue if valid format
            if (dates.length == 2) {
                String startDate = dates[0].trim();
                String endDate = dates[1].trim();
                SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
                sdf.setLenient(false);
                try {
                    // get start and end date objects
                    sdf.parse(startDate);
                    sdf.parse(endDate);
                    // create filename
                    String filename = dir+"/"+customer.getFullName().replace(" ", "");
                    boolean rc = false;
                    if (allTransactions){
                        filename+=type+".txt";
                        // add every account to file
                        for (Account account1: customer.getAccounts())
                            rc = fh.generateUserTransactionsFile(filename,customer, account1, startDate, endDate);
                    }
                    else{
                        // only add specific account to file
                        filename+=account.getType()+type+".txt";
                        rc = fh.generateUserTransactionsFile(filename, customer, account, startDate, endDate);
                    }
                    // check if successful
                    if (rc){
                        fh.appendLog("EPMB_Transactions","Generated " + filename + " for " + customer.getFullName()+" [ID:" + customer.getId() + "]");
                        System.out.println("\n* * * Successfully exported transactions to " + filename + " * * *\n");
                    }
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

    /**
     * Give a prompt to user and match input to regex to ensure its proper format.
     *
     * @param scan      object scanner to allow for user input.
     * @param prompt    string question to ask.
     * @param regex     pattern of which the input should match.
     * @param fh        file handler object to log to files.
     * @return          input once in correct form.
     */
    private String requestCustomerInfo(Scanner scan, String prompt, String regex, FileHandler fh) {
        String input;
        int attempts = 0;
        // 3 attempts
        while (attempts < 3) {
            out.print(prompt);
            input = scan.nextLine().trim();
            // check if their input matches the correct format
            if (input.matches(regex)) {
                return input;
            }
            attempts++;
            fh.appendLog("EPMB_Error_Log", "Failed to input correct format: " + input);
            if (attempts < 3)out.println("Invalid input. Please try again.");
        }
        out.println("Maximum attempts reached, returning to main...");
        return null;
    }

    /**
     * Handles the asking of users each required information prompt.
     *
     * @param scan  scanner object to allow for user input.
     * @param fh    file handler object to log to files when needed.
     */
    public void handleNewCustomer(Scanner scan, FileHandler fh) {
        String[] defaultHeaders = {"Identification Number", "First Name", "Last Name", "Date of Birth", "Address", "Phone Number", "Checking Account Number", "Checking Starting Balance", "Savings Account Number", "Savings Starting Balance", "Credit Account Number", "Credit Starting Balance", "Credit Max", "Password"};
        out.println("Please fill out the following information:");
        // new customer fields + required formats
        String[][] prompts = {
                {"First Name: ", "[a-zA-Z]+"},
                {"Last Name: ", "[a-zA-Z]+"},
                {"Date of Birth (day-Mon-yy, e.g., '7-Feb-71'): ", "\\d{1,2}-[a-zA-Z]{3}-\\d{2}"},
                {"Address: ", ".+"},
                {"City: ", "[a-zA-Z ]+"},
                {"State (two-letter state code, e.g., 'TX'): ", "[A-Z]{2}"},
                {"Zip Code: ", "\\d{5}"},
                {"Phone Number: ", "\\d{10}"},
                {"Create a password with the following requirements:\n1. Minimum Length of 8 characters.\n2. Must include at least one uppercase letter.\n3. Must include at least one lowercase letter.\n4. Must include at least one digit.\n5. Must include at least one special character (e.g., !, @, #, $)\nNew Password: ", "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*(),.?\":{}|<>])[A-Za-z\\d!@#$%^&*(),.?\":{}|<>]{8,}$"},
                {"Confirm Password: ", "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*(),.?\":{}|<>])[A-Za-z\\d!@#$%^&*(),.?\":{}|<>]{8,}$"}
        };
        List<String> recordFormatted = new ArrayList<>();
        String[] record = new String[prompts.length + 1]; // +1 for the customer ID
        TreeSet<Integer> customerIDs = BankDatabase.getInstance().getCustomerIDs();
        // assign prompted answers to list
        record[0] = String.valueOf(customerIDs.last() + 1);
        for (int i = 0; i < prompts.length; i++) {
            record[i + 1] = requestCustomerInfo(scan, prompts[i][0], prompts[i][1], fh);
            if (record[i + 1] == null)return;
        }
        String formattedAddress = String.format("%s, %s, %s %s",
                (record[4].toLowerCase()),                                                                  // address
                (record[5].toLowerCase()),                                                                  // city
                record[6].toUpperCase(),                                                                    // state
                record[7].toLowerCase()                                                                     // zip
        );
        // if everything checks out THEN add a new id
        recordFormatted.add(record[0].toLowerCase());                                                       // id
        recordFormatted.add(record[1].toLowerCase());                                                       // first name
        recordFormatted.add(record[2].toLowerCase());                                                       // last name
        recordFormatted.add(record[3].toLowerCase());                                                       // dob
        recordFormatted.add(formattedAddress);                                                              // address
        recordFormatted.add(record[8].toLowerCase());                                                       // phone number
        recordFormatted.add(String.valueOf(BankDatabase.getInstance().getCheckingAccNums().last() + 1)); // checking account number
        recordFormatted.add(String.valueOf(0));                                                          // checking current balance
        recordFormatted.add(String.valueOf(BankDatabase.getInstance().getSavingsAccNums().last() + 1));  // savings account number
        recordFormatted.add(String.valueOf(0));                                                          // savings current balance
        recordFormatted.add(String.valueOf(BankDatabase.getInstance().getCreditAccNums().last() + 1));   // credit account number
        recordFormatted.add(String.valueOf(0));                                                          // credit current balance
        recordFormatted.add(String.valueOf(100 + new Random().nextInt(25000 - 100 + 1)));         // credit max
        if (!record[9].equals(record[10])){
            out.println("Passwords do not match.");
            out.println("\n* * * Failed to add new customer. * * *\n");
            return;
        }
        recordFormatted.add(record[9]);                                                                    // password
        Dictionary<String, String> recordDict = fh.recordToDictionary(recordFormatted.toArray(new String[0]), defaultHeaders);
        boolean rc = BankDatabase.getInstance().addCustomer(recordDict);
        if (rc){
            Customer newCustomer = BankDatabase.getInstance().getCustomers().get(recordFormatted.get(1)+recordFormatted.get(2));
            fh.appendLog("EPMB_Transactions", "New customer added: " + newCustomer.getFullName() + " [ID:" + newCustomer.getId() + "]");
            out.println("\n* * * Successfully added new customer. * * *\n");
        }
        else out.println("\n* * * Failed to add new customer. * * *\n");
    }

    /**
     * Query the user for a password.
     *
     * @param customer  customer that is being inquired.
     * @param scan      scanner object.
     * @return          if password is correct.
     */
    private boolean requestPassword(Customer customer, Scanner scan){
        String inp;
        for (int i = 0; i < 3; i++){
            // request user to enter password
            out.print("Password: ");
            inp = scan.nextLine();
            // return success if password is correct
            if (customer.verifyPassword(inp)) return true;
            out.println("Incorrect password. Please try again.");
        }
        out.println("Too many failed attempts. Returning...");
        return false;
    }
}

