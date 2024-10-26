/*
// Authors: Christian Dees & Aitiana Mondragon & Crystina Rivera
// Date: October 23, 2024
// Course: CS 3331 - Advanced Object-Oriented Programming - Fall 2024
// Instructor: Dr. Bhanukiran Gurijala
// Assignment: Programming Assignment 1 (Project Part 1)
// Lab Description: This lab is meant to demonstrate our knowledge in object-oriented concepts such as inheritance, polymorphism, UML diagrams, and more through coding our own implementation of a bank system of which deposits, withdraws, transfer, and pays. This lab also included concepts of logging, testing, debugging, file reading, and JavaDoc.
// Honesty Statement: We affirm that we have completed this assignment entirely on our own, without any assistance from outside sources, including peers, experts, online resources, or other means. All code and ideas were that of our own work, and we have followed proper academic integrity.
 */
import org.apache.commons.csv.CSVRecord;
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

    String[] defaultHeaders = {
            "ID", "First Name", "Last Name", "Date of Birth", "Address",
            "Phone Number", "Checking Account Number", "Checking Current Balance",
            "Savings Account Number", "Savings Current Balance",
            "Credit Account Number", "Credit Max", "Credit Current Balance"
    };

    boolean addCustomer(CSVRecord record, String[] headers) {return false;}
    boolean addCustomer(Dictionary<String, String> record, String[] headers) {return false;}

    /**
     * Capitalize first letter of a string.
     *
     * @param   str String to have first letter capitalized
     * @return  String after first letter is capitalized
     */
    public String capitalizeFirst(String str) {
        StringBuilder capitalized = new StringBuilder();
        String[] words = str.split("\\s+");

        for (String word : words) {
            if (!word.isEmpty()) {
                capitalized.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1)).append(" ");
            }
        }

        return capitalized.toString().trim();
    }

}
