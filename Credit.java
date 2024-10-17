public class Credit extends Account{
    int creditMax;

    // credit account constructor
    Credit(int accNum, double startBalance, int creditMax){
        super(accNum, startBalance);
        this.creditMax = creditMax;
    }

    // remove money from account
    public boolean withdraw(double amount, boolean suppressSuccess){
        double totalCharge = (amount + (0.025*amount)); // AVERAGE TRANSACTION FEE
        if ((amount > 0) && ((this.balance - totalCharge) <= this.creditMax)) {
            this.balance -= totalCharge;
            if (!suppressSuccess) System.out.println("*  *  *  *  *  *  *  Withdraw Successful  *  *  *  *  *  *  *\n");
            return true;
        }
        System.out.println("*  *  *  *  *  *  *  Insufficient Funds   *  *  *  *  *  *  *\n");
        return false;
    }
}