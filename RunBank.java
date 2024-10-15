import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class RunBank {
    private static final ArrayList<Customer> customers= new ArrayList<Customer>();
    public static void loadFromCSV(String filename) {

        try{
            Scanner scan = new Scanner(new File(filename)); // a new scanner object to go through the earthquake csv file
            scan.nextLine(); // moves past the header line in the csv file
            while(scan.hasNextLine()){ //iterates through the file as long as the file has a next line
                String person = scan.nextLine(); // quake is set to the current line in the file
                String[] personInfo = person.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                int idNum = Integer.parseInt(personInfo[0]);
                Customer customer = getCustomer(personInfo, idNum);
                customers.add(customer);
            }
            scan.close();
        }catch(Exception e) {
            System.out.println("Error loading from file.");
        }
    }

    private static Customer getCustomer(String[] personInfo, int idNum) {
        String firstName = personInfo[1];
        String lastName = personInfo[2];
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
        Account checkingAcc = new Checkings(checkingAccNum, checkingStartBalance);
        Account savingAcc = new Savings(savingAccNum, savingStartBalance);
        Credit credit = new Credit(creditAccNum, creditMax, creditStartBalance);
        return new Customer(idNum, firstName, lastName, dob, address, phoneNum, checkingAcc, savingAcc, credit);
    }


    public static void main(String[] args) {
        loadFromCSV("bankUsers.csv");
        System.out.println(customers.toString());
    }
}