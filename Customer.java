import java.text.NumberFormat;
import java.util.ArrayList;

/**
 * Represents a customer with their accounts, dob, address, phone number, and unique id number
 */
class Customer extends Person{
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
     * @param checkingAcc   The customer's checking account.
     * @param savingsAcc    The customer's savings account.
     * @param creditAcc     The customer's credit account.
     */
    Customer(int idNum, String firstName, String lastName, String dob, String address, String phoneNum, Account checkingAcc, Account savingsAcc, Credit creditAcc){
        super(firstName, lastName);
        this.idNum = idNum;
        this.dob = dob;
        this.address = address;
        this.phoneNum = phoneNum;
        accounts.add(checkingAcc);
        accounts.add(savingsAcc);
        accounts.add(creditAcc);
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
        if (this.accounts.contains(src) && this.accounts.contains(dst)){
            boolean rc = src.withdraw(amount, true);
            if (rc){
                dst.deposit(amount, true);
                src.printAccount(true);
                dst.printAccount(true);
                System.out.println("*  *  *  *  *  *  *  Transfer Successful  *  *  *  *  *  *  *\n");
                return true;
            }
        }
        System.out.println("*  *  *  *  *  *  *    Transfer Failed    *  *  *  *  *  *  *\n");
        return false;
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
        if (src.withdraw(amount, true)) {
            dst.deposit(amount, true);
            src.printAccount(true);
            System.out.println("*  *  *  *  *  *  *    Send Successful    *  *  *  *  *  *  *\n");
            return true;
        }
        System.out.println("*  *  *  *  *  *  *      Send Failed      *  *  *  *  *  *  *\n");
        return false;
    }
}