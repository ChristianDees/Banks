/*
// Authors: Christian Dees & Aitiana Mondragon & Cristina Rivera
// Date: November 12, 2024
// Course: CS 3331 - Advanced Object-Oriented Programming - Fall 2024
// Instructor: Dr. Bhanukiran Gurijala
// Assignment: Programming Assignment 2 (Project Part 2)
// Lab Description: This lab is meant to demonstrate our knowledge in object-oriented concepts such as inheritance, polymorphism, UML diagrams, interfaces, design patterns, and more through coding our own implementation of a bank system of which deposits, withdraws, transfer, pays, and generates various files. This lab also included concepts of logging, testing with JUnit, debugging, file reading, error handling and JavaDoc.
// Honesty Statement: We affirm that we have completed this assignment entirely on our own, without any assistance from outside sources, including peers, experts, online resources, or other means. All code and ideas were that of our own work, and we have followed proper academic integrity.
 */
import java.util.ArrayList;
import java.util.Dictionary;

/**
 * Represents a manager.
 */
public class Manager implements Person{

    String firstName;
    String lastName;

    /**
     * Constructs a new Person with the specified attributes.
     *
     * @param firstName The first name of the person.
     * @param lastName  The last name of the person.
     */
    Manager(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    /**
     * Performs transaction from a provided file.
     *
     * @param filename  file that has the transactions.
     */
    public void transactFromFile(String filename) {
        FileHandler fh = new FileHandler();
        ArrayList<Dictionary<String, String>> allRecords = fh.getAllRecordsFromCSV("Transactions/"+filename);

        for (Dictionary<String, String> recordDict : allRecords) {
            String fromCustomerName = (recordDict.get("From First Name") + recordDict.get("From Last Name")).toLowerCase();
            Customer fromCustomer = BankDatabase.customers.get(fromCustomerName);
            if (fromCustomer != null) {
                String action = recordDict.get("Action").toLowerCase();
                String toCustomerName = (recordDict.get("To First Name") + recordDict.get("To Last Name")).toLowerCase();
                Customer toCustomer = BankDatabase.customers.get(toCustomerName);
                Account fromAccount = null;
                for (Account account : fromCustomer.getAccounts()) {
                    if (account.getType().equalsIgnoreCase(recordDict.get("From Where"))) {
                        fromAccount = account;
                        break;
                    }
                }
                Account toAccount = null;
                if (toCustomer != null) {
                    for (Account account : toCustomer.getAccounts()) {
                        if (account.getType().equalsIgnoreCase(recordDict.get("To Where"))) {
                            toAccount = account;
                            break;
                        }
                    }
                }
                if (fromAccount != null) {
                    double amount = recordDict.get("Action Amount").isEmpty() ? 0 : Double.parseDouble(recordDict.get("Action Amount"));
                    boolean rc;
                    switch (action) {
                        case "pays":
                            rc = fromCustomer.send(fromAccount, toAccount, amount, toCustomer);
                            if (!rc && toCustomer!=null) fh.appendLog("EPMB_Error_Log", fromCustomer.getFullName() + " [ID=" + fromCustomer.getId() + "]"+ " attempted to send funds to "  + toCustomer.getFullName() + " [ID=" + fromCustomer.getId() + "]"+ ".");
                            else if (!rc)fh.appendLog("EPMB_Error_Log", fromCustomer.getFullName() + " attempted to send funds without a valid receiving customer.");
                            else {
                                assert toCustomer != null;
                                fh.appendLog("EPMB_Transactions", fromCustomer.getFullName() + " [ID:" + fromCustomer.getId() + "] sent funds to " + toCustomer.getFullName() + " [ID=" + toCustomer.getId() + "]."+ " Account's Current Balance: $" + String.format("%.2f", fromAccount.getBalance()));
                            }
                            break;
                        case "transfers":
                            rc = fromCustomer.transfer(fromAccount, toAccount, amount);
                            if (!rc) fh.appendLog("EPMB_Error_Log", fromCustomer.getFullName() + " [ID=" + fromCustomer.getId() + "]"+  " attempted to transfer invalidly.");
                            else {
                                assert toAccount != null;
                                fh.appendLog("EPMB_Transactions", fromCustomer.getFullName() + " [ID:" + fromCustomer.getId() + "] transferred funds from " + fromAccount.getType() + " [ID=" + fromAccount.getAccountNumber() +"] to " + toAccount.getType()+ " [ID=" + toAccount.getAccountNumber() +"]" + "Account's Current Balance: $" + String.format("%.2f", fromAccount.getBalance()));
                            }
                            break;
                        case "inquires":
                            fromCustomer.viewAccount(fromAccount, true);
                            fh.appendLog("EPMB_Transactions", fromCustomer.getFullName() + " [ID:" + fromCustomer.getId() + "] inquired the details of " + fromAccount.getType() + " [ID=" + fromAccount.getAccountNumber() +"]");
                            break;
                        case "withdraws":
                            rc = fromCustomer.withdraw(fromAccount, amount);
                            if (!rc) fh.appendLog("EPMB_Error_Log", fromCustomer.getFullName()+ " [ID=" + fromCustomer.getId() + "]" + " attempted to withdraw funds.");
                            else fh.appendLog("EPMB_Transactions", fromCustomer.getFullName() + " [ID:" + fromCustomer.getId() + "] withdrew $" + String.format("%2f", amount) + " from " + fromAccount.getType() + " [ID=" + fromAccount.getAccountNumber() +"]");
                            break;
                        case "deposits":
                            rc = fromCustomer.deposit(toAccount, amount);
                            if (!rc) fh.appendLog("EPMB_Error_Log", fromCustomer.getFullName()+ " [ID=" + fromCustomer.getId() + "]" + " attempted to deposit funds.");
                            else {
                                assert toAccount != null;
                                fh.appendLog("EPMB_Transactions", fromCustomer.getFullName() + " [ID:" + fromCustomer.getId() + "] deposited $" + String.format("%2f", amount) + " to " + toAccount.getType() + " [ID=" + toAccount.getAccountNumber() +"]");
                            }
                            break;
                        default:
                            System.out.println("Invalid action: " + action);
                    }
                } else {
                    System.out.println("Transaction failed: No source account specified.");
                }
            } else {
                System.out.println("Transaction failed: No source customer specified.");
            }
        }
    }

    @Override
    public String getFullName() {
        return this.firstName + " " + this.lastName;
    }
}
