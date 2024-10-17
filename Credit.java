/**
 * Represents a credit account with its balance, unique account number, and a maximum allowed credit debt
 */

public class Credit extends Account{
    int creditMax;

    /**
     * Constructs a new Credit Account with the specified attributes.
     *
     * @param accNum         The unique id number of the account.
     * @param startBalance   The credit account's starting balance.
     * @param creditMax      The credit account's maximum allowed debt.
     */
    Credit(int accNum, double startBalance, int creditMax){
        super(accNum, startBalance);
        this.creditMax = creditMax;
    }

    /**
     * Takes a specified amount out from one customer's account.
     *
     * @param amount            The total amount to be withdrawn from the account.
     * @param suppressSuccess   Flag to not print the success of the withdrawal or not.
     *
     * **/
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