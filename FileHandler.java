/*
// Authors: Christian Dees & Aitiana Mondragon & Crystina Rivera
// Date: October 23, 2024
// Course: CS 3331 - Advanced Object-Oriented Programming - Fall 2024
// Instructor: Dr. Bhanukiran Gurijala
// Assignment: Programming Assignment 1 (Project Part 1)
// Lab Description: This lab is meant to demonstrate our knowledge in object-oriented concepts such as inheritance, polymorphism, UML diagrams, and more through coding our own implementation of a bank system of which deposits, withdraws, transfer, and pays. This lab also included concepts of logging, testing, debugging, file reading, and JavaDoc.
// Honesty Statement: We affirm that we have completed this assignment entirely on our own, without any assistance from outside sources, including peers, experts, online resources, or other means. All code and ideas were that of our own work, and we have followed proper academic integrity.
 */
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import java.io.FileReader;
import java.io.*;
import java.util.*;
import static java.lang.System.out;

/**
 * Represents an object to handle file appending and creation.
 */
public class FileHandler extends BankRegistry{

    /**
     * Load from csv.
     *
     * @param filename the filename.
     */
    public boolean loadFromCSV(String filename) {
        filename += ".csv";
        try (Scanner scan = new Scanner(new File(filename))) {
            // get headers
            String[] headers = scan.nextLine().split(",");
            try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
                CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                        .setHeader(headers)
                        .setSkipHeaderRecord(true)
                        .build();
                // collect customer's info and accounts per each record
                for (CSVRecord record : csvFormat.parse(reader)) {
                    if(!addCustomer(record, headers)) out.println("Failed to add customer.");
                }
                return true;
            }
        } catch (IOException e) {
            out.println("Error loading from file: " + e.getMessage());
            return false;
        }
    }

    boolean addCustomer(CSVRecord record, String[] headers) {
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
        double creditStartBalance = Double.parseDouble(record.get(headers[12]));
        int creditMax = Integer.parseInt(record.get(headers[11]));
        // add customer and its accounts
        boolean rc = customerIDs.add(idNum);
        if(rc){
            Customer newCustomer = new Customer(idNum, firstName, lastName, dob, address, phoneNum);
            Checking checkingAcc = new Checking(checkingAccNum, checkingStartBalance);
            Savings savingsAcc = new Savings(savingsAccNum, savingsStartBalance);
            Credit creditAcc = new Credit(creditAccNum, creditStartBalance, creditMax);
            if (addAccountToMaps(checkingAcc)) newCustomer.addAccount(checkingAcc);
            if (addAccountToMaps(savingsAcc)) newCustomer.addAccount(savingsAcc);
            if (addAccountToMaps(creditAcc)) newCustomer.addAccount(creditAcc);
            customers.put(idNum+firstName+lastName, newCustomer);
        } else {
            this.appendLog("EPMB_Error_Log", "Failed to add user with id: " + idNum + ". Reason: User with that id already exists.");
        }
        return rc;
    }

    /**
     * Add provided account to respective dictionary
     *
     * @param account provided account to add
     */
    public boolean addAccountToMaps(Account account) {
        boolean rc = false;
        int accountNum = account.getAccountNumber();
        String err = "Failed to add account with account number: " + accountNum + ". Reason: Account with that account number already exists.";
        switch (account) {
            case Checking ignored -> {
                rc = checkingAccNums.add(accountNum);
                if (rc) checkingAccounts.put(accountNum, (Checking) account);
                else this.appendLog("EPMB_Error_Log", err);
            }
            case Savings ignored -> {
                rc = savingsAccNums.add(accountNum);
                if (rc) savingAccounts.put(accountNum, (Savings) account);
                else this.appendLog("EPMB_Error_Log", err);
            }
            case Credit ignored -> {
                rc = creditAccNums.add(accountNum);
                if (rc) creditAccounts.put(accountNum, (Credit) account);
                else this.appendLog("EPMB_Error_Log", err);
            }
            default -> this.appendLog("EPMB_Error_Log", "Failed to add account with account number: " + accountNum + ". Reason: Account with that type does not exist.");
        }
        return rc;
    }

    /**
     * Export all customers to a csv file.
     *
     * @param filename the filename to exported to.
     */
    public void exportToCSV(String filename) {
        filename += ".csv";
        // write to file
        try (FileWriter writer = new FileWriter(filename, false)) {
            // write hardcoded headers
            writer.write(String.join(",", defaultHeaders) + System.lineSeparator());
            // iterate customers data to add
            for (String key : Collections.list(customers.keys())) {
                Customer customer = customers.get(key);
                String[] customerData = new String[defaultHeaders.length];
                customerData[0] = String.valueOf(customer.getId());
                customerData[1] = capitalizeFirst(customer.getFirstName());
                customerData[2] = capitalizeFirst(customer.getLastName());
                customerData[3] = customer.getDob();
                customerData[4] = escapeValue(customer.getAddress());
                customerData[5] = customer.getPhoneNum();
                String checkingAccountNumber = "", checkingBalance = "";
                String savingsAccountNumber = "", savingsBalance = "";
                String creditAccountNumber = "", creditBalance = "", creditMaxValue = "";
                for (Account account : customer.getAccounts()) {
                    if (account instanceof Checking checking) {
                        checkingAccountNumber = String.valueOf(checking.getAccountNumber());
                        checkingBalance = String.format("%.2f", checking.getBalance());
                    } else if (account instanceof Savings savings) {
                        savingsAccountNumber = String.valueOf(savings.getAccountNumber());
                        savingsBalance = String.format("%.2f", savings.getBalance());
                    } else if (account instanceof Credit credit) {
                        creditAccountNumber = String.valueOf(credit.getAccountNumber());
                        creditBalance = String.format("%.2f", credit.getBalance());
                        creditMaxValue = String.valueOf(credit.getCreditMax());
                    }
                }
                customerData[6] = checkingAccountNumber;
                customerData[7] = checkingBalance;
                customerData[8] = savingsAccountNumber;
                customerData[9] = savingsBalance;
                customerData[10] = creditAccountNumber;
                customerData[11] = creditMaxValue;
                customerData[12] = creditBalance;
                writer.write(String.join(",", customerData) + System.lineSeparator());
            }
            out.println("\n* * * Successfully exported data to " + filename + " * * *");
        } catch (IOException e) {
            out.println("An error occurred while writing to the CSV file: " + e.getMessage());
        }
    }

    /**
     * Add quotes around strings with commas.
     *
     * @param value is the string with commas
     */
    private String escapeValue(String value) {
        if (value.contains(",")) {
            return "\"" + value.replace("\"", "\"\"") + "\""; // Escape double quotes by doubling them
        }
        return value;
    }

    /**
     * Append message to a log (txt).
     *
     * @param filename the filename.
     */
    public void appendLog(String filename, String msg) {
        try (FileWriter myWriter = new FileWriter(filename + ".txt", true)) {
            // append message to text file
            myWriter.write(msg + System.lineSeparator());
        } catch (IOException e) {
            out.println("An error occurred while writing to the log file: " + e.getMessage());
        }
    }

}
