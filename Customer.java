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
    Customer(int idNum, String firstName, String lastName, String dob, String address, String phoneNum, Account checkingAcc, Account savingsAcc, Credit credit){
        super(firstName, lastName);
        this.idNum = idNum;
        this.dob = dob;
        this.address = address;
        this.phoneNum = phoneNum;
        this.credit = credit;
        accounts.add(checkingAcc);
        accounts.add(savingsAcc);

    }

    // transfer money ONLY from customer owned accounts
    public void transfer(Account src, Account dst, double amount){
        if (this.accounts.contains(src) && this.accounts.contains(dst)){
            boolean rc = src.withdraw(amount);
            if (rc){
                NumberFormat formatter = NumberFormat.getCurrencyInstance();
                dst.deposit(amount);
                System.out.println("Transfer Successful.");
                System.out.println(src.toString());
                System.out.println(dst.toString());
            } else {
                System.out.println("Transfer Failed.");
            }
        }
    }

    // transfer money into someone else's account
    public void send(Account src, Account dst, double amount){
        boolean rc = src.withdraw(amount);
        if (rc){
            dst.deposit(amount);
            System.out.println("Transfer Successful");
            System.out.println(src.toString());
        } else {
            System.out.println("Transfer Failed.");
        }
    }
}
