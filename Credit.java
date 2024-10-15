public class Credit {
    int accNum;
    int creditMax;
    double balance;

    Credit(int accNum, int creditMax, double startBalance){
        this.accNum = accNum;
        this.creditMax = creditMax;
        this.balance = startBalance;
    }

    // return success/fail
    public boolean charge(double amount){
        if (amount > 0 && amount <= this.creditMax){
            this.balance -= amount;
            return true;
        }
        return false;
    }
}