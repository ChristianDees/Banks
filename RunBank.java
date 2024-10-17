import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Dictionary;
import java.util.Hashtable;

public class RunBank {
    private static final Dictionary<String, Customer> customers = new Hashtable<>();
    private static final Dictionary<Integer, Checking> checkingAccounts = new Hashtable<>();
    private static final Dictionary<Integer, Savings> savingAccounts = new Hashtable<>();
    private static final Dictionary<Integer, Credit> creditAccounts = new Hashtable<>();
    private static boolean exit = false;

    public static void loadFromCSV(String filename) {
        try (Scanner scan = new Scanner(new File(filename))) {
            scan.nextLine();
            while (scan.hasNextLine()) {
                String[] personInfo = scan.nextLine().split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                int idNum = Integer.parseInt(personInfo[0]);
                String firstName = personInfo[1].toLowerCase();
                String lastName = personInfo[2].toLowerCase();
                String dob = personInfo[3];
                String address = personInfo[4].replace("\"", "");
                String phoneNum = personInfo[5].replaceAll("[()\\s-]", "");
                int checkingAccNum = Integer.parseInt(personInfo[6]);
                double checkingStartBalance = Double.parseDouble(personInfo[7]);
                int savingAccNum = Integer.parseInt(personInfo[8]);
                double savingStartBalance = Double.parseDouble(personInfo[9]);
                int creditAccNum = Integer.parseInt(personInfo[10]);
                int creditMax = Integer.parseInt(personInfo[11]);
                double creditStartBalance = Double.parseDouble(personInfo[12]);
                Checking checkingAccount = new Checking(checkingAccNum, checkingStartBalance);
                Savings savingsAccount = new Savings(savingAccNum, savingStartBalance);
                Credit creditAccount = new Credit(creditAccNum, creditStartBalance, creditMax);
                customers.put(idNum + firstName + lastName, new Customer(idNum, firstName, lastName, dob, address, phoneNum, checkingAccount, savingsAccount, creditAccount));
                checkingAccounts.put(checkingAccNum, checkingAccount);
                savingAccounts.put(savingAccNum, savingsAccount);
                creditAccounts.put(creditAccNum, creditAccount);
            }
        } catch (Exception e) {
            System.out.println("Error loading from file: " + e.getMessage());
        }
    }

    public static void updateCSV(String filename){
        // loop through every customer upon exit, update row with (id, name, date of birth, phone number, Checking Account Number, Checking Starting Balance, Savings Account Number, Savings Starting Balance, Credit Account Number, Credit Max, Credit Starting Balance)
    }

    private static void printHeader(boolean metadata){
        if (metadata) {
            // full header
            System.out.println("+-----------------+----------------------+------------------+");
            System.out.printf("| %-15s | %-20s | %-16s |\n", "Type", "Account Number", "Balance");
            System.out.println("+-----------------+----------------------+------------------+");
        } else {
            // partial header
            System.out.println("+-----------------+----------------------+");
            System.out.printf("| %-15s | %-20s |\n", "Type", "Account Number");
            System.out.println("+-----------------+----------------------+");
        }
    }

    private static boolean viewAccounts(String name, boolean metadata) {
        Customer customer = customers.get(name);
        if (customer == null) {
            System.out.println("That person does not have any accounts with us!");
            return false;
        }
        System.out.println("Accounts:");
        printHeader(metadata);
        customer.accounts.forEach(account -> account.printAccount(metadata));
        return true;
    }

    private static void handleCustomer(Scanner scan) {
        int attempts = 0;
        while(attempts < 3){
            if (exit) return;
            System.out.println("A. Transaction between single person.\nB. Transaction between two people.");
            String input = scan.nextLine().trim().toLowerCase();
            if (checkExit(input)) return;
            switch (input) {
                case "a":
                    oneAccountTransaction(scan);
                    break;
                case "b":
                    TwoAccountTransaction(scan, null, null);
                    break;
                default:
                    System.out.println("Invalid option. Please choose 'A' or 'B'.");
                    attempts++;
            }
        }
        System.out.println("Too many attempts. Please log in again.\n");
    }

    private static void TwoAccountTransaction(Scanner scan, Customer customerOne, Account accountOne) {
        boolean isTransfer = customerOne != null;
        String customerNametwo = customerOne != null ? customerOne.getFullName(): "";
        if (!isTransfer) {
            String customerNameOne = getUserName(scan, false);
            if (customerNameOne==null)return;
            customerNametwo = getUserName(scan, false);
            if (customerNametwo==null)return;
            customerOne = customers.get(customerNameOne);
            accountOne = getAccountForTransaction(scan, customerOne);
        } else {
            System.out.println("Enter the account you would like to transfer funds into.");
        }
        Customer customerTwo = customers.get(customerNametwo);
        Account accountTwo = getAccountForTransaction(scan, customerTwo);
        double amount = 0;
        System.out.println("How much would you like to transfer:");
        for (int attempts = 0; attempts < 3; attempts++) {
            try {
                amount = Double.parseDouble(scan.nextLine().trim());
                break;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
        if (accountOne != null && accountTwo != null && customerOne != null) {
            boolean rc = false;
            printHeader(true);
            if (isTransfer) {
                rc = customerOne.transfer(accountOne, accountTwo, amount);
                // UPDATE CSV HERE IF RC IS TRUE
            } else {
                rc = customerOne.send(accountOne, accountTwo, amount);
                // UPDATE CSV HERE IF RC IS TRUE
            }
        }
    }

    private static void oneAccountTransaction(Scanner scan) {
        String name = getUserName(scan, false);
        if (name == null) return;
        Customer customer = customers.get(name);
        Account account = getAccountForTransaction(scan, customer);
        if (account == null) return;
        performTransaction(scan, account, customer);
    }

    private static String getUserName(Scanner scan, boolean metadata) {
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
            if (inquireByName(scan, formattedName, metadata)) {
                return formattedName;
            }
        }
        return null;
    }

    private static Account getAccountForTransaction(Scanner scan, Customer customer) {
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
                Account account = getAccount(accType, accNum);
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

    private static Account getAccount(String accType, int accNum) {
        return "checking".equalsIgnoreCase(accType) ? checkingAccounts.get(accNum) :
                "savings".equalsIgnoreCase(accType) ? savingAccounts.get(accNum) :
                "credit".equalsIgnoreCase(accType) ? creditAccounts.get(accNum) : null;
    }

    private static void performTransaction(Scanner scan, Account account, Customer customer) {
        for (int attempts = 0; attempts < 3; attempts++) {
            System.out.println("Choose an action:\nA. Inquire Account Details\nB. Deposit\nC. Withdraw\nD. Transfer");
            String input = scan.nextLine().trim().toLowerCase();
            if (checkExit(input)) return;
            switch (input) {
                case "a":
                    printHeader(true);
                    account.printAccount(true);
                    return;
                case "b":
                    System.out.print("Enter deposit amount: ");
                    double depositAmount = 0;
                    try {
                        depositAmount = Double.parseDouble(scan.nextLine());
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input. Please enter a valid amount.");
                        break;
                    }
                    printHeader(true);
                    account.printAccount(true);
                    account.deposit(depositAmount, false);
                    return;
                case "c":
                    System.out.print("Enter withdrawal amount: ");
                    double withdrawAmount = 0;
                    try {
                        withdrawAmount = Double.parseDouble(scan.nextLine());
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input. Please enter a valid amount.");
                        break;
                    }
                    printHeader(true);
                    account.printAccount(true);
                    account.withdraw(withdrawAmount, false);
                    return;
                case "d":
                    TwoAccountTransaction(scan, customer, account);
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private static void handleManager(Scanner scan) {
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

    private static boolean inquireByName(Scanner scan, String name, boolean metadata) {
        return checkExit(name) || viewAccounts(name, metadata);
    }

    private static void inquireByAccount(Scanner scan) {
        String accType = getAccountType(scan);
        if (accType != null) {
            Account account = getAccountInfo(scan, accType);
            if (account != null) {
                printHeader(true);
                account.printAccount(true);
            }
        }
    }

    private static String getAccountType(Scanner scan) {
        for (int attempts = 0; attempts < 3; attempts++) {
            System.out.println("What is the account type? (checking/savings/credit):");
            String accType = scan.nextLine().trim();
            if (checkExit(accType)) return null;
            if ("checking".equalsIgnoreCase(accType) || "savings".equalsIgnoreCase(accType) || "credit".equalsIgnoreCase(accType)) return accType;
            System.out.println("Invalid account type. Please enter 'checking', 'savings', or 'credit'.");
        }
        return null;
    }

    private static Account getAccountInfo(Scanner scan, String accType) {
        for (int attempts = 0; attempts < 3; attempts++) {
            System.out.println("What is the account number? ");
            String input = scan.nextLine().trim();
            if (checkExit(input)) return null;

            try {
                int accNum = Integer.parseInt(input);
                Account account = getAccount(accType, accNum);
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

    private static boolean checkExit(String input) {
        return input.equalsIgnoreCase("exit") && (exit = true);
    }

    public static void main(String[] args) {
        loadFromCSV("bankUsers.csv");
        Scanner scan = new Scanner(System.in);
        String message = "El Paso Miner's Bank";
        int length = message.length();
        System.out.println("+" + "-".repeat(length + 2) + "+");
        System.out.println("| " + message + " |");
        System.out.println("+" + "-".repeat(length + 2) + "+");
        System.out.println("Type 'exit' to quit.");
        while (!exit) {
            System.out.println("Please enter your role (customer/manager):");
            String role = scan.nextLine().trim().toLowerCase();
            switch (role) {
                case "customer" -> handleCustomer(scan);
                case "manager" -> handleManager(scan);
                case "exit" -> exit = true;
                default -> System.out.println("Invalid option. Please choose 'customer' or 'manager'.");
            }
        }
        System.out.println("Exiting...");
    }
}
