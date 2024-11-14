/*
// Authors: Christian Dees & Aitiana Mondragon & Cristina Rivera
// Date: November 12, 2024
// Course: CS 3331 - Advanced Object-Oriented Programming - Fall 2024
// Instructor: Dr. Bhanukiran Gurijala
// Assignment: Programming Assignment 2 (Project Part 2)
// Lab Description: This lab is meant to demonstrate our knowledge in object-oriented concepts such as inheritance, polymorphism, UML diagrams, interfaces, design patterns, and more through coding our own implementation of a bank system of which deposits, withdraws, transfer, pays, and generates various files. This lab also included concepts of logging, testing with JUnit, debugging, file reading, error handling and JavaDoc.
// Honesty Statement: We affirm that we have completed this assignment entirely on our own, without any assistance from outside sources, including peers, experts, online resources, or other means. All code and ideas were that of our own work, and we have followed proper academic integrity.
 */
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Linked list for transactions of an account.
 */
public class TransactionLinkedList {

    /**
     * Head of linked list.
     */
    private TransactionNode head;

    /**
     * Initial balance before any transactions.
     */
    public final double startingBalance;

    /**
     * Constructor of LL
     *
     * @param startingBalance starting balance before any transactions.
     */
    public TransactionLinkedList(double startingBalance) {
        this.head = null;
        this.startingBalance = startingBalance;
    }

    /**
     * Adds a transaction with its respective attributes.
     *
     * @param date          of transaction.
     * @param description   of transaction.
     * @param amount        transacted.
     * @param newBalance    after transaction.
     */
    public void addTransaction(String date, String description, double amount, double newBalance) {
        TransactionNode newNode = new TransactionNode(date, description, amount, newBalance);
        if (head == null) head = newNode;
        else {
            TransactionNode current = head;
            while (current.next != null) current = current.next;
            current.next = newNode;
        }
    }

    /**
     * Get list of transactions between a start and end range of dates.
     *
     * @param startDate         lower bound.
     * @param endDate           upper bound.
     * @return                  list of transactions within that bound.
     * @throws ParseException   for dates.
     */
    public List<TransactionNode> getTransactionsBetweenDates(String startDate, String endDate) throws ParseException {
        List<TransactionNode> result = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
        // get start object to compare
        java.util.Date start = sdf.parse(startDate);
        // get end object to compare
        java.util.Date end = sdf.parse(endDate);
        TransactionNode current = head;
        // keep going until end
        while (current != null) {
            java.util.Date transactionDate = sdf.parse(current.date);
            // if dates match, or its after start or before end, add it to the list
            if ((transactionDate.equals(start) || transactionDate.after(start)) &&
                    (transactionDate.equals(end) || transactionDate.before(end))) {
                result.add(current);
            }
            current = current.next;
        }
        return result;
    }

}
