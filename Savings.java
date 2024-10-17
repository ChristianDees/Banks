/**
 * Represents a savings account with its balance and unique account number
 */

public class Savings extends Account{
    /**
     * Constructs a new savings Account with the specified attributes.
     *
     * @param accNum         The unique id number of the account.
     * @param startBalance   The savings account's starting balance.
     */
    Savings(int accNum, double startBalance){
        super(accNum, startBalance);
    }
}
