import java.util.Scanner;
import java.util.regex.Pattern;

public class TransactionInputHandler extends UserInterface {

    private void depositAmount(Scanner scan, Customer customer, Account account, FileHandler fh) {
        for (int attempts = 0; attempts < 3; attempts++) {
            System.out.print("Enter deposit amount: \n$");
            String depositAmountStr = scan.nextLine();
            double depositAmount = this.validateMoney(depositAmountStr);
            if (depositAmount >= 0) {
                account.deposit(depositAmount, false);
                account.printAccount(true, true);
                fh.appendLog("EPMB_Transactions", customer.getFullName() + " [ID:" + customer.getId() + "] made a deposit of $" + String.format("%.2f", depositAmount) +
                        " to " + account.getType() + " account [Account Number: " + account.getAccountNumber() + "]. Current balance: $" + String.format("%.2f", account.getBalance()));
                return;
            } else {
                System.out.println("Invalid format. Please try again.");
            }
        }
        fh.appendLog("EPMB_Error_Log", customer.getFullName() + " [ID:" + customer.getId() + "] reached maximum attempts.");
        System.out.println("Maximum attempts reached. Returning to main menu.");
    }


    private void withdrawAmount(Scanner scan, Customer customer, Account account, FileHandler fh) {
        for (int attempts = 0; attempts < 3; attempts++) {
            System.out.print("Enter withdrawal amount: \n$");
            String withdrawAmountStr = scan.nextLine();
            double withdrawAmount = this.validateMoney(withdrawAmountStr);
            if (withdrawAmount >= 0) {
                boolean success = account.withdraw(withdrawAmount, false);
                account.printAccount(true, true);
                String logMessage = customer.getFullName() + " [ID:" + customer.getId() + "] attempted a withdrawal of $" + String.format("%.2f", withdrawAmount) +
                        " from " + account.getType() + " account [Account Number:" + account.getAccountNumber() + "]. Current balance: $" + String.format("%.2f", account.getBalance());
                if (success) {
                    fh.appendLog("EPMB_Transactions", logMessage);
                } else {
                    fh.appendLog("EPMB_Error_Log", logMessage + " Reason for failure: Insufficient funds.");
                }
                return;
            } else {
                fh.appendLog("EPMB_Error_Log", customer.getFullName() + " [ID:" + customer.getId() + "] attempted a withdrawal from " +
                        account.getType() + " account [Account Number:" + account.getAccountNumber() + "]. Reason for failure: Inappropriate formatting. Current balance: $" + String.format("%.2f", account.getBalance()));
                System.out.println("Invalid format. Please try again.");
            }
        }
        fh.appendLog("EPMB_Error_Log", customer.getFullName() + " [ID:" + customer.getId() + "] reached maximum attempts.");
        System.out.println("Maximum attempts reached. Returning to main menu.");
    }


    /**
     * Handle a transaction with one account.
     *
     * @param scan         The scanner object to continue taking input.
     */
    public void oneAccountTransaction(Scanner scan, Customer customer, FileHandler fh) {
        InputHandler ih = new InputHandler();
        customer.viewAccounts(false);
        Account account = ih.getAccountForTransaction(scan, customer, fh);
        if (account == null) return;
        for (int attempts = 0; attempts < 3; attempts++) {
            System.out.print("Choose an action:\nA. Inquire Account Details\nB. Deposit\nC. Withdraw\nD. Transfer\n> ");
            String input = scan.nextLine().trim().toLowerCase();
            if (checkExit(input)) return;
            switch (input) {
                case "a":
                    fh.appendLog("EPMB_Transactions", customer.getFullName() + " [ID:" + customer.getId() + "] viewed the details of " + account.getType() + " account [Account Number:" + account.getAccountNumber() +
                            "] Account's Current Balance: $" + String.format("%.2f", account.getBalance()));
                    account.printAccount(true, true);
                    return;
                case "b":
                    depositAmount(scan, customer, account, fh);
                    return;
                case "c":
                    withdrawAmount(scan, customer, account, fh);
                    return;
                case "d":
                    TwoAccountTransaction(scan, customer, account, true, fh);
                    return;
                default:
                    fh.appendLog("EPMB_Error_Log", customer.getFullName() + " [ID:" + customer.getId() + "] Reason for failure: Failed to choose appropriate transaction option.");
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
    public void TwoAccountTransaction(Scanner scan, Customer customerOne, Account accountOne, boolean transfer, FileHandler fh) {
        boolean send = !transfer;
        InputHandler ih = new InputHandler();
        if (send) customerOne.viewAccounts(false);
        if (accountOne == null) {
            accountOne = ih.getAccountForTransaction(scan, customerOne, fh);
            if (accountOne == null) return;
        }
        Account accountTwo;
        Customer customerTwo;
        if (send) {
            System.out.println("Please enter the following for the receiving account:\n" + "-".repeat(51));
            customerTwo = ih.getUserName(scan, true, false, fh);
            if (customerTwo == null) return;
            accountTwo = ih.getAccountForTransaction(scan, customerTwo, fh);
        } else {
            accountTwo = ih.getAccountForTransaction(scan, customerOne, fh);
        }
        if (accountTwo == null) return;
        if (transfer) {
            this.transferAmount(scan, customerOne, accountOne, accountTwo, fh);
        } else {
            this.sendAmount(scan, customerOne, accountOne, accountTwo, fh);
        }    }

    private void transferAmount(Scanner scan, Customer customer, Account accountOne, Account accountTwo, FileHandler fh) {
        for (int attempts = 0; attempts < 3; attempts++) {
            System.out.print("Enter transfer amount: \n$");
            String transferAmountStr = scan.nextLine();
            double transferAmount = this.validateMoney(transferAmountStr);
            if (transferAmount >= 0) {
                boolean rc = customer.transfer(accountOne, accountTwo, transferAmount);
                String logMessage = customer.getFullName() + " [ID:" + customer.getId() + "] attempted a transfer of $" + String.format("%.2f", transferAmount) +
                        " from " + accountOne.getType() + " account [Account Number:" + accountOne.getAccountNumber() +
                        "] to " + accountTwo.getType() + " account [Account Number:" + accountTwo.getAccountNumber() + "]. " +
                        "Account One Current Balance: $" + String.format("%.2f", accountOne.getBalance()) +
                        " Account Two Current Balance: $" + String.format("%.2f", accountTwo.getBalance());
                if (rc) {
                    fh.appendLog("EPMB_Transactions", logMessage);
                } else {
                    fh.appendLog("EPMB_Error_Log", logMessage + " Reason for failure: Insufficient funds.");
                }
            } else {
                fh.appendLog("EPMB_Error_Log", customer.getFullName() + " [ID:" + customer.getId() + "] attempted a transfer of $" + transferAmountStr + " from " +
                        accountOne.getType() + " to " + accountTwo.getType() + " Reason for failure: Inappropriate format.");
                System.out.println("Invalid format. Please try again.");
            }
        }
        fh.appendLog("EPMB_Error_Log", customer.getFullName() + " [ID:" + customer.getId() + "] reached maximum attempts.");
        System.out.println("Maximum attempts reached. Returning to main menu.");
    }

    private void sendAmount(Scanner scan, Customer customerOne, Account accountOne, Account accountTwo, FileHandler fh) {
        for (int attempts = 0; attempts < 3; attempts++) {
            System.out.print("Enter amount to be sent: \n$");
            String sendAmountStr = scan.nextLine();
            double sendAmount = this.validateMoney(sendAmountStr);
            if (sendAmount >= 0) {
                boolean rc = customerOne.send(accountOne, accountTwo, sendAmount);
                String logMessage = customerOne.getFullName() + " [ID:" + customerOne.getId() + "] attempted to send $" + String.format("%.2f", sendAmount) +
                        " from " + accountOne.getType() + " account [Account Number:" + accountOne.getAccountNumber() +
                        "] to " + customerOne.getFullName() + " [ID:" + customerOne.getId() + "] " + accountTwo.getType() + " account [Account Number:" + accountTwo.getAccountNumber() + "]. " +
                        "Account One Current Balance: $" + String.format("%.2f", accountOne.getBalance());
                if (rc) {
                    fh.appendLog("EPMB_Transactions", logMessage);
                    return;
                } else {
                    fh.appendLog("EPMB_Error_Log", logMessage + " Reason for failure: Insufficient funds.");
                }
            } else {
                fh.appendLog("EPMB_Error_Log", customerOne.getFullName() + " [ID:" + customerOne.getId() + "] attempted to send $" + String.format("%.2f", sendAmount) +
                        " from " + accountOne.getType() + " account [Account Number:" + accountOne.getAccountNumber() + "] to " + customerOne.getFullName() + " [ID:" + customerOne.getId() + "] " + accountTwo.getType() + " account [Account Number:" + accountTwo.getAccountNumber() + "]. Reason for Failure: Inappropriate format.");
                System.out.println("Invalid amount. Please try again.");
            }
        }
        fh.appendLog("EPMB_Error_Log", customerOne.getFullName() + " [ID:" + customerOne.getId() + "] reached maximum attempts.");
        System.out.println("Maximum attempts reached. Returning to main menu.");
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
