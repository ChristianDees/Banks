/*
// Authors: Christian Dees & Aitiana Mondragon & Crystina Rivera
// Date: October 23, 2024
// Course: CS 3331 - Advanced Object-Oriented Programming - Fall 2024
// Instructor: Dr. Bhanukiran Gurijala
// Assignment: Programming Assignment 1 (Project Part 1)
// Lab Description: This lab is meant to demonstrate our knowledge in object-oriented concepts such as inheritance, polymorphism, UML diagrams, and more through coding our own implementation of a bank system of which deposits, withdraws, transfers, and pays. This lab also included concepts of logging, testing, debugging, file reading, and JavaDoc.
// Honesty Statement: We affirm that we have completed this assignment entirely on our own, without any assistance from outside sources, including peers, experts, online resources, or other means. All code and ideas were that of our own work and we have followed proper academic integrity.
 */
/**
 * Represents a credit account with its balance, unique account number, and a maximum allowed credit debt
 */

public class Credit extends Account{
    int creditMax;

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
     * @param suppressSuccess   Flag to not print the success of the withdrawal or not.
     *
     * **/
    public boolean withdraw(double amount, boolean suppressSuccess){
        double totalCharge = (amount + (0.025*amount)); // AVERAGE TRANSACTION FEE
        if ((amount > 0) && ((this.balance - totalCharge) >= this.creditMax)) {
            this.balance -= totalCharge;
            if (!suppressSuccess) System.out.println("*  *  *  *  *  *  *  Withdraw Successful  *  *  *  *  *  *  *\n");
            return true;
        }
        System.out.println("*  *  *  *  *  *  *  Insufficient Funds   *  *  *  *  *  *  *\n");
        return false;
    }

    /**
     * Get the maximum credit limit of an account.
     *
     * @return an accounts maximum credit limit
     */
    public int getCreditMax(){
        return this.creditMax;
    }
}