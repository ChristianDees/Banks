/*
// Authors: Christian Dees & Aitiana Mondragon & Cristina Rivera
// Date: November 12, 2024
// Course: CS 3331 - Advanced Object-Oriented Programming - Fall 2024
// Instructor: Dr. Bhanukiran Gurijala
// Assignment: Programming Assignment 2 (Project Part 2)
// Lab Description: This lab is meant to demonstrate our knowledge in object-oriented concepts such as inheritance, polymorphism, UML diagrams, interfaces, design patterns, and more through coding our own implementation of a bank system of which deposits, withdraws, transfer, pays, and generates various files. This lab also included concepts of logging, testing with JUnit, debugging, file reading, error handling and JavaDoc.
// Honesty Statement: We affirm that we have completed this assignment entirely on our own, without any assistance from outside sources, including peers, experts, online resources, or other means. All code and ideas were that of our own work, and we have followed proper academic integrity.
*/
import java.text.NumberFormat;

/**
 * Represents a credit account with its balance, unique account number, and a maximum allowed credit debt
 */

public class Credit extends Account{
    double creditMax;

    /**
     * Constructs a new Credit Account with the specified attributes.
     *
     * @param accNum         The unique id number of the account.
     * @param startBalance   The credit account's starting balance.
     * @param creditMax      The credit account's maximum allowed debt.
     */
    Credit(int accNum, double startBalance, int creditMax){
        super(accNum, startBalance);
        this.creditMax = creditMax;
    }

    /**
     * Takes a specified amount out from one customer's account.
     *
     * @param amount            The total amount to be withdrawn from the account.
     *
     * **/
    public boolean withdraw(double amount){
        double totalCharge = (amount + (0.025*amount)); // AVERAGE TRANSACTION FEE
        // if valid amount to withdraw, including a fee
        if ((amount > 0) && ((this.balance - totalCharge) >= (-1*this.creditMax))) {
            this.balance -= totalCharge;
            return true;
        }
        System.out.println("\nWarning: Invalid amount.");
        return false;
    }

    /**
     * Add money to an account.
     **/
    public boolean deposit(double amount){
        // if valid amount to deposit
        if (amount + this.balance <= 0){
            this.balance += amount;
            return true;
        }
        else System.out.println("\nWarning: Credit deposits cannot exceed the outstanding balance.");
        return false;
    }

    /**
     * Get the maximum credit limit of an account.
     *
     * @return an accounts maximum credit limit
     */
    public double getCreditMax(){
        return this.creditMax;
    }

    /**
     * Print attributes of an account
     *
     * @param showBalance  Print the balance along with other attributes.
     **/
    public void printAccount(boolean showBalance, boolean printHeader) {
        if (printHeader) this.printHeader(showBalance);
        NumberFormat formatter = NumberFormat.getCurrencyInstance();
        String formattedBalance = formatter.format(this.getBalance());
        String formattedCreditMax = formatter.format(this.getCreditMax());
        // print detailed header if requested
        if (showBalance) {
            System.out.printf("| %-15s | %-20s | %-20s | %-20s | \n",
                    this.getType(),
                    this.getAccountNumber(),
                    formattedBalance,
                    formattedCreditMax);
            System.out.println("+-----------------+----------------------+----------------------+----------------------+");
        } else {
            System.out.printf("| %-15s | %-20s |\n",
                    this.getType(),
                    this.getAccountNumber());
            System.out.println("+-----------------+----------------------+");
        }
    }
}