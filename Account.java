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
        return balance;
    }

    // getter for type
    public String getType(){
        return this.getClass().getName();
    }

    // add money to account balance
    public void deposit(double amount){
        this.balance += amount;
        System.out.println("Deposit Successful.");
    }

    // remove money, return success/fail
    public boolean withdraw(double amount){
        if (amount <= this.balance && amount > 0){
            this.balance -= amount;
            System.out.println("Withdraw Successful.");
            return true;
        }
        System.out.println("Insufficient funds.");
        return false;
    }

    // print attributes
    public String toString(){
        return "Account Type: " + this.getType() + "\nAccount number: " + Integer.toString(this.accNum) + "\nBalance: " + Double.toString(this.balance) + "\n";
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
