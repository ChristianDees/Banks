/*
// Authors: Christian Dees & Aitiana Mondragon & Crystina Rivera
// Date: October 23, 2024
// Course: CS 3331 - Advanced Object-Oriented Programming - Fall 2024
// Instructor: Dr. Bhanukiran Gurijala
// Assignment: Programming Assignment 1 (Project Part 1)
// Lab Description: This lab is meant to demonstrate our knowledge in object-oriented concepts such as inheritance, polymorphism, UML diagrams, and more through coding our own implementation of a bank system of which deposits, withdraws, transfer, and pays. This lab also included concepts of logging, testing, debugging, file reading, and JavaDoc.
// Honesty Statement: We affirm that we have completed this assignment entirely on our own, without any assistance from outside sources, including peers, experts, online resources, or other means. All code and ideas were that of our own work, and we have followed proper academic integrity.
 */
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Represents a customer with their accounts, dob, address, phone number, and unique id number
 */
public class Customer implements Person{
    int idNum;
    String firstName;
    String lastName;
    String dob;
    String address;
    String phoneNum;
    ArrayList<Account> accounts = new ArrayList<>();
    LinkedList<String> transactions = new LinkedList<>();

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
        this.firstName = firstName;
        this.lastName = lastName;
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
        String[] parts = this.dob.split("-");
        if (parts.length > 1)parts[1] = parts[1].substring(0, 1).toUpperCase() + parts[1].substring(1);
        return String.join("-", parts);
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
     * Get phone number string.
     *
     * @return the string formatted
     */
    public String getPhoneNum(){
        return String.format("(%s) %s-%s", this.phoneNum.substring(0, 3), this.phoneNum.substring(3, 6), this.phoneNum.substring(6));
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
     * Get customer's transactions
     *
     * @return customer's transactions
     */
    public LinkedList<String> getTransactions(){
        return this.transactions;
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
     * Prints an account's information if it exists.
     *
     * @param viewBalance   Print the balance if true, don't if false.
     */
    public void viewAccount(Account account, boolean viewBalance) {
        if (this.accounts.contains(account)){
            account.printAccount(viewBalance, viewBalance);
        } else {
            System.out.println("This account does not belong to this owner!");
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
    public boolean transfer(Account src, Account dst, double amount) {
        if (!this.accounts.contains(src) || !this.accounts.contains(dst) || src.equals(dst)) {
            System.out.println("\nWarning: The customer must own both accounts to transfer funds between them and the accounts cannot be the same.");
            return false;
        }
        boolean rc = src.withdraw(amount) && dst.deposit(amount);
        String transactionMessage = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) +
                "," + "TRANSFER" + ","  + src.getType() + "," + src.getAccountNumber() + "," +
                dst.getType() + "," + dst.getAccountNumber() + "," +
                String.format("%.2f", amount) + "," + dst.getBalance();
        if (rc) {
            this.addTransaction(transactionMessage);
            System.out.println("\n*  *  *  *  *  *  *  Transfer Successful  *  *  *  *  *  *  *");
        } else System.out.println("\n*  *  *  *  *  *  *    Transfer Failed    *  *  *  *  *  *  *");
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
    public boolean send(Account src, Account dst, double amount, Customer toCustomer) {
        if (this.accounts.contains(src) && this.accounts.contains(dst)) {
            System.out.println("\nWarning: Customers cannot send funds to themselves; please use the transfer option instead.");
            return false;
        }
        boolean rc = src.withdraw(amount) && dst.deposit(amount);
        String transactionMessage = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) +
                "," + "SEND" + ","  + src.getType() + "," + src.getAccountNumber() + "," +
                toCustomer.getFullName() + "," + toCustomer.getId() + "," +
                String.format("%.2f", amount) + "," + src.getBalance();
        String depositMessage = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) +
                "," + "RECEIVE" + "," + this.getFullName() + "," + this.getId() + "," +
                dst.getType() + "," + dst.getAccountNumber() + "," +
                String.format("%.2f", amount) + "," + dst.getBalance();
        if (rc) {
            toCustomer.addTransaction(depositMessage);
            this.addTransaction(transactionMessage);
            System.out.println("\n*  *  *  *  *  *  *    Send Successful    *  *  *  *  *  *  *");
        } else System.out.println("*  *  *  *  *  *  *      Send Failed      *  *  *  *  *  *  *");
        src.printAccount(true, true);
        dst.printAccount(false, false);
        return rc;
    }

    /**
     * Withdraw an amount from an account, if customer owns it.
     *
     * @param src               source account.
     * @param amount            amount to be withdrawn.
     * @return                  true if success/false if failed.
     */
    public boolean withdraw(Account src, double amount) {
        if (!this.accounts.contains(src)) {
            System.out.println("\nWarning: This account does not belong to this customer.");
            return false;
        }
        boolean rc = src.withdraw(amount);
        if (rc) {
            String transactionMessage = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) +
                    "," + "WITHDRAWAL" + ","  + src.getType() + "," + src.getAccountNumber() + "," +
                    " " + "," + " " + "," +
                    String.format("%.2f", amount) + "," + src.getBalance();
            this.addTransaction(transactionMessage);
            System.out.println("\n*  *  *  *  *  *  *  Withdraw Successful  *  *  *  *  *  *  *");
        } else System.out.println("\n*  *  *  *  *  *  *    Withdraw Failed    *  *  *  *  *  *  *");
        src.printAccount(true, true);
        return rc;
    }

    /**
     * Deposit an amount into an account if owned by customer.
     *
     * @param src               source account to have a deposited amount.
     * @param amount            amount to be deposited.
     * @return                  true if success/false if failed.
     */
    public boolean deposit(Account src, double amount) {
        if (!this.accounts.contains(src)) {
            System.out.println("\nWarning: Deposits are only permitted into accounts owned by the customer.");
            return false;
        }
        boolean rc = src.deposit(amount);
        if (rc) {
            String transactionMessage = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) +
                    "," + "DEPOSIT" + ","  + " " + "," + " " + "," +
                    src.getType() + "," + src.getAccountNumber() + "," +
                    String.format("%.2f", amount) + "," + src.getBalance();
            this.addTransaction(transactionMessage);
            System.out.println("\n*  *  *  *  *  *  *  Deposit Successful   *  *  *  *  *  *  *");
        } else {
            System.out.println("\n*  *  *  *  *  *  *    Deposit Failed    *  *  *  *  *  *  *");
        }
        src.printAccount(true, true);
        return rc;
    }

    /**
     * Adds a transaction string to transaction LL.
     *
     * @param transaction string that describes the transaction.
     */
    private void addTransaction(String transaction){
        this.transactions.add(transaction);
    }

    /**
     * Get customers formatted full name.
     *
     * @return customers formatted full name.
     */
    @Override
    public String getFullName() {
        String firstName = this.getFirstName();
        String lastName = this.getLastName();
        return (firstName.isEmpty() ? firstName : Character.toUpperCase(firstName.charAt(0)) + firstName.substring(1).toLowerCase()) +
                " " +
                (lastName.isEmpty() ? lastName : Character.toUpperCase(lastName.charAt(0)) + lastName.substring(1).toLowerCase());
    }
}