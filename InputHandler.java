import java.util.ArrayList;
import java.util.Scanner;

public class InputHandler extends UserInterface{
    public void displayWelcomeMessage(){
        String message = "El Paso Miner's Bank";
        int length = message.length();
        System.out.println("+" + "-".repeat(length + 2) + "+");
        System.out.println("| " + message + " |");
        System.out.println("+" + "-".repeat(length + 2) + "+");
        System.out.println("Type 'exit' to quit.");
    }
    /**
     * Query user for the type of account to handle with.
     *
     * @param scan          The scanner object to continue taking input.
     * @return              The account type if it exists.
     */

    private String getAccountType(Scanner scan) {
        for (int attempts = 0; attempts < 3; attempts++) {
            System.out.println("What is the account type? (checking/savings/credit):");
            String accType = scan.nextLine().trim();
            if (checkExit(accType)) return null;
            if ("checking".equalsIgnoreCase(accType) || "savings".equalsIgnoreCase(accType) || "credit".equalsIgnoreCase(accType)) return accType;
            System.out.println("Invalid account type. Please enter 'checking', 'savings', or 'credit'.");
        }
        return null;
    }

    /**
     * Query user for an account number and return the respective account.
     *
     * @param scan          The scanner object to continue taking input.
     * @return              The account that has the provided account type and account number.
     */
    private static Account getAccountInfo(Scanner scan, String accType) {
        for (int attempts = 0; attempts < 3; attempts++) {
            System.out.println("What is the account number? ");
            String input = scan.nextLine().trim();
            if (checkExit(input)) return null;
            try {
                int accNum = Integer.parseInt(input);
                TransactionInputHandler th = new TransactionInputHandler();
                Account account = th.getAccount(accType, accNum);
                if (account != null){
                    return account;
                } else {
                    System.out.println("That account does not exist.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid account number.");
            }
        }
        return null;
    }

    /**
     * Query user for type of account and print the account information.
     *
     * @param scan The scanner object to continue taking input.
     */
    private void inquireByAccount(Scanner scan) {
        String accType = getAccountType(scan);
        if (accType != null) {
            Account account = getAccountInfo(scan, accType);
            if (account != null) {
                account.printHeader(true);
                account.printAccount(true);
            }
        }
    }

    /**
     * Handle a transaction with one account.
     *
     * @param scan          The scanner object to continue taking input.
     * @param showBalance   Flag to show balance.
     */
    public String getUserName(Scanner scan, boolean showBalance) {
        for (int attempts = 0; attempts < 3; attempts++) {
            System.out.println("Enter customer id and name (id, name): ");
            String input = scan.nextLine().trim();
            if (checkExit(input)) return null;
            String[] parts = input.split(",");
            if (parts.length != 2) {
                System.out.println("Invalid format. Use 'id, name'.");
                continue;
            }
            String formattedName = parts[0].trim() + parts[1].trim().toLowerCase().replace(" ", "");
            if (!checkExit(formattedName)){
                Customer customer = customers.get(formattedName);
                if (customer != null){
                    customer.viewAccounts(showBalance);
                    return formattedName;
                }
                System.out.println("There is no account under that name associated with this bank.");
            }
        }
        return null;
    }

    private void getUserRole(Scanner scan){
        System.out.println("Please enter your role (customer/manager):");
        String role = scan.nextLine().trim().toLowerCase();
        switch (role) {
            case "customer" -> handleCustomer(scan);
            case "manager" -> handleManager(scan);
            case "exit" -> exit = true;
            default -> System.out.println("Invalid option. Please choose 'customer' or 'manager'.");
        }
    }

    /**
     * Query for the total transactions the user will perform.
     *
     * @param scan          The scanner object to continue taking input.
     */
    private void handleCustomer(Scanner scan) {
        int attempts = 0;
        while(attempts < 3){
            if (exit) return;
            System.out.println("Choose a transaction:\nA. Transaction between single person.\nB. Transaction between two people.");
            String input = scan.nextLine().trim().toLowerCase();
            if (checkExit(input)) return;
            TransactionInputHandler transaction = new TransactionInputHandler();
            switch (input) {
                case "a":
                    transaction.oneAccountTransaction(scan);
                    break;
                case "b":
                    transaction.TwoAccountTransaction(scan, null, null);
                    break;
                default:
                    System.out.println("Invalid option. Please choose 'A' or 'B'.");
                    attempts++;
            }
        }
        System.out.println("Too many attempts. Please log in again.\n");
    }

    /**
     * Handle the interaction for a manager.
     *
     * @param scan The scanner object to continue taking input.
     */
    private void handleManager(Scanner scan) {
        int attempts = 0;
        while(attempts < 3){
            if (exit) return;
            System.out.println("A. Inquire accounts by customer name and id.\nB. Inquire account by type/number.");
            String input = scan.nextLine().trim().toLowerCase();
            if (checkExit(input)) return;
            switch (input) {
                case "a":
                    getUserName(scan, true);
                    break;
                case "b":
                    inquireByAccount(scan);
                    break;
                default:
                    System.out.println("Invalid option. Please choose 'A' or 'B'.");
                    attempts++;
            }
        }
        System.out.println("Too many attempts. Please log in again.\n");
    }


    /**
     * Ask user for an account and return it.
     *
     * @param scan          The scanner object to continue taking input.
     * @param customer      The customer who is answering the question.
     */
    public Account getAccountForTransaction(Scanner scan, Customer customer) {
        for (int attempts = 0; attempts < 3; attempts++) {
            System.out.println("Specify the account (type, number):");
            String input = scan.nextLine().trim().toLowerCase();
            String[] parts = input.split(",");
            if (checkExit(input)) return null;
            if (parts.length != 2) {
                System.out.println("Invalid format.");
                continue;
            }
            String accType = parts[0].trim();
            try {
                int accNum = Integer.parseInt(parts[1].trim());
                TransactionInputHandler th = new TransactionInputHandler();
                Account account = th.getAccount(accType, accNum);
                if (account != null) {
                    ArrayList<Account> accounts = customer.accounts;
                    if (accounts.contains(account)) {
                        return account;
                    }else {
                        System.out.println("You don't own that account! Please try again.");
                    }
                } else {
                    System.out.println("Account not found. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid account number. Please try again.");
            }
        }
        return null;
    }


    public void handleInput(){
        Scanner scan = new Scanner(System.in);
        while (!exit) {
            this.getUserRole(scan);
        }
        System.out.println("Exiting...");
    }
}
