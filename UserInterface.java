/*
// Authors: Christian Dees & Aitiana Mondragon & Crystina Rivera
// Date: October 23, 2024
// Course: CS 3331 - Advanced Object-Oriented Programming - Fall 2024
// Instructor: Dr. Bhanukiran Gurijala
// Assignment: Programming Assignment 1 (Project Part 1)
// Lab Description: This lab is meant to demonstrate our knowledge in object-oriented concepts such as inheritance, polymorphism, UML diagrams, and more through coding our own implementation of a bank system of which deposits, withdraws, transfers, and pays. This lab also included concepts of logging, testing, debugging, file reading, and JavaDoc.
// Honesty Statement: We affirm that we have completed this assignment entirely on our own, without any assistance from outside sources, including peers, experts, online resources, or other means. All code and ideas were that of our own work and we have followed proper academic integrity.
 */
import java.util.Dictionary;
import java.util.Hashtable;

public abstract class UserInterface {
    public static boolean exit = false;
    public static boolean logout = false;
    public static final Dictionary<String, Customer> customers = new Hashtable<>();
    public static final Dictionary<Integer, Checking> checkingAccounts = new Hashtable<>();
    public static final Dictionary<Integer, Savings> savingAccounts = new Hashtable<>();
    public static final Dictionary<Integer, Credit> creditAccounts = new Hashtable<>();

    /**
     * Determine if input is of 'exit' and set global exit variable.
     *
     * @param input         The scanner object to continue taking input.
     * @return              The status of if the program should be exited.
     */
    public static boolean checkExit(String input) {
        return (input.equalsIgnoreCase("exit") && (exit = true)) || (input.equalsIgnoreCase("logout") && (logout = true));
    }
}

