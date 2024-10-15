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


    // move money from one account to another, return success/fail
    public boolean transfer(Account src, Account dst, double amount){
        boolean rc = src.withdraw(amount);
        if (rc) {
            dst.deposit(amount);
            return true;
        }
        return false;
    }
}
