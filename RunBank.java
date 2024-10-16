import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Dictionary;
import java.util.Hashtable;

public class RunBank {
    private static final Dictionary<String, Customer> customers = new Hashtable<>();
    private static final Dictionary<Integer, Checkings> checkingAccounts = new Hashtable<>();
    private static final Dictionary<Integer, Savings> savingAccounts = new Hashtable<>();
    private static boolean exit = false;

    public static void loadFromCSV(String filename) {
        try (Scanner scan = new Scanner(new File(filename))) { // Auto-close the scanner
            scan.nextLine(); // Skip header
            while (scan.hasNextLine()) {
                String[] personInfo = scan.nextLine().split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                int idNum = Integer.parseInt(personInfo[0]);
                String firstName = personInfo[1].toLowerCase();
                String lastName = personInfo[2].toLowerCase();
                String dob = personInfo[3];
                String address = personInfo[4].replace("\"", "");
                String phoneNum = personInfo[5].replaceAll("[()\\s-]", ""); // Use regex for cleanup
                int checkingAccNum = Integer.parseInt(personInfo[6]);
                double checkingStartBalance = Double.parseDouble(personInfo[7]);
                int savingAccNum = Integer.parseInt(personInfo[8]);
                double savingStartBalance = Double.parseDouble(personInfo[9]);
                int creditAccNum = Integer.parseInt(personInfo[10]);
                int creditMax = Integer.parseInt(personInfo[11]);
                double creditStartBalance = Double.parseDouble(personInfo[12]);
                customers.put(firstName + lastName, new Customer(idNum, firstName, lastName, dob, address, phoneNum,
                        new Checkings(checkingAccNum, checkingStartBalance),
                        new Savings(savingAccNum, savingStartBalance),
                        new Credit(creditAccNum, creditMax, creditStartBalance)));
                checkingAccounts.put(checkingAccNum, new Checkings(checkingAccNum, checkingStartBalance));
                savingAccounts.put(savingAccNum, new Savings(savingAccNum, savingStartBalance));
            }
        } catch (Exception e) {
            System.out.println("Error loading from file: " + e.getMessage());
        }
    }

    private static void printAllAccountInfo(String name) {
        Customer customer = customers.get(name);
        if (customer != null) {
            customer.accounts.forEach(System.out::println);
        } else {
            System.out.println("That person does not have any accounts with us!");
        }
    }

    public static void main(String[] args) {
        loadFromCSV("bankUsers.csv");
        Scanner scan = new Scanner(System.in);
        while (!exit) {
            System.out.println("A. Inquire account by name.\nB. Inquire account by type/number.\nType 'exit' to quit.");
            String input = scan.nextLine().trim().toLowerCase();
            switch (input) {
                case "a" -> inquireByName(scan);
                case "b" -> inquireByAccount(scan);
                case "exit" -> exit = true;
                default -> System.out.println("Invalid option. Please choose 'A' or 'B'.");
            }
        }
        System.out.println("Exiting...");
    }


    private static void inquireByName(Scanner scan) {
        System.out.println("Whose account would you like to inquire about?");
        String name = scan.nextLine().trim().replace(" ", "").toLowerCase();
        if (checkExit(name)) return;
        printAllAccountInfo(name);
    }

    private static void inquireByAccount(Scanner scan) {
        String accType = getAccountType(scan);
        if (accType != null) {
            Account accInfo = getAccountInfo(scan, accType);
            if (accInfo != null) System.out.println(accInfo);
        }
    }

    private static String getAccountType(Scanner scan) {
        for (int attempts = 0; attempts < 3; attempts++) {
            System.out.print("What is the account type? (checking/saving): ");
            String accType = scan.nextLine().trim().toLowerCase();
            if (checkExit(accType)) return null;
            if ("checking".equals(accType) || "saving".equals(accType)) return accType;
            System.out.println("Invalid account type. Please enter 'checking' or 'saving'.");
        }
        System.out.println("Too many attempts. Exiting.");
        return null;
    }

    private static Account getAccountInfo(Scanner scan, String accType) {
        for (int attempts = 0; attempts < 3; attempts++) {
            System.out.print("What is the account number? ");
            String input = scan.nextLine().trim().toLowerCase();
            if (checkExit(input)) return null;
            try {
                int accNum = Integer.parseInt(input);
                Account accInfo = accType.equals("checking") ? checkingAccounts.get(accNum) : savingAccounts.get(accNum);
                if (accInfo != null) return accInfo;
                System.out.println("Account number does not exist under that name.");
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid account number.");
            }
        }
        System.out.println("Too many attempts. Exiting.");
        return null;
    }

    private static boolean checkExit(String input) {
        return input.equals("exit") && (exit = true);
    }
}

