import java.util.Scanner;

public class TransactionInputHandler extends UserInterface {

    /**
     * Query the user for their type of transaction and perform it.
     *
     * @param scan        The scanner object to continue taking input.
     * @param account     The account provided to perform an action on.
     * @param customer    The customer provided to perform an action.
     */
    public void performTransaction(Scanner scan, Account account, Customer customer) {
        for (int attempts = 0; attempts < 3; attempts++) {
            System.out.println("Choose an action:\nA. Inquire Account Details\nB. Deposit\nC. Withdraw\nD. Transfer");
            String input = scan.nextLine().trim().toLowerCase();
            if (checkExit(input)) return;
            switch (input) {
                case "a":
                    account.printHeader(true);
                    account.printAccount(true);
                    return;
                case "b":
                    System.out.print("Enter deposit amount: \n$");
                    double depositAmount;
                    try {
                        depositAmount = Double.parseDouble(scan.nextLine().trim().replace(",",""));
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input. Please enter a valid amount.");
                        break;
                    }
                    account.deposit(depositAmount, false);
                    account.printHeader(true);
                    account.printAccount(true);
                    return;
                case "c":
                    System.out.print("Enter withdrawal amount: \n$");
                    double withdrawAmount;
                    try {
                        withdrawAmount = Double.parseDouble(scan.nextLine().trim().replace(",",""));
                    } catch (NumberFormatException e) {
                        System.out.print("Invalid input. Please enter a valid amount.");
                        break;
                    }
                    account.withdraw(withdrawAmount, false);
                    account.printHeader(true);
                    account.printAccount(true);
                    return;
                case "d":
                    TwoAccountTransaction(scan, customer, account);
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    /**
     * Handle a transaction with one account.
     *
     * @param scan         The scanner object to continue taking input.
     */
    public void oneAccountTransaction(Scanner scan) {
        InputHandler ih = new InputHandler();
        String name = ih.getUserName(scan, false);
        if (name == null) return;
        Customer customer = customers.get(name);
        Account account = ih.getAccountForTransaction(scan, customer);
        if (account == null) return;
        this.performTransaction(scan, account, customer);
    }

    /**
     * Handle a transaction between two accounts.
     *
     * @param scan          The scanner object to continue taking input.
     * @param customerOne   The first customer involved in the transaction.
     * @param accountOne    The first account associated in the transaction.
     */
    public void TwoAccountTransaction(Scanner scan, Customer customerOne, Account accountOne) {
        InputHandler ih = new InputHandler();
        if (customerOne == null) {
            String customerNameOne = ih.getUserName(scan, false);
            if (customerNameOne == null) return;
            customerOne = customers.get(customerNameOne);
            accountOne = ih.getAccountForTransaction(scan, customerOne);
            if (accountOne == null) return;  // Early exit if no account
        } else {
            System.out.println("Enter the account you would like to transfer funds into.");
        }

        String customerNameTwo = ih.getUserName(scan, false);
        if (customerNameTwo == null) return;

        Customer customerTwo = customers.get(customerNameTwo);
        Account accountTwo = ih.getAccountForTransaction(scan, customerTwo);
        if (accountTwo == null || customerTwo == null) return;

        double amount = getTransferAmount(scan);
        if (amount <= 0) return;

        executeTransaction(customerOne, accountOne, customerTwo, accountTwo, amount);
    }

    private double getTransferAmount(Scanner scan) {
        double amount;
        System.out.print("Enter transfer amount: \n$");
        for (int attempts = 0; attempts < 3; attempts++) {
            try {
                amount = Double.parseDouble(scan.nextLine().trim());
                if (amount > 0) return amount; // Valid amount
                System.out.println("Amount must be greater than zero.");
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
        return -1;
    }

    private void executeTransaction(Customer customerOne, Account accountOne, Customer customerTwo, Account accountTwo, double amount) {
        boolean isTransfer = customerOne.equals(customerTwo);
        accountOne.printHeader(true);
        boolean rc;
        FileHandler fw = new FileHandler();
        if (isTransfer) {
            rc = customerOne.transfer(accountOne, accountTwo, amount);
            // Update log here if rc is true
        } else {
            rc = customerOne.send(accountOne, accountTwo, amount);
            // Update log here if rc is true
        }
        if (rc) fw.appendLog("transactions", "enter message here");
        else fw.appendLog("errorLog", "enter message here");
    }

    /**
     * Return account from the respective array lists.
     *
     * @param accType     The account type the user provided.
     * @param accNum      The account number the user provided.
     * @return            The account if it exists.
     */
    public Account getAccount(String accType, int accNum) {
        return "checking".equalsIgnoreCase(accType) ? checkingAccounts.get(accNum) :
                "savings".equalsIgnoreCase(accType) ? savingAccounts.get(accNum) :
                        "credit".equalsIgnoreCase(accType) ? creditAccounts.get(accNum) : null;
    }
}
