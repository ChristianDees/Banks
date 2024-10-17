import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Dictionary;
import java.util.Hashtable;
import java.text.NumberFormat;

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
                customers.put(firstName + lastName, new Customer(idNum, firstName, lastName, dob, address, phoneNum, checkingAccount, savingsAccount, creditAccount));
                checkingAccounts.put(checkingAccNum, checkingAccount);
                savingAccounts.put(savingAccNum, savingsAccount);
                creditAccounts.put(creditAccNum, creditAccount);
            }
        } catch (Exception e) {
            System.out.println("Error loading from file: " + e.getMessage());
        }
    }


    private static boolean viewAccounts(String name, boolean metadata) {
        Customer customer = customers.get(name);
        if (customer == null) {
            System.out.println("That person does not have any accounts with us!");
            return false;
        }
        System.out.println("Accounts:");
        if (metadata) {
            // header
            System.out.println("+-----------------+----------------------+------------------+");
            System.out.printf("| %-15s | %-20s | %-16s |\n", "Type", "Account Number", "Balance");
            System.out.println("+-----------------+----------------------+------------------+");
        } else {
            // header
            System.out.println("+-----------------+----------------------+");
            System.out.printf("| %-15s | %-20s |\n", "Type", "Account Number");
            System.out.println("+-----------------+----------------------+");
        }
        customer.accounts.forEach(account -> account.printAccount(metadata));
        return true;
    }


    public static void main(String[] args) {
        System.out.println("Exiting...");
        loadFromCSV("bankUsers.csv");
        Scanner scan = new Scanner(System.in);
        System.out.println("Welcome to El Paso Miner's Bank!\nType 'exit' to quit.");
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
        System.out.println("Too many attempts. Exiting.");
    }

    private static void TwoAccountTransaction(Scanner scan, Customer customer, Account accountOne) {
        boolean isTransfer = customer != null;
        if (!isTransfer) {
            String customerNameOne = getUserName(scan);
            getUserName(scan);
            customer = customers.get(customerNameOne);
            accountOne = getAccountForTransaction(scan);
        } else {
            System.out.println("Enter the account you would like to transfer funds into.");
        }
        Account accountTwo = getAccountForTransaction(scan);
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
        if (accountOne != null && accountTwo != null && customer != null) {
            if (!isTransfer) {
                boolean rc = customer.send(accountOne, accountTwo, amount);
                // UPDATE CSV HERE IF RC IS TRUE
            } else {
                boolean rc = customer.transfer(accountOne, accountTwo, amount);
                // UPDATE CSV HERE IF RC IS TRUE
            }
        }
    }


    private static void oneAccountTransaction(Scanner scan) {
        String name = getUserName(scan);
        if (name == null) return;
        Account account = getAccountForTransaction(scan);
        if (account == null) return;
        Customer customer = customers.get(name);
        performTransaction(scan, account, customer);
    }

    private static String getUserName(Scanner scan) {
        for (int attempts = 0; attempts < 3; attempts++) {
            System.out.println("Please enter your name to view your accounts.");
            String name = scan.nextLine().trim().toLowerCase().replace(" ", "");
            if (checkExit(name)) return null;
            if (inquireByName(scan, name, false)) {
                return name;
            }
        }
        return null;
    }

    private static Account getAccountForTransaction(Scanner scan) {
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
                    return account;
                }
                System.out.println("Account not found. Please try again.");
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
            System.out.println("Choose an action:\nA. Inquire Balance\nB. Deposit\nC. Withdraw\nD. Transfer");
            String input = scan.nextLine().trim().toLowerCase();
            if (checkExit(input)) return;
            NumberFormat formatter = NumberFormat.getCurrencyInstance();
            switch (input) {
                case "a":
                    System.out.println("Current Balance: " + formatter.format(account.getBalance()));
                    return;
                case "b":
                    System.out.print("Enter deposit amount: ");
                    account.deposit(Double.parseDouble(scan.nextLine()), false);
                    System.out.println("Current Balance: " + formatter.format(account.getBalance()));
                    return;
                case "c":
                    System.out.print("Enter withdrawal amount: ");
                    account.withdraw(Double.parseDouble(scan.nextLine()), false);
                    System.out.println("Current Balance: " + formatter.format(account.getBalance()));
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
        for (int attempts = 0; attempts < 3; attempts++) {
            System.out.println("A. Inquire accounts by customer name.\nB. Inquire account by type/number.");
            String input = scan.nextLine().trim().toLowerCase();
            if (checkExit(input)) return;
            switch (input) {
                case "a" -> inquireByName(scan, null, true);
                case "b" -> inquireByAccount(scan);
                default -> System.out.println("Invalid option. Please choose 'A' or 'B'.");
            }
        }
        System.out.println("Too many attempts. Exiting.");
    }

    private static boolean inquireByName(Scanner scan, String name, boolean metadata) {
        if (name == null) {
            System.out.println("Whose account would you like to inquire about?");
            name = scan.nextLine().trim().replace(" ", "").toLowerCase();
        }
        return checkExit(name) || viewAccounts(name, metadata);
    }

    private static void inquireByAccount(Scanner scan) {
        String accType = getAccountType(scan);
        if (accType != null) {
            Account account = getAccountInfo(scan, accType);
            if (account != null) {
                account.printAccount(true);
            }
        }
    }

    private static String getAccountType(Scanner scan) {
        for (int attempts = 0; attempts < 3; attempts++) {
            System.out.println("What is the account type? (checking/savings): ");
            String accType = scan.nextLine().trim();
            if (checkExit(accType)) return null;
            if ("checking".equalsIgnoreCase(accType) || "savings".equalsIgnoreCase(accType)) return accType;
            System.out.println("Invalid account type. Please enter 'checking' or 'savings'.");
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
                return getAccount(accType, accNum);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid account number.");
            }
        }
        return null;
    }

    private static boolean checkExit(String input) {
        return input.equalsIgnoreCase("exit") && (exit = true);
    }
}

