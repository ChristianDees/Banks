/*
// Authors: Christian Dees & Aitiana Mondragon & Crystina Rivera
// Date: October 23, 2024
// Course: CS 3331 - Advanced Object-Oriented Programming - Fall 2024
// Instructor: Dr. Bhanukiran Gurijala
// Assignment: Programming Assignment 1 (Project Part 1)
// Lab Description: This lab is meant to demonstrate our knowledge in object-oriented concepts such as inheritance, polymorphism, UML diagrams, and more through coding our own implementation of a bank system of which deposits, withdraws, transfer, and pays. This lab also included concepts of logging, testing, debugging, file reading, and JavaDoc.
// Honesty Statement: We affirm that we have completed this assignment entirely on our own, without any assistance from outside sources, including peers, experts, online resources, or other means. All code and ideas were that of our own work, and we have followed proper academic integrity.
 */
import java.text.NumberFormat;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Represents an Account with its unique account number and current balance
 */
public abstract class Account {
    int accNum;
    double balance;

    /**
     * Constructs a new Account with the specified attributes.
     *
     * @param accNum         The unique id number of the account.
     * @param startBalance   The account's starting balance.
     */
    Account(int accNum, double startBalance){
        this.accNum = accNum;
        this.balance = startBalance;
    }

    /**
     * Get the account's current balance.
     *
     * @return  The current balance of the account.
     * **/
    public double getBalance() {
        BigDecimal bd = BigDecimal.valueOf(this.balance);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    /**
     * Get the account's account number.
     *
     * @return  The account's account number.
     * **/
    public int getAccountNumber(){
        return this.accNum;
    }

    /**
     * Get the account's type.
     *
     * @return  The account's type.
     * **/
    public String getType(){
        return this.getClass().getName();
    }

    /**
     * Add money to an account.
     **/
    public boolean deposit(double amount){
        this.balance += amount;
        return true;
    }

    /**
     * Take money out of an account.
     *
     * @return  The successfulness of a withdrawal.
     * **/
    public boolean withdraw(double amount){
        if (amount <= this.balance && amount > 0){
            this.balance -= amount;
            return true;
        }
        System.out.println("\nWarning: Insufficient funds.");
        return false;
    }

    /**
     * Print attributes of an account
     *
     * @param showBalance  Print the balance along with other attributes.
     **/
    public void printAccount(boolean showBalance, boolean printHeader) {
        if (printHeader) this.printHeader(showBalance);
        NumberFormat formatter = NumberFormat.getCurrencyInstance();
        // print the balance
        if (showBalance) {
            System.out.printf("| %-15s | %-20s | %-16s |\n",
                    this.getType(),
                    this.getAccountNumber(),
                    formatter.format(this.getBalance()));
            System.out.println("+-----------------+----------------------+------------------+");
        } else {
            System.out.printf("| %-15s | %-20s |\n",
                    this.getType(),
                    this.getAccountNumber());
            System.out.println("+-----------------+----------------------+");
        }
    }

    /**
     * Print header for an account.
     *
     * @param showBalance Print balance in header.
     */
    public void printHeader(boolean showBalance){
        if (showBalance) {
            // full header
            System.out.println("+-----------------+----------------------+------------------+");
            System.out.printf("| %-15s | %-20s | %-16s |\n", "Type", "Account Number", "Balance");
            System.out.println("+-----------------+----------------------+------------------+");
        } else {
            // partial header
            System.out.println("+-----------------+----------------------+");
            System.out.printf("| %-15s | %-20s |\n", "Type", "Account Number");
            System.out.println("+-----------------+----------------------+");
        }
    }

    /**
     * Compares this Account object to the specified object for equality.
     * Two Account objects are considered equal if they have the same account number.
     *
     * @param obj the object to be compared for equality with this Account
     * @return true if the specified object is equal to this Account;
     *         false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Account account = (Account) obj;
        return accNum == (account.accNum);
    }

    /**
     * Returns a hash code value for this Account object.
     * The hash code is computed based on the account number.
     *
     * @return a hash code value for this Account
     */
    @Override
    public int hashCode() {
        return Integer.hashCode(accNum);
    }

}