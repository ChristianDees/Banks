import java.util.Scanner;
import java.util.regex.Pattern;

public class TransactionInputHandler extends UserInterface {

    private void depositAmount(Scanner scan, Account account) {
        for (int attempts = 0; attempts < 3; attempts++) {
            System.out.print("Enter deposit amount: \n$");
            String depositAmountStr = scan.nextLine();
            double depositAmount = this.validateMoney(depositAmountStr);
            if (depositAmount >= 0) {
                account.deposit(depositAmount, false);
                account.printAccount(true, true);
                return;
            } else {
                System.out.println("Invalid amount. Please try again.");
            }
        }
        System.out.println("Maximum attempts reached. Returning to main menu.");
    }

    private void withdrawAmount(Scanner scan, Account account) {
        for (int attempts = 0; attempts < 3; attempts++) {
            System.out.print("Enter withdrawal amount: \n$");
            String withdrawAmountStr = scan.nextLine();
            double withdrawAmount = this.validateMoney(withdrawAmountStr);
            if (withdrawAmount >= 0) {
                account.withdraw(withdrawAmount, false);
                account.printAccount(true, true);
                return;
            } else {
                System.out.println("Invalid amount. Please try again.");
            }
        }
        System.out.println("Maximum attempts reached. Returning to main menu.");
    }

    /**
     * Handle a transaction with one account.
     *
     * @param scan         The scanner object to continue taking input.
     */
    public void oneAccountTransaction(Scanner scan, Customer customer) {
        InputHandler ih = new InputHandler();
        customer.viewAccounts(false);
        Account account = ih.getAccountForTransaction(scan, customer);
        if (account == null) return;
        for (int attempts = 0; attempts < 3; attempts++) {
            System.out.print("Choose an action:\nA. Inquire Account Details\nB. Deposit\nC. Withdraw\nD. Transfer\n> ");
            String input = scan.nextLine().trim().toLowerCase();
            if (checkExit(input)) return;
            switch (input) {
                case "a":
                    account.printAccount(true, true);
                    return;
                case "b":
                    depositAmount(scan, account);
                    return;
                case "c":
                    withdrawAmount(scan, account);
                    return;
                case "d":
                    TwoAccountTransaction(scan, customer, account, true);
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    /**
     * Handle a transaction between two accounts.
     *
     * @param scan          The scanner object to continue taking input.
     * @param customerOne   The first customer involved in the transaction.
     * @param accountOne    The first account associated in the transaction.
     */
    public void TwoAccountTransaction(Scanner scan, Customer customerOne, Account accountOne, boolean transfer) {
        boolean send = !transfer;
        InputHandler ih = new InputHandler();
        if (send) customerOne.viewAccounts(false);
        if (accountOne == null) {
            accountOne = ih.getAccountForTransaction(scan, customerOne);
            if (accountOne == null) return;
        }
        Account accountTwo;
        Customer customerTwo;
        if (send) {
            System.out.println("Please enter the following for the receiving account:\n" + "-".repeat(51));
            customerTwo = ih.getUserName(scan, true, false);
            if (customerTwo == null) return;
            accountTwo = ih.getAccountForTransaction(scan, customerTwo);
        } else {
            accountTwo = ih.getAccountForTransaction(scan, customerOne);
        }
        if (accountTwo == null) return;
        boolean rc = transfer ? this.transferAmount(scan, customerOne, accountOne, accountTwo) : this.sendAmount(scan, customerOne, accountOne, accountTwo);
        FileHandler fw = new FileHandler();
        if (rc) {
            String transactionType = transfer ? "transfer" : "send";
            fw.appendLog("transactions", transactionType + " successful");
        }
    }

    private boolean transferAmount(Scanner scan, Customer customer, Account accountOne, Account accountTwo) {
        for (int attempts = 0; attempts < 3; attempts++) {
            System.out.print("Enter transfer amount: \n$");
            String transferAmountStr = scan.nextLine();
            double transferAmount = this.validateMoney(transferAmountStr);
            if (transferAmount >= 0) {
                return customer.transfer(accountOne, accountTwo, transferAmount);
            } else {
                System.out.println("Invalid format or amount. Please try again.");
            }
        }
        System.out.println("Maximum attempts reached. Returning to main menu.");
        return false;
    }

    private boolean sendAmount(Scanner scan, Customer customerOne, Account accountOne, Account accountTwo) {
        for (int attempts = 0; attempts < 3; attempts++) {
            System.out.print("Enter transfer amount: \n$");
            String sendAmountStr = scan.nextLine();
            double sendAmount = this.validateMoney(sendAmountStr);
            if (sendAmount >= 0) {
                return customerOne.send(accountOne, accountTwo, sendAmount);
            } else {
                System.out.println("Invalid amount. Please try again.");
            }
        }
        System.out.println("Maximum attempts reached. Returning to main menu.");
        return false;
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

    private double validateMoney(String input) {
        boolean correctFormat = Pattern.matches("^(\\d{1,3}(,\\d{3})*(\\.\\d{1,2})?|\\d+(\\.\\d{1,2})?)$", input);
        if (correctFormat)
            return Double.parseDouble(input.replace(",", ""));
        return -1;
    }
}
