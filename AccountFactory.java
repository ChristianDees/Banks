/**
 * Represents factory of creating accounts based on type.
 */
public class AccountFactory {

    /**
     * Get account based on type.
     *
     * @param accountType   type of account requested.
     * @param accNum        account number to be added to account.
     * @param startBalance  account's start balance.
     * @param limit         account's limit.
     * @return              new instance of account of requested type.
     */
    public static Account getAccount(String accountType, int accNum, double startBalance, int limit){
        accountType = accountType.toLowerCase().trim();
        // return account type based on requested string of account type
        return switch (accountType) {
            case "checking" -> new Checking(accNum, startBalance);
            case "savings" -> new Savings(accNum, startBalance);
            case "credit" -> new Credit(accNum, startBalance, limit);
            default -> null;
        };
    }
}
