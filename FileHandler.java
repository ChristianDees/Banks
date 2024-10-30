/*
// Authors: Christian Dees & Aitiana Mondragon & Crystina Rivera
// Date: October 23, 2024
// Course: CS 3331 - Advanced Object-Oriented Programming - Fall 2024
// Instructor: Dr. Bhanukiran Gurijala
// Assignment: Programming Assignment 1 (Project Part 1)
// Lab Description: This lab is meant to demonstrate our knowledge in object-oriented concepts such as inheritance, polymorphism, UML diagrams, and more through coding our own implementation of a bank system of which deposits, withdraws, transfer, and pays. This lab also included concepts of logging, testing, debugging, file reading, and JavaDoc.
// Honesty Statement: We affirm that we have completed this assignment entirely on our own, without any assistance from outside sources, including peers, experts, online resources, or other means. All code and ideas were that of our own work, and we have followed proper academic integrity.
 */
import java.io.*;
import java.util.*;
import java.util.function.Function;
import static java.lang.System.out;

/**
 * Represents an object to handle file appending and creation.
 */
public class FileHandler extends BankRegistry{

    /**
     * Load from CSV.
     *
     * @param filename the filename.
     */
    public void loadFromCSV(String filename) {
        ArrayList<Dictionary<String, String>> allRecords = getAllRecordsFromCSV(filename);
        for (Dictionary<String, String> recordDict : allRecords){
            if (!addCustomer(recordDict)) System.out.println("Failed to add customer.");
        }
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
    public void exportCustomerReportToCSV(String filename) {
        filename += ".csv";
        // write to file
        try (FileWriter writer = new FileWriter(filename, false)) {
            Function<String, String> formatPhoneNumber = phoneNumber -> String.format("(%s) %s-%s", phoneNumber.substring(0, 3), phoneNumber.substring(3, 6), phoneNumber.substring(6));
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
                customerData[5] = formatPhoneNumber.apply(customer.getPhoneNum());
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

    /**
     * Given a record and the file's headers, get a dictionary mapping header to its value.
     *
     * @param values    a csv record/row's values.
     * @param headers   the csv headers.
     * @return          dictionary of value mapped to header.
     */
    public Dictionary<String, String> recordToDictionary(String[] values, String[] headers) {
        Dictionary<String, String> recordDict = new Hashtable<>();
        for (int i = 0; i < headers.length; i++) recordDict.put(headers[i].trim(), values[i].trim());
        return recordDict;
    }

    /**
     * Get all the records from a csv file.
     *
     * @param filename  of which the data to be extracted.
     * @return          2d array list of every row from csv.
     */
    public ArrayList<Dictionary<String, String>> getAllRecordsFromCSV(String filename) {
        ArrayList<Dictionary<String, String>> allRecords = new ArrayList<>();
        String[] headers = getHeadersFromCSV(filename);
        filename += ".csv";
        try (Scanner scan = new Scanner(new File(filename))) {
            scan.nextLine();
            while (scan.hasNextLine()) {
                String line = scan.nextLine();
                String[] values = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                Dictionary<String, String> recordDict = recordToDictionary(values, headers);
                allRecords.add(recordDict);
            }
        } catch (IOException e) {
            out.println("Error loading from file: " + e.getMessage());
        }
        return allRecords;
    }

    /**
     * Get the headers of a csv file.
     *
     * @param filename  of the csv file to extract headers.
     * @return          an array of headers.
     */
    public String[] getHeadersFromCSV(String filename) {
        String[] headers = null;
        filename += ".csv";
        try (Scanner scan = new Scanner(new File(filename))) {
            headers = scan.nextLine().split(",");
        } catch (IOException e) {
            out.println("Error loading from file: " + e.getMessage());
        }
        return headers;
    }
}
