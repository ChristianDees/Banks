import java.text.NumberFormat;

/**
 * Represents an Account with its unique account number and current balance
 */

abstract class Account {
    int accNum;
    double balance;

    /**
     * Constructs a new Account with the specified attributes.
     *
     * @param accNum         The unique id number of the account.
     * @param startBalance   The account's starting balance.
     */
    Account(int accNum, double startBalance){
        this.accNum = accNum;
        this.balance = startBalance;
    }

    /**
     * Get the account's current balance.
     *
     * @return  The current balance of the account.
     * **/
    public double getBalance() {
        return this.balance;
    }

    /**
     * Get the account's account number.
     *
     * @return  The account's account number.
     * **/
    public int getAccountNumber(){
        return this.accNum;
    }

    /**
     * Get the account's type.
     *
     * @return  The account's type.
     * **/
    public String getType(){
        return this.getClass().getName();
    }

    /**
     * Add money to an account.
     **/
    public void deposit(double amount, boolean suppressSuccess){
        this.balance += amount;
        if (!suppressSuccess) System.out.println("\n*  *  *  *  *  *  *  Deposit Successful   *  *  *  *  *  *  *");
    }

    /**
     * Take money out of an account.
     *
     * @return  The successfulness of a withdrawal.
     * **/
    public boolean withdraw(double amount, boolean suppressSuccess){
        if (amount <= this.balance && amount > 0){
            this.balance -= amount;
            if (!suppressSuccess) System.out.println("\n*  *  *  *  *  *  *  Withdraw Successful  *  *  *  *  *  *  *");
            return true;
        }
        System.out.println("\n*  *  *  *  *  *  *  Insufficient Funds   *  *  *  *  *  *  *");
        return false;
    }

    /**
     * Print attributes of an account
     *
     * @param showBalance  Print the balance along with other attributes.
     **/
    public void printAccount(boolean showBalance) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance();
        if (showBalance) {
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

    /**
     * Compares this Account object to the specified object for equality.
     * Two Account objects are considered equal if they have the same account number.
     *
     * @param obj the object to be compared for equality with this Account
     * @return true if the specified object is equal to this Account;
     *         false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Account account = (Account) obj;
        return accNum == (account.accNum);
    }

    /**
     * Returns a hash code value for this Account object.
     * The hash code is computed based on the account number.
     *
     * @return a hash code value for this Account
     */
    @Override
    public int hashCode() {
        return Integer.hashCode(accNum);
    }

}