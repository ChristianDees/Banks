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
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Represents an Account with its unique account number and current balance
 */
public abstract class Account {
    int accNum;
    double balance;
    TransactionLinkedList transactionList;

    /**
     * Constructs a new Account with the specified attributes.
     *
     * @param accNum         The unique id number of the account.
     * @param startBalance   The account's starting balance.
     */
    Account(int accNum, double startBalance){
        this.accNum = accNum;
        this.balance = startBalance;
        this.transactionList = new TransactionLinkedList(this.getBalance());
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
     * Get the account's transaction list.
     *
     * @return  the account's transaction linked list.
     */
    public TransactionLinkedList getTransactionList() {
        return this.transactionList;}

    /**
     * Success indicating that deposit was successful.
     *
     * @param amount    amount to be deposited.
     * @return          success/fail of deposit.
     */
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
     *
     * @param printHeader  Flag to print header or not.
     **/
    public void printAccount(boolean showBalance, boolean printHeader) {
        if (printHeader) this.printHeader(showBalance);

        NumberFormat formatter = NumberFormat.getCurrencyInstance();
        // Ensure we have a consistent width for currency formatting.
        String formattedBalance = formatter.format(this.getBalance());


        // print the balance and other attributes

        if (showBalance) {
            System.out.printf("| %-15s | %-20s | %-20s | %-20s | \n",
                    this.getType(),
                    this.getAccountNumber(),
                    formattedBalance,
                    "None");
            System.out.println("+-----------------+----------------------+----------------------+----------------------+");
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
            // Full header with adjusted column widths
            System.out.println("+-----------------+----------------------+----------------------+----------------------+");
            System.out.printf("| %-15s | %-20s | %-20s | %-20s | \n", "Type", "Account Number", "Balance", "Limit");
            System.out.println("+-----------------+----------------------+----------------------+----------------------+");
        } else {
            // Partial header for when balance and limit are not shown
            System.out.println("+-----------------+----------------------+");
            System.out.printf("| %-15s | %-20s |\n", "Type", "Account Number");
            System.out.println("+-----------------+----------------------+");
        }
    }

    /**
     * Adds a transaction string to transaction LL.
     *
     * @param description string that describes the transaction.
     *
     * @param amount      to be transacted
     */
    public void addTransaction(String description, double amount){
        this.transactionList.addTransaction(LocalDate.now().format(DateTimeFormatter.ofPattern("MM-dd-yyyy")), description, amount, this.getBalance());
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