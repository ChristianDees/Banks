/*
// Authors: Christian Dees & Aitiana Mondragon & Cristina Rivera
// Date: November 12, 2024
// Course: CS 3331 - Advanced Object-Oriented Programming - Fall 2024
// Instructor: Dr. Bhanukiran Gurijala
// Assignment: Programming Assignment 2 (Project Part 2)
// Lab Description: This lab is meant to demonstrate our knowledge in object-oriented concepts such as inheritance, polymorphism, UML diagrams, interfaces, design patterns, and more through coding our own implementation of a bank system of which deposits, withdraws, transfer, pays, and generates various files. This lab also included concepts of logging, testing with JUnit, debugging, file reading, error handling and JavaDoc.
// Honesty Statement: We affirm that we have completed this assignment entirely on our own, without any assistance from outside sources, including peers, experts, online resources, or other means. All code and ideas were that of our own work, and we have followed proper academic integrity.*/
/**
 * Represents a savings account with its balance and unique account number
 */

public class Savings extends Account{

    /**
     * Constructs a new savings Account with the specified attributes.
     *
     * @param accNum         The unique id number of the account.
     * @param startBalance   The savings account's starting balance.
     */
    Savings(int accNum, double startBalance){
        super(accNum, startBalance);
    }

    /**
     * Success indicating that deposit was successful.
     *
     * @param amount    amount to be deposited.
     * @return          success/fail of deposit.
     */
    @Override
    public boolean deposit(double amount){
        this.balance += amount;
        return true;
    }

    /**
     * Take money out of an account.
     *
     * @param amount    amount to be withdrawn.
     *
     * @return          The successfulness of a withdrawal.
     * **/
    @Override
    public boolean withdraw(double amount){
        if (amount <= this.balance && amount > 0){
            this.balance -= amount;
            return true;
        }
        System.out.println("\nWarning: Insufficient funds.");
        return false;
    }
}
