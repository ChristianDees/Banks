/*
// Authors: Christian Dees & Aitiana Mondragon & Crystina Rivera
// Date: October 23, 2024
// Course: CS 3331 - Advanced Object-Oriented Programming - Fall 2024
// Instructor: Dr. Bhanukiran Gurijala
// Assignment: Programming Assignment 1 (Project Part 1)
// Lab Description: This lab is meant to demonstrate our knowledge in object-oriented concepts such as inheritance, polymorphism, UML diagrams, and more through coding our own implementation of a bank system of which deposits, withdraws, transfer, and pays. This lab also included concepts of logging, testing, debugging, file reading, and JavaDoc.
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
        int attempts = 0;
        // get their username
        for (int i = 0; i < 3; i++){
            out.print("Are you an existing customer, or a new one?\nA. Existing customer\nB. New customer\n> ");
            String input = scan.nextLine().trim().toLowerCase();
            if (logout(input)) return;
            switch (input){
                case "a":
                    Customer customer = this.getUserName(scan, false, false, fh);
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
                                    transaction.oneAccountTransaction(scan, customer, fh);
                                    break;
                                case "b":
                                    transaction.twoAccountTransaction(scan, customer, null, false, fh);
                                    break;
                                case "c":
                                    this.getTimeRange(scan, customer, fh, false, "UserTransactions", "Transactions");
                                    break;
                                case "d":
                                    this.getTimeRange(scan, customer, fh, true, "UserTransactions", "Transactions");
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
                    return;
                case "b":
                    handleNewCustomer(scan, fh);
                    return;
                default:
                    out.println("Please enter a valid option ('a' or 'b').");
            }
        }
        out.println("Logging out.\n");
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
        while (attempts < 3) {
            out.print(prompt);
            input = scan.nextLine().trim();
            if (input.matches(regex)) {
                return input.toLowerCase();
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
        String[] defaultHeaders = {"Identification Number", "First Name", "Last Name", "Date of Birth", "Address", "Phone Number", "Checking Account Number", "Checking Starting Balance", "Savings Account Number", "Savings Starting Balance", "Credit Account Number", "Credit Starting Balance", "Credit Max"};
        out.println("Please fill out the following information:");
        String[][] prompts = {
                {"First Name: ", "[a-zA-Z]+"},
                {"Last Name: ", "[a-zA-Z]+"},
                {"Date of Birth (day-Mon-yy, e.g., '7-Feb-71'): ", "\\d{1,2}-[a-zA-Z]{3}-\\d{2}"},
                {"Address: ", ".+"},
                {"City: ", "[a-zA-Z ]+"},
                {"State (two-letter state code, e.g., 'TX'): ", "[A-Z]{2}"},
                {"Zip Code: ", "\\d{5}"},
                {"Phone Number: ", "\\d{10}"}
        };
        List<String> recordFormatted = new ArrayList<>();
        String[] record = new String[prompts.length + 1]; // +1 for the customer ID
        TreeSet<Integer> customerIDs = BankDatabase.getInstance().getCustomerIDs();
        record[0] = String.valueOf(customerIDs.last() + 1);
        for (int i = 0; i < prompts.length; i++) {
            record[i + 1] = requestCustomerInfo(scan, prompts[i][0], prompts[i][1], fh);
            if (record[i + 1] == null)return;
        }
        String formattedAddress = String.format("%s, %s, %s %s",
                (record[4]),                // address
                (record[5]),                // city
                record[6].toUpperCase(),    // state
                record[7]                   // zip
        );
        // if everything checks out THEN add a new id
        recordFormatted.add(record[0]);                                                                     // id
        recordFormatted.add(record[1]);                                                                     // first name
        recordFormatted.add(record[2]);                                                                     // last name
        recordFormatted.add(record[3]);                                                                     // dob
        recordFormatted.add(formattedAddress);                                                              // address
        recordFormatted.add(record[8]);                                                                     // phone number
        recordFormatted.add(String.valueOf(BankDatabase.getInstance().getCheckingAccNums().last() + 1)); // checking account number
        recordFormatted.add(String.valueOf(0));                                                          // checking current balance
        recordFormatted.add(String.valueOf(BankDatabase.getInstance().getSavingsAccNums().last() + 1));  // savings account number
        recordFormatted.add(String.valueOf(0));                                                          // savings current balance
        recordFormatted.add(String.valueOf(BankDatabase.getInstance().getCreditAccNums().last() + 1));   // credit account number
        recordFormatted.add(String.valueOf(0));                                                          // credit current balance
        recordFormatted.add(String.valueOf(100 + new Random().nextInt(25000 - 100 + 1)));         // credit max
        Dictionary<String, String> recordDict = fh.recordToDictionary(recordFormatted.toArray(new String[0]), defaultHeaders);
        boolean rc = BankDatabase.getInstance().addCustomer(recordDict);
        if (rc) out.println("\n* * * Successfully added new customer. * * *\n");
        else out.println("\n* * * Failed to add new customer. * * *\n");
    }
}
