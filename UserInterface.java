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

public abstract class UserInterface extends BankRegistry {
    public static boolean exit = false;
    public static boolean logout = false;

    /**
     * Displays a welcome message to the user
     */
    public void displayWelcomeMessage(){
        String message = "El Paso Miner's Bank";
        int length = message.length();
        out.println("+" + "-".repeat(length + 2) + "+");
        out.println("| " + message + " |");
        out.println("+" + "-".repeat(length + 2) + "+");
        out.println("Type 'exit' to quit.\n");
    }

    /**
     * Determine if input is of 'exit' and set global exit variable.
     *
     * @param input         The scanner object to continue taking input.
     * @return              The status of if the program should be exited.
     */
    public static boolean checkExit(String input) {
        return (input.equalsIgnoreCase("exit") && (exit = true)) || (input.equalsIgnoreCase("logout") && (logout = true));
    }

    /**
     *
     * @return if the user requests to exit or logout
     */
    public boolean leave(){
        return exit || logout;
    }

    /**
     * Handle a transaction with one account.
     *
     * @param scan          The scanner object to continue taking input.
     * @param viewBalance   Flag to show balance.
     */
    public Customer getUserName(Scanner scan, boolean viewAccounts, boolean viewBalance, FileHandler fh) {
        // three attempts
        for (int attempts = 0; attempts < 3; attempts++) {
            if(leave())return null;
            out.print("Enter customer id and name (id, name):\n> ");
            String input = scan.nextLine().trim();
            if (checkExit(input)) return null;
            String[] parts = input.split(",");
            if (parts.length != 2) {
                // error logging
                fh.appendLog("EPMB_Error_Log", "Attempted to ask for user's name. Reason for failure: Invalid format of id, name.");
                if (attempts < 2)
                    out.println("Invalid format. Use 'id, name'.");
                continue;
            }
            String formattedName = parts[0].trim() + parts[1].trim().toLowerCase().replace(" ", "");
            if (!checkExit(formattedName)){
                Customer customer = customers.get(formattedName);
                if (customer != null){
                    if (viewAccounts)
                        // print accounts if given
                        customer.viewAccounts(viewBalance);
                    return customer;
                }
                // error logging
                fh.appendLog("EPMB_Error_Log", "Attempted to ask for user's name. Reason for failure: Attempted to get nonexistent customer.");
                out.println("There is no account under that name associated with this bank.");
            }
        }
        return null;
    }

}

