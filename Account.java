import java.text.NumberFormat;
import java.util.ArrayList;
import java.text.NumberFormat;

abstract class Account {
    int accNum;
    double balance;

    // account constructor
    Account(int accNum, double startBalance){
        this.accNum = accNum;
        this.balance = startBalance;
    }

    // getter for balance
    public double getBalance() {
        return this.balance;
    }

    public int getAccountNumber(){
        return this.accNum;
    }

    // getter for type
    public String getType(){
        return this.getClass().getName();
    }

    // add money to account balance
    public void deposit(double amount, boolean suppressSuccess){
        this.balance += amount;
        if (!suppressSuccess) System.out.println("*  *  *  *  *  *  *  Deposit Successful   *  *  *  *  *  *  *\n");
    }

    // remove money, return success/fail
    public boolean withdraw(double amount, boolean suppressSuccess){
        if (amount <= this.balance && amount > 0){
            this.balance -= amount;
            if (!suppressSuccess) System.out.println("*  *  *  *  *  *  *  Withdraw Successful  *  *  *  *  *  *  *\n");
            return true;
        }
        System.out.println("*  *  *  *  *  *  *  Insufficient Funds   *  *  *  *  *  *  *\n");
        return false;
    }

    // print attributes
    public void printAccount(boolean allData) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance();
        if (allData) {
            System.out.printf("| %-15s | %-20s | %-16s |\n",
                    this.getType(),
                    this.getAccountNumber(),
                    formatter.format(this.getBalance()));
            System.out.println("+-----------------+----------------------+------------------+");
        } else {
            System.out.printf("| %-15s | %-20s |\n",
                    this.getType(),
                    this.getAccountNumber());
            System.out.println("+-----------------+----------------------+");
        }
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Account account = (Account) obj;
        return accNum == (account.accNum);
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(accNum);
    }


}
