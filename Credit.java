public class Credit extends Account{
    int creditMax;

    // credit account constructor
    Credit(int accNum, double startBalance, int creditMax){
        super(accNum, startBalance);
        this.creditMax = creditMax;
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