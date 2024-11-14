/*
// Authors: Christian Dees & Aitiana Mondragon & Cristina Rivera
// Date: November 12, 2024
// Course: CS 3331 - Advanced Object-Oriented Programming - Fall 2024
// Instructor: Dr. Bhanukiran Gurijala
// Assignment: Programming Assignment 2 (Project Part 2)
// Lab Description: This lab is meant to demonstrate our knowledge in object-oriented concepts such as inheritance, polymorphism, UML diagrams, interfaces, design patterns, and more through coding our own implementation of a bank system of which deposits, withdraws, transfer, pays, and generates various files. This lab also included concepts of logging, testing with JUnit, debugging, file reading, error handling and JavaDoc.
// Honesty Statement: We affirm that we have completed this assignment entirely on our own, without any assistance from outside sources, including peers, experts, online resources, or other means. All code and ideas were that of our own work, and we have followed proper academic integrity.
*/
import java.util.*;
import static java.lang.System.out;

/**
 * Represents an object to handle input for the bank
 */
public class MainInterface extends UserInterface{

    /**
     * Flag indicating if user requested to exit the bank.
     */
    private boolean exit = false;

    /**
     * Query for user's role to log in
     *
     * @param scan required for input
     */
    private void getUserRole(Scanner scan){
        FileHandler fh = new FileHandler();
        // check if user logged out
        if (logout){
            fh.appendLog("EPMB_Transactions", "User logged out.");
            logout = false;
        }
        out.print("Please enter your role (customer/manager):\n> ");
        String role = scan.nextLine().trim().toLowerCase();
        switch (role) {
            case "customer":
                // enact customer interface
                CustomerInterface ci = new CustomerInterface();
                ci.handleCustomer(scan, fh);
                break;
            case "manager":
                // enact manager interface
                ManagerInterface mi = new ManagerInterface();
                mi.handleManager(scan, fh);
                break;
            case "exit":
                // leave if requested
                exit = true;
                break;
            default:
                // error logging
                fh.appendLog("EPMB_Error_Log", "User attempted to log in. Reason for failure: Invalid option of login.");
                out.println("Invalid option. Please choose 'customer' or 'manager'.");
        }
    }

    /**
     * Ask for input until program is exited
     */
    public void mainMenu(){
        Scanner scan = new Scanner(System.in);
        // stay on unless requested to leave
        while (!exit)
            getUserRole(scan);
        out.println("Exiting...");
    }
}
