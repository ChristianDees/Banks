/**
 * Represents a checking account with its balance and unique account number
 */
public class Checking extends Account{
    /**
     * Constructs a new checking Account with the specified attributes.
     *
     * @param accNum         The unique id number of the account.
     * @param startBalance   The checking account's starting balance.
     */
    Checking(int accNum, double startBalance){
        super(accNum, startBalance);
    }
}
