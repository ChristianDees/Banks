import java.text.NumberFormat;
import java.util.ArrayList;

class Customer extends Person{
    ArrayList<Account> accounts = new ArrayList<Account>();
    String dob;
    String address;
    String phoneNum;
    int idNum;
    Credit credit;


    // customer constructor
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

    // transfer money ONLY from customer owned accounts
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

    // transfer money into someone else's account
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
