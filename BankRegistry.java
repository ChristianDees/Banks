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
import java.util.TreeSet;

public abstract class BankRegistry {
    public static Dictionary<String, Customer> customers = new Hashtable<>();
    public static  Dictionary<Integer, Checking> checkingAccounts = new Hashtable<>();
    public static  Dictionary<Integer, Savings> savingAccounts = new Hashtable<>();
    public static final Dictionary<Integer, Credit> creditAccounts = new Hashtable<>();
    public static  TreeSet<Integer> customerIDs = new TreeSet<>();
    public static final TreeSet<Integer> checkingAccNums = new TreeSet<>();
    public static final TreeSet<Integer> savingsAccNums = new TreeSet<>();
    public static final TreeSet<Integer> creditAccNums = new TreeSet<>();

    String[] defaultHeaders = {"Identification Number", "First Name", "Last Name", "Date of Birth", "Address", "Phone Number", "Checking Account Number", "Checking Starting Balance", "Savings Account Number", "Savings Starting Balance", "Credit Account Number", "Credit Max", "Credit Starting Balance"};

    /**
     * Add a customer given values mapped to headers.
     *
     * @param record    values mapped to headers.
     * @return          true if success/false if failed.
     */
    boolean addCustomer(Dictionary<String, String> record) {
        int idNum = Integer.parseInt(record.get(defaultHeaders[0]));
        String firstName = record.get(defaultHeaders[1]).toLowerCase();
        String lastName = record.get(defaultHeaders[2]).toLowerCase();
        String dob = record.get(defaultHeaders[3]);
        String address = record.get(defaultHeaders[4]).replace("\"", "");
        String phoneNum = record.get(defaultHeaders[5]).replaceAll("[()\\s-]", "");
        int checkingAccNum = Integer.parseInt(record.get(defaultHeaders[6]));
        double checkingStartBalance = Double.parseDouble(record.get(defaultHeaders[7]));
        int savingsAccNum = Integer.parseInt(record.get(defaultHeaders[8]));
        double savingsStartBalance = Double.parseDouble(record.get(defaultHeaders[9]));
        int creditAccNum = Integer.parseInt(record.get(defaultHeaders[10]));
        int creditMax = Integer.parseInt(record.get(defaultHeaders[11]));
        double creditStartBalance = Double.parseDouble(record.get(defaultHeaders[12]));
        /*
            FUTURE: REMOVE THIS BECAUSE DUMB
         */
        FileHandler fh = new FileHandler();
        if (customers.get(firstName+lastName)!=null){
            System.out.println("-----A Customer with that name already exists!-----");
            fh.appendLog("EPMB_Error_Log", "Failed to add user with id: " + idNum + ". Reason: User with that name already exists.");
            return false;
        }
        // add customer and its accounts
        boolean rc = customerIDs.add(idNum);
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
            customers.put(firstName+lastName, newCustomer);
        }
        return rc;
    }
}
