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

    public void addAccount(Account account){
        this.accounts.add(account);
    }

    public int getId(){
        return this.idNum;
    }

    public String getFirstName(){
        return this.firstName;
    }

    public String getLastName(){
        return this.lastName;
    }

    public String getDob(){
        return this.dob;
    }

    public String getAddress(){
        return this.address;
    }

    public String getPhoneNum(){
        return this.phoneNum;
    }

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