/*
// Authors: Christian Dees & Aitiana Mondragon & Crystina Rivera
// Date: October 23, 2024
// Course: CS 3331 - Advanced Object-Oriented Programming - Fall 2024
// Instructor: Dr. Bhanukiran Gurijala
// Assignment: Programming Assignment 1 (Project Part 1)
// Lab Description: This lab is meant to demonstrate our knowledge in object-oriented concepts such as inheritance, polymorphism, UML diagrams, and more through coding our own implementation of a bank system of which deposits, withdraws, transfers, and pays. This lab also included concepts of logging, testing, debugging, file reading, and JavaDoc.
// Honesty Statement: We affirm that we have completed this assignment entirely on our own, without any assistance from outside sources, including peers, experts, online resources, or other means. All code and ideas were that of our own work and we have followed proper academic integrity.
 */
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import java.io.FileReader;
import java.io.*;
import java.util.*;

/**
 * Represents an object to handle file appending and creation.
 */
public class FileHandler extends UserInterface{

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
                    Customer newCustomer = createCustomer(record, headers);
                    customers.put(newCustomer.getId()+newCustomer.getFirstName()+newCustomer.getLastName(), newCustomer);
                    for (Account account : newCustomer.getAccounts()) {
                        addAccountToMaps(account);
                    }
                }
                return true;
            }
        } catch (IOException e) {
            System.out.println("Error loading from file: " + e.getMessage());
            return false;
        }
    }

    /**
     * Create a customer based on a record and possible headers.
     *
     * @param record    customers information
     * @param HEADERS   possible headers
     * @return          Customer object of its appropriate attributes
     */
    private Customer createCustomer(CSVRecord record, String[] HEADERS) {
        int idNum = Integer.parseInt(record.get(HEADERS[0]));
        String firstName = record.get(HEADERS[1]).toLowerCase();
        String lastName = record.get(HEADERS[2]).toLowerCase();
        String dob = record.get(HEADERS[3]);
        String address = record.get(HEADERS[4]).replace("\"", "");
        String phoneNum = record.get(HEADERS[5]).replaceAll("[()\\s-]", "");
        // add customer and its accounts
        Customer customer = new Customer(idNum, firstName, lastName, dob, address, phoneNum);
        customer.addAccount(new Checking(Integer.parseInt(record.get(HEADERS[6])), Double.parseDouble(record.get(HEADERS[7]))));
        customer.addAccount(new Savings(Integer.parseInt(record.get(HEADERS[8])), Double.parseDouble(record.get(HEADERS[9]))));
        customer.addAccount(new Credit(Integer.parseInt(record.get(HEADERS[10])), Double.parseDouble(record.get(HEADERS[12])), Integer.parseInt(record.get(HEADERS[11]))));

        return customer;
    }

    /**
     * Add provided account to respective dictionary
     *
     * @param account provided account to add
     */
    private void addAccountToMaps(Account account) {
        if (account instanceof Checking) {
            checkingAccounts.put(account.getAccountNumber(), (Checking) account);
        } else if (account instanceof Savings) {
            savingAccounts.put(account.getAccountNumber(), (Savings) account);
        } else if (account instanceof Credit) {
            creditAccounts.put(account.getAccountNumber(), (Credit) account);
        }
    }

    /**
     * Update csv.
     *
     * @param filename the filename.
     */
    public void exportToCSV(String filename) {
        // setup filename
        filename += ".csv";
        HashMap<String, String[]> existingData = new HashMap<>();
        // get current data
        try (Scanner scan = new Scanner(new FileReader(filename))) {
            String headerLine = scan.nextLine();
            String[] headers = headerLine.split(",");
            // get index of id col
            int idColumnIndex = -1;
            for (int i = 0; i < headers.length; i++) {
                if (headers[i].equalsIgnoreCase("ID")) {
                    idColumnIndex = i;
                    break;
                }
            }
            // no id col
            if (idColumnIndex == -1) {
                System.out.println("Error: ID column does not exist within the file.");
                return;
            }
            // get current data
            while (scan.hasNextLine()) {
                String line = scan.nextLine();
                String[] values = line.split(",");
                // must fit for headers
                if (values.length > idColumnIndex) {
                    String id = values[idColumnIndex];
                    existingData.put(id, values);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading the existing CSV file: " + e.getMessage());
        }
        // start writing
        try (FileWriter writer = new FileWriter(filename, false)) { // Overwrite file
            String[] headers = {
                    "ID",
                    "First Name",
                    "Last Name",
                    "Date of Birth",
                    "Address",
                    "Phone Number",
                    "Checking Account Number",
                    "Checking Current Balance",
                    "Savings Account Number",
                    "Savings Current Balance",
                    "Credit Account Number",
                    "Credit Max",
                    "Credit Current Balance"
            };
            // write headers
            writer.write(String.join(",", headers));
            writer.write(System.lineSeparator());
            // iterate over customers
            Enumeration<String> keys = customers.keys();
            while (keys.hasMoreElements()) {
                String key = keys.nextElement();
                Customer customer = customers.get(key);
                // get new values
                String[] currentValues = new String[headers.length];
                currentValues[0] = String.valueOf(customer.getId());
                currentValues[1] = customer.getFirstName();
                currentValues[2] = customer.getLastName();
                currentValues[3] = customer.getDob();
                currentValues[4] = customer.getAddress();
                currentValues[5] = customer.getPhoneNum();
                // setup accounts
                String checkingAccountNumber = "", checkingBalance = "";
                String savingsAccountNumber = "", savingsBalance = "";
                String creditAccountNumber = "", creditMax = "", creditBalance = "";
                // get customer's accounts
                ArrayList<Account> accounts = customer.getAccounts();
                if (accounts != null) {
                    for (Account account : accounts) {
                        if (account instanceof Checking checking) {
                            checkingAccountNumber = String.valueOf(checking.getAccountNumber());
                            checkingBalance = String.format("%.2f", checking.getBalance());
                        } else if (account instanceof Savings savings) {
                            savingsAccountNumber = String.valueOf(savings.getAccountNumber());
                            savingsBalance = String.format("%.2f", savings.getBalance());
                        } else if (account instanceof Credit credit) {
                            creditAccountNumber = String.valueOf(credit.getAccountNumber());
                            creditMax = String.valueOf(credit.getCreditMax());
                            creditBalance = String.format("%.2f", credit.getBalance());
                        }
                    }
                }
                // get new information
                currentValues[6] = checkingAccountNumber;
                currentValues[7] = checkingBalance;
                currentValues[8] = savingsAccountNumber;
                currentValues[9] = savingsBalance;
                currentValues[10] = creditAccountNumber;
                currentValues[11] = creditMax;
                currentValues[12] = creditBalance;
                // get current vals
                String[] existingValues = existingData.get(currentValues[0]);
                if (existingValues != null) {
                    // check if worth updating
                    for (int i = 0; i < headers.length; i++) {
                        if (i < existingValues.length) { // check out of bounds
                            if (!currentValues[i].equals(existingValues[i])) {
                                existingValues[i] = currentValues[i]; // update if different
                            }
                        }
                    }
                } else {
                    // if user is new
                    existingData.put(currentValues[0], currentValues);
                }
            }
            // write all updated or new entries back to the file
            for (String[] values : existingData.values()) {
                writer.write(String.join(",", values));
                writer.write(System.lineSeparator());
            }
            System.out.println("\n* * * Successfully exported data to " + filename + " * * *");
        } catch (IOException e) {
            System.out.println("An error occurred while writing to the log file: " + e.getMessage());
        }
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
            System.out.println("An error occurred while writing to the log file: " + e.getMessage());
        }
    }

}
