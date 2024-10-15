import java.util.Random;

abstract class Account {
    int accountNumber;
    double accountBalance;

    // account constructor
    Account(double amount){
        this.accountNumber = new Random().nextInt(10000000);
        this.accountBalance = amount;
    }

    // add money to account balance
    public void deposit(double amount){
        this.accountBalance += amount;
    }

    // remove money, return success/fail
    public boolean withdraw(double amount){
        if (amount <= this.accountBalance && amount > 0){
            this.accountBalance -= amount;
            return true;
        }
        return false;
    }
}
