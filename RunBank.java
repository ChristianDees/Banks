import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Dictionary;
import java.util.Hashtable;

public class RunBank {
    private static final Dictionary<String, Customer> customers= new Hashtable<>();
    private static final Dictionary<Integer, Checkings> checkingAccounts = new Hashtable<>();
    private static final Dictionary<Integer, Savings> savingAccounts= new Hashtable<>();
    public static void loadFromCSV(String filename) {
        try{
            Scanner scan = new Scanner(new File(filename)); // a new scanner object to go through the earthquake csv file
            scan.nextLine(); // moves past the header line in the csv file
            while(scan.hasNextLine()){ //iterates through the file as long as the file has a next line
                String person = scan.nextLine(); // quake is set to the current line in the file
                String[] personInfo = person.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                int idNum = Integer.parseInt(personInfo[0]);
                String firstName = personInfo[1].toLowerCase();
                String lastName = personInfo[2].toLowerCase();
                String dob = personInfo[3];
                String address = personInfo[4].replace("\"", "");
                String phoneNum = personInfo[5].replace("(", "").replace(")","").replace(" ", "").replace("-", "");
                int checkingAccNum = Integer.parseInt(personInfo[6]);
                double checkingStartBalance = Double.parseDouble(personInfo[7]);
                int savingAccNum = Integer.parseInt(personInfo[8]);
                double savingStartBalance = Double.parseDouble(personInfo[9]);
                int creditAccNum = Integer.parseInt(personInfo[10]);
                int creditMax = Integer.parseInt(personInfo[11]);
                double creditStartBalance = Double.parseDouble(personInfo[12]);
                Checkings checkingAcc = new Checkings(checkingAccNum, checkingStartBalance);
                Savings savingAcc = new Savings(savingAccNum, savingStartBalance);
                Credit credit = new Credit(creditAccNum, creditMax, creditStartBalance);
                Customer customer = new Customer(idNum, firstName, lastName, dob, address, phoneNum, checkingAcc, savingAcc, credit);
                customers.put(firstName + lastName, customer);
                checkingAccounts.put(checkingAccNum, checkingAcc);
                savingAccounts.put(savingAccNum, savingAcc);
            }
            scan.close();
        }catch(Exception e) {
            System.out.println("Error loading from file.");
        }
    }

    private static void printAllAccountInfo(String name){
        Customer customer = customers.get(name);
        if (customer != null){
            ArrayList<Account> accounts = customer.accounts;
            for (Account account : accounts) {
                System.out.println(account.toString());
            }
        } else {
            System.out.println("That person does not have any accounts with us!");
        }

    }

    public static void main(String[] args) {
        loadFromCSV("bankUsers.csv");
        String input = "";
        Scanner scan = new Scanner(System.in);
        while(!input.equals("exit")){
            System.out.println("A. Inquire account by name.\nB. Inquire account by type/number.");
            input = scan.nextLine().replace(" ", "").toLowerCase();
            if (input.equalsIgnoreCase("a")){
                System.out.println("Whose account would you like to inquire about?");
                String name = scan.nextLine().replace(" ", "").toLowerCase();
                printAllAccountInfo(name);
            } else if (input.equalsIgnoreCase("b")){
                System.out.println("What is the account type?");
                String accType = scan.nextLine().replace(" ", "").toLowerCase();
                if (accType.equals("checking")){
                    System.out.println("What is the account number?");
                    int accNum = Integer.parseInt(scan.nextLine().replace(" ", "").toLowerCase());
                    System.out.println(checkingAccounts.get(accNum).toString());
                } else if (accType.equals("saving")){
                    System.out.println("What is the account number?");
                    int accNum = Integer.parseInt(scan.nextLine().replace(" ", "").toLowerCase());
                    System.out.println(savingAccounts.get(accNum).toString());
                }
            }
        }
    }
}