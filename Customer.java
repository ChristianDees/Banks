/*
// Authors: Christian Dees & Aitiana Mondragon & Crystina Rivera
// Date: October 23, 2024
// Course: CS 3331 - Advanced Object-Oriented Programming - Fall 2024
// Instructor: Dr. Bhanukiran Gurijala
// Assignment: Programming Assignment 1 (Project Part 1)
// Lab Description: This lab is meant to demonstrate our knowledge in object-oriented concepts such as inheritance, polymorphism, UML diagrams, and more through coding our own implementation of a bank system of which deposits, withdraws, transfer, and pays. This lab also included concepts of logging, testing, debugging, file reading, and JavaDoc.
// Honesty Statement: We affirm that we have completed this assignment entirely on our own, without any assistance from outside sources, including peers, experts, online resources, or other means. All code and ideas were that of our own work, and we have followed proper academic integrity.
 */
import java.util.ArrayList;

/**
 * Represents a customer with their accounts, dob, address, phone number, and unique id number
 */
public class Customer extends Person{
    ArrayList<Account> accounts = new ArrayList<Account>();
    String dob;
    String address;
    String phoneNum;
    int idNum;

    /**
     * Constructs a new Customer with the specified attributes.
     *
     * @param idNum         The unique id number of a customer.
     * @param firstName     The customer's first name.
     * @param lastName      The customer's last name.
     * @param dob           The customer's date of birth.
     * @param address       The customer's address of residence.
     * @param phoneNum      The customer's phone number.
     */
    Customer(int idNum, String firstName, String lastName, String dob, String address, String phoneNum){
        super(firstName, lastName);
        this.idNum = idNum;
        this.dob = dob;
        this.address = address;
        this.phoneNum = phoneNum;
    }

    /**
     * Get id int.
     *
     * @return the int
     */
    public int getId(){
        return this.idNum;
    }

    /**
     * Get first name string.
     *
     * @return the string
     */
    public String getFirstName(){
        return this.firstName;
    }

    /**
     * Get last name string.
     *
     * @return the string
     */
    public String getLastName(){
        return this.lastName;
    }

    /**
     * Get dob string.
     *
     * @return the string
     */
    public String getDob(){
        return this.dob;
    }

    /**
     * Get address string.
     *
     * @return the string
     */
    public String getAddress(){
        return this.address;
    }

    /**
     * Get phone num string.
     *
     * @return the string
     */
    public String getPhoneNum(){
        return this.phoneNum;
    }

    /**
     * Get accounts array list.
     *
     * @return the array list
     */
    public ArrayList<Account> getAccounts(){
        return this.accounts;
    }

    /**
     * Prints an account's information if it exists.
     *
     * @param viewBalance   Print the balance if true, don't if false.
     */
    public void viewAccounts(boolean viewBalance) {
        for (int i = 0; i < this.accounts.size(); i++) {
            boolean isFirstAccount = (i == 0);
            this.accounts.get(i).printAccount(viewBalance, isFirstAccount);
        }
    }

    /**
     * Add account.
     *
     * @param account the account
     */
    public void addAccount(Account account){
        this.accounts.add(account);
    }

    /**
     * Transfers money from one customer's account to another account under the same customer.
     *
     * @param src       The source account that the amount will be withdrawn from.
     * @param dst       The destination account that the amount will be deposited to.
     * @param amount    The amount of money to be transferred.
     *
     * @return          The successfulness of money being transferred.
     * **/
    public boolean transfer(Account src, Account dst, double amount){
        boolean rc = false;
        if (this.accounts.contains(src) && this.accounts.contains(dst)){
            rc = src.withdraw(amount, true);
            if (rc){
                dst.deposit(amount, true);
                System.out.println("\n*  *  *  *  *  *  *  Transfer Successful  *  *  *  *  *  *  *");
            }
        }
        if (!rc) System.out.println("*  *  *  *  *  *  *    Transfer Failed    *  *  *  *  *  *  *");
        src.printAccount(true, true);
        dst.printAccount(true, false);
        return rc;
    }

    /**
     * Sends money from one customer's account to another customer's account.
     *
     * @param src       The source account that the amount will be withdrawn from.
     * @param dst       The destination account that the amount will be deposited to.
     * @param amount    The amount of money to be sent.
     *
     * @return          The successfulness of money being sent.
     * **/
    public boolean send(Account src, Account dst, double amount) {
        boolean rc = src.withdraw(amount, true);
        if (rc) {
            dst.deposit(amount, true);
            System.out.println("\n*  *  *  *  *  *  *    Send Successful    *  *  *  *  *  *  *");
        } else {
            System.out.println("*  *  *  *  *  *  *      Send Failed      *  *  *  *  *  *  *");
        }
        src.printAccount(true, true);
        return rc;
    }
}