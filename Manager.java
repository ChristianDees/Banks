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

    /**
     * Manager's first name
     */
    String firstName;

    /**
     * Manager's last name
     */
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
        // for every record dictionary in all the csv records
        if (allRecords==null)return;
        for (Dictionary<String, String> recordDict : allRecords) {
            String fromCustomerName = (recordDict.get("From First Name") + recordDict.get("From Last Name")).toLowerCase();
            Customer fromCustomer = BankDatabase.customers.get(fromCustomerName);
            String toCustomerName = (recordDict.get("To First Name") + recordDict.get("To Last Name")).toLowerCase();
            Customer toCustomer = BankDatabase.customers.get(toCustomerName);
            String action = recordDict.get("Action").toLowerCase();
            String fromAccountType = recordDict.get("From Where");
            Account fromAccount = null;
            // get source account if required
            if (fromCustomer != null) {
                for (Account account : fromCustomer.getAccounts()) {
                    if (account.getType().equalsIgnoreCase(fromAccountType)) {
                        fromAccount = account;
                        break;
                    }
                }
            }
            Account toAccount = null;
            String toAccountType = recordDict.get("To Where");
            // get destination account if required
            if (toCustomer != null) {
                for (Account account : toCustomer.getAccounts()) {
                    if (account.getType().equalsIgnoreCase(toAccountType)) {
                        toAccount = account;
                        break;
                    }
                }
            }
            double amount = recordDict.get("Action Amount").isEmpty() ? 0 : Double.parseDouble(recordDict.get("Action Amount"));
            String amountStr = String.format("%.2f", amount);
            boolean rc;
            String err = "";
            String err1 = "source not specified";
            String err2 = "destination not specified";
            String err3 = "source should not be specified";
            String err4 = "destination should not be specified";
            // switch based on transaction type
            switch (action) {
                case "pays":
                    // check if send is successful
                    if (fromCustomer != null){
                        if (toCustomer != null) {
                            rc = fromCustomer.send(fromAccount, toAccount, amount, toCustomer);
                            if (!rc) fh.appendLog("EPMB_Error_Log", fromCustomer.getFullName() + " [ID=" + fromCustomer.getId() + "]" + " attempted to send funds to " + toCustomer.getFullName() + " [ID=" + fromCustomer.getId() + "]" + ".");
                            else {
                                assert fromAccount != null;
                                assert toAccount != null;
                                fh.appendLog("EPMB_Transactions", fromCustomer.getFullName() + " [ID=" + fromCustomer.getId() + "] sent $" + amountStr + " from " + fromAccount.getType() + " [ID=" + fromAccount.getAccountNumber() + "]" + " to " + toCustomer.getFullName() + "'s " + "[ID=" + toCustomer.getId() + "] " + toAccount.getType() + " [ID=" + toAccount.getAccountNumber() + "]");
                            }
                        } else err = err2;
                    } else err = err1;
                    break;
                case "transfers":
                    // check if transfer is successful
                    if (fromCustomer != null) {
                        if (toCustomer != null) {
                            rc = fromCustomer.transfer(fromAccount, toAccount, amount);
                            if (!rc)
                                fh.appendLog("EPMB_Error_Log", fromCustomer.getFullName() + " [ID=" + fromCustomer.getId() + "]" + " attempted to transfer invalidly.");
                            else {
                                assert toAccount != null;
                                assert fromAccount != null;
                                fh.appendLog("EPMB_Transactions", fromCustomer.getFullName() + " [ID=" + fromCustomer.getId() + "] transferred $" + amountStr +  " from " + fromAccount.getType() + " [ID=" + fromAccount.getAccountNumber() + "] to " + toAccount.getType() + " [ID=" + toAccount.getAccountNumber() + "]");
                            }
                        } else err = err2;
                    } else err = err1;
                    break;
                case "inquires":
                    // no checking, inquiring is free of charge
                    if (fromCustomer != null){
                        if (toCustomer == null){
                            fromCustomer.viewAccount(fromAccount, true);
                            assert fromAccount != null;
                            fh.appendLog("EPMB_Transactions", fromCustomer.getFullName() + " [ID=" + fromCustomer.getId() + "] inquired the details of " + fromAccount.getType() + " [ID=" + fromAccount.getAccountNumber() +"]");
                        } else err = err4;
                    } else err = err1;
                    break;
                case "withdraws":
                    // check if withdraw is successful
                    if (fromCustomer != null) {
                        if (toCustomer == null && toAccountType.isEmpty()) {
                            rc = fromCustomer.withdraw(fromAccount, amount);
                            if (!rc)
                                fh.appendLog("EPMB_Error_Log", fromCustomer.getFullName() + " [ID=" + fromCustomer.getId() + "]" + " attempted to withdraw funds.");
                            else {
                                assert fromAccount != null;
                                fh.appendLog("EPMB_Transactions", fromCustomer.getFullName() + " [ID=" + fromCustomer.getId() + "] withdrew $" + amountStr + " from " + fromAccount.getType() + " [ID=" + fromAccount.getAccountNumber() + "]");
                            }
                        } else err = err4;
                    } else err = err1;
                    break;
                case "deposits":
                    // check if deposit is successful
                    if (toCustomer != null) {
                        if (fromCustomer == null && fromAccountType.isEmpty()) {
                            rc = toCustomer.deposit(toAccount, amount);
                            if (!rc) fh.appendLog("EPMB_Error_Log", toCustomer.getFullName()+ " [ID=" + toCustomer.getId() + "]" + " attempted to deposit funds.");
                            else {
                                assert toAccount != null;
                                fh.appendLog("EPMB_Transactions", toCustomer.getFullName() + " [ID=" + toCustomer.getId() + "] deposited $" + amountStr + " to " + toAccount.getType() + " [ID=" + toAccount.getAccountNumber() +"]");
                            }
                        } else err = err3;
                    } else err = err2;
                    break;
                default:
                    System.out.println("Invalid action: " + action);
            }
            if (!err.isEmpty()) System.out.println("Error: " + err);
        }
    }

    /**
     * Gets a manager's full name
     *
     * @return The person's full name
     * **/
    @Override
    public String getFullName() {
        return this.firstName + " " + this.lastName;
    }
}
