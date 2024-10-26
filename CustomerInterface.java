/*
// Authors: Christian Dees & Aitiana Mondragon & Crystina Rivera
// Date: October 23, 2024
// Course: CS 3331 - Advanced Object-Oriented Programming - Fall 2024
// Instructor: Dr. Bhanukiran Gurijala
// Assignment: Programming Assignment 1 (Project Part 1)
// Lab Description: This lab is meant to demonstrate our knowledge in object-oriented concepts such as inheritance, polymorphism, UML diagrams, and more through coding our own implementation of a bank system of which deposits, withdraws, transfer, and pays. This lab also included concepts of logging, testing, debugging, file reading, and JavaDoc.
// Honesty Statement: We affirm that we have completed this assignment entirely on our own, without any assistance from outside sources, including peers, experts, online resources, or other means. All code and ideas were that of our own work, and we have followed proper academic integrity.
 */
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Scanner;
import static java.lang.System.out;

public class CustomerInterface extends UserInterface{

    /**
     * Query for the total transactions the user will perform.
     *
     * @param scan          The scanner object to continue taking input.
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
                            out.print("Choose a transaction:\nA. Transaction between single person.\nB. Transaction between two people.\n> ");
                            input = scan.nextLine().trim().toLowerCase();
                            if (logout(input)) return;
                            TransactionInterface transaction = new TransactionInterface();
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
            out.println("Invalid input. Please try again.");
        }
        out.println("Maximum attempts reached, returning to main...");
        return null;
    }

    public void handleNewCustomer(Scanner scan, FileHandler fh) {
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
        String[] userInputs = new String[prompts.length];
        for (int i = 0; i < prompts.length; i++) {
            userInputs[i] = requestCustomerInfo(scan, prompts[i][0], prompts[i][1], fh);
            if (userInputs[i] == null) return; // exit if invalid
        }
        String formattedAddress = String.format("%s, %s, %s %s",
                capitalizeFirst(userInputs[3]), // address
                capitalizeFirst(userInputs[4]), // city
                userInputs[5].toUpperCase(), // state
                userInputs[6] // zip
        );
        // if everything checks out THEN add a new id
        Dictionary<String, String> record = getStringStringDictionary(userInputs, formattedAddress);
        boolean rc = addCustomer(record, defaultHeaders);
        if (rc) out.println("\n* * * Successfully added new customer. * * *\n");
        else out.println("\n* * * Failed to add new customer. * * *\n");
    }

    private static Dictionary<String, String> getStringStringDictionary(String[] userInputs, String formattedAddress) {
        int idNum = customerIDs.last() + 1;
        int checkingAccNum = checkingAccNums.last() + 1;
        int checkingCurrBalance = 0;
        int savingsAccNum = savingsAccNums.last() + 1;
        int savingsCurrBalance = 0;
        int creditAccNum = creditAccNums.last() + 1;
        int creditAccBalance = 0;
        int creditMax = 0; // CHANGE THIS
        // add customer data to dictionary
        Dictionary<String, String> record = new Hashtable<>();
        record.put("ID", String.valueOf(idNum));
        record.put("First Name", userInputs[0]);
        record.put("Last Name", userInputs[1]);
        record.put("Date of Birth", capitalizeMonth(userInputs[2]));
        record.put("Address", formattedAddress);
        record.put("Phone Number", userInputs[7]);
        record.put("Checking Account Number", String.valueOf(checkingAccNum));
        record.put("Checking Current Balance", String.valueOf(checkingCurrBalance));
        record.put("Savings Account Number", String.valueOf(savingsAccNum));
        record.put("Savings Current Balance", String.valueOf(savingsCurrBalance));
        record.put("Credit Account Number", String.valueOf(creditAccNum));
        record.put("Credit Max", String.valueOf(creditMax));
        record.put("Credit Current Balance", String.valueOf(creditAccBalance));
        return record;
    }

    private static String capitalizeMonth(String date) {
        String[] parts = date.split("-");
        if (parts.length > 1) {
            parts[1] = parts[1].substring(0, 1).toUpperCase() + parts[1].substring(1);
        }
        return String.join("-", parts);
    }

    boolean addCustomer(Dictionary<String, String> record, String[] headers) {
        int idNum = Integer.parseInt(record.get(headers[0]));
        String firstName = record.get(headers[1]).toLowerCase();
        String lastName = record.get(headers[2]).toLowerCase();
        String dob = record.get(headers[3]);
        String address = record.get(headers[4]).replace("\"", "");
        String phoneNum = record.get(headers[5]).replaceAll("[()\\s-]", "");
        int checkingAccNum = Integer.parseInt(record.get(headers[6]));
        double checkingStartBalance = Double.parseDouble(record.get(headers[7]));
        int savingsAccNum = Integer.parseInt(record.get(headers[8]));
        double savingsStartBalance = Double.parseDouble(record.get(headers[9]));
        int creditAccNum = Integer.parseInt(record.get(headers[10]));
        int creditMax = Integer.parseInt(record.get(headers[11]));
        double creditStartBalance = Double.parseDouble(record.get(headers[12]));

        // add customer and its accounts
        boolean rc = customerIDs.add(idNum);
        FileHandler fh = new FileHandler();
        if(!rc){
            fh.appendLog("EPMB_Error_Log", "Failed to add user with id: " + idNum + ". Reason: User with that id already exists.");
        } else {
            Customer newCustomer = new Customer(idNum, firstName, lastName, dob, address, phoneNum);
            Checking checkingAcc = new Checking(checkingAccNum, checkingStartBalance);
            Savings savingsAcc = new Savings(savingsAccNum, savingsStartBalance);
            Credit creditAcc = new Credit(creditAccNum, creditStartBalance, creditMax);
            if (fh.addAccountToMaps(checkingAcc)) newCustomer.addAccount(checkingAcc);
            if (fh.addAccountToMaps(savingsAcc)) newCustomer.addAccount(savingsAcc);
            if (fh.addAccountToMaps(creditAcc)) newCustomer.addAccount(creditAcc);
            customers.put(idNum+firstName+lastName, newCustomer);
        }
        return rc;
    }

}
