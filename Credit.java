public class Credit {
    int CreditAccountNumber;
    int creditMax;
    double balance;


    // return success/fail
    public boolean charge(double amount){
        if (amount > 0 && amount <= this.creditMax){
            this.balance -= amount;
            return true;
        }
        return false;
    }
}