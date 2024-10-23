import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import java.io.FileReader;
import java.io.*;
import java.util.Scanner;

public class FileHandler extends UserInterface{
    /**
     * Load from csv.
     *
     * @param filename the filename.
     */
    public void loadFromCSV(String filename) {

        String[] HEADERS = new String[0];
        try (Scanner scan = new Scanner(new File(filename))) {
            HEADERS = scan.nextLine().split(",");
        } catch (Exception e) {
            System.out.println("Error loading from file: " + e.getMessage());
        }
        Reader in;
        try {
            in = new FileReader(filename);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        try (BufferedReader ignored = new BufferedReader(new FileReader(filename))) {
            CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                    .setHeader(HEADERS)
                    .setSkipHeaderRecord(true)
                    .build();

            Iterable<CSVRecord> records = csvFormat.parse(in);
            {
                for (CSVRecord record : records) {
                    int idNum = Integer.parseInt(record.get(HEADERS[0]));
                    String firstName = record.get(HEADERS[1]).toLowerCase();
                    String lastName = record.get(HEADERS[2]).toLowerCase();
                    String dob = record.get(HEADERS[3]);
                    String address = record.get(HEADERS[4]).replace("\"", "");
                    String phoneNum = record.get(HEADERS[5]).replaceAll("[()\\s-]", "");
                    int checkingAccNum = Integer.parseInt(record.get(HEADERS[6]));
                    double checkingStartBalance = Double.parseDouble(record.get(HEADERS[7]));
                    int savingAccNum = Integer.parseInt(record.get(HEADERS[8]));
                    double savingStartBalance = Double.parseDouble(record.get(HEADERS[9]));
                    int creditAccNum = Integer.parseInt(record.get(HEADERS[10]));
                    int creditMax = Integer.parseInt(record.get(HEADERS[11]));
                    double creditStartBalance = Double.parseDouble(record.get(HEADERS[12]));
                    Checking checkingAccount = new Checking(checkingAccNum, checkingStartBalance);
                    Savings savingsAccount = new Savings(savingAccNum, savingStartBalance);
                    Credit creditAccount = new Credit(creditAccNum, creditStartBalance, creditMax);
                    Customer newCustomer = new Customer(idNum, firstName, lastName, dob, address, phoneNum);
                    newCustomer.addAccount(checkingAccount);
                    newCustomer.addAccount(savingsAccount);
                    newCustomer.addAccount(creditAccount);
                    customers.put(idNum + firstName + lastName, newCustomer);
                    checkingAccounts.put(checkingAccNum, checkingAccount);
                    savingAccounts.put(savingAccNum, savingsAccount);
                    creditAccounts.put(creditAccNum, creditAccount);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Update csv.
     *
     * @param filename the filename.
     */
    public void exportToCSV(String filename){
        try (FileWriter myWriter = new FileWriter(filename + ".csv", true)) {
            myWriter.write("HELLO");
            System.out.println("\nSuccessfully exported data to " + filename + ".csv");
        } catch (IOException e) {
            System.out.println("An error occurred while writing to the log file: " + e.getMessage());
        }
        // Loop through every customer upon exit, update row with (id, name, date of birth, phone number, Checking Account Number, Checking Starting Balance, Savings Account Number, Savings Starting Balance, Credit Account Number, Credit Max, Credit Starting Balance)
    }

    /**
     * Append message to a log (txt).
     *
     * @param filename the filename.
     */
    public void appendLog(String filename, String msg) {
        try (FileWriter myWriter = new FileWriter(filename + ".txt", true)) {
            myWriter.write(msg + System.lineSeparator());
            System.out.println("Successfully appended to " + filename + ".txt");
        } catch (IOException e) {
            System.out.println("An error occurred while writing to the log file: " + e.getMessage());
        }
    }
}
