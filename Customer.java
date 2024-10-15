import java.util.ArrayList;

class Customer extends Person{
    ArrayList<Account> accounts = new ArrayList<Account>();
    String address;
    int phoneNumber;


    // customer constructor
    Customer(String firstName, String lastName){
        super(firstName, lastName);
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
