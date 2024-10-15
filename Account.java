import java.util.Random;

abstract class Account {
    int accNum;
    double balance;

    // account constructor
    Account(int accNum, double startBalance){
        this.accNum = accNum;
        this.balance = startBalance;
    }

    // add money to account balance
    public void deposit(double amount){
        this.balance += amount;
    }

    // remove money, return success/fail
    public boolean withdraw(double amount){
        if (amount <= this.balance && amount > 0){
            this.balance -= amount;
            return true;
        }
        return false;
    }
}
