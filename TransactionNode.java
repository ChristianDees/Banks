/*
// Authors: Christian Dees & Aitiana Mondragon & Cristina Rivera
// Date: November 12, 2024
// Course: CS 3331 - Advanced Object-Oriented Programming - Fall 2024
// Instructor: Dr. Bhanukiran Gurijala
// Assignment: Programming Assignment 2 (Project Part 2)
// Lab Description: This lab is meant to demonstrate our knowledge in object-oriented concepts such as inheritance, polymorphism, UML diagrams, interfaces, design patterns, and more through coding our own implementation of a bank system of which deposits, withdraws, transfer, pays, and generates various files. This lab also included concepts of logging, testing with JUnit, debugging, file reading, error handling and JavaDoc.
// Honesty Statement: We affirm that we have completed this assignment entirely on our own, without any assistance from outside sources, including peers, experts, online resources, or other means. All code and ideas were that of our own work, and we have followed proper academic integrity.
 */

/**
 * Transaction Node with its respective attributes for a transaction.
 */
public class TransactionNode {
    String date;
    String description;
    double amount;
    double newBalance;
    TransactionNode next;

    /**
     * Constructor to create a transaction node.
     *
     * @param date          of transaction.
     * @param description   of transaction.
     * @param amount        transacted.
     * @param newBalance    after transaction.
     */
    public TransactionNode(String date, String description, double amount, double newBalance) {
        this.date = date;
        this.description = description;
        this.amount = amount;
        this.newBalance = newBalance;
        this.next = null;
    }
}

