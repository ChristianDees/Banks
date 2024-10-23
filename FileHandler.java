import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import java.io.FileReader;
import java.io.*;
import java.util.*;

public class FileHandler extends UserInterface{

    /**
     * Load from csv.
     *
     * @param filename the filename.
     */
    public boolean loadFromCSV(String filename) {
        filename += ".csv";
        try (Scanner scan = new Scanner(new File(filename))) {
            String[] headers = scan.nextLine().split(",");
            try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
                CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                        .setHeader(headers)
                        .setSkipHeaderRecord(true)
                        .build();
                for (CSVRecord record : csvFormat.parse(reader)) {
                    Customer newCustomer = createCustomer(record, headers);
                    customers.put(newCustomer.getId()+newCustomer.getFirstName()+newCustomer.getLastName(), newCustomer);
                    for (Account account : newCustomer.getAccounts()) {
                        addAccountToMaps(account);
                    }
                }
                return true;
            }
        } catch (IOException e) {
            System.out.println("Error loading from file: " + e.getMessage());
            return false;
        }
    }

    private Customer createCustomer(CSVRecord record, String[] HEADERS) {
        int idNum = Integer.parseInt(record.get(HEADERS[0]));
        String firstName = record.get(HEADERS[1]).toLowerCase();
        String lastName = record.get(HEADERS[2]).toLowerCase();
        String dob = record.get(HEADERS[3]);
        String address = record.get(HEADERS[4]).replace("\"", "");
        String phoneNum = record.get(HEADERS[5]).replaceAll("[()\\s-]", "");

        Customer customer = new Customer(idNum, firstName, lastName, dob, address, phoneNum);
        customer.addAccount(new Checking(Integer.parseInt(record.get(HEADERS[6])), Double.parseDouble(record.get(HEADERS[7]))));
        customer.addAccount(new Savings(Integer.parseInt(record.get(HEADERS[8])), Double.parseDouble(record.get(HEADERS[9]))));
        customer.addAccount(new Credit(Integer.parseInt(record.get(HEADERS[10])), Double.parseDouble(record.get(HEADERS[12])), Integer.parseInt(record.get(HEADERS[11]))));

        return customer;
    }

    private void addAccountToMaps(Account account) {
        if (account instanceof Checking) {
            checkingAccounts.put(account.getAccountNumber(), (Checking) account);
        } else if (account instanceof Savings) {
            savingAccounts.put(account.getAccountNumber(), (Savings) account);
        } else if (account instanceof Credit) {
            creditAccounts.put(account.getAccountNumber(), (Credit) account);
        }
    }


    /**
     * Update csv.
     *
     * @param filename the filename.
     */
    public void exportToCSV(String filename) {
        // setup filename
        filename += ".csv";
        HashMap<String, String[]> existingData = new HashMap<>();
        // get current data
        try (Scanner scan = new Scanner(new FileReader(filename))) {
            String headerLine = scan.nextLine();
            String[] headers = headerLine.split(",");
            // get index of id col
            int idColumnIndex = -1;
            for (int i = 0; i < headers.length; i++) {
                if (headers[i].equalsIgnoreCase("ID")) {
                    idColumnIndex = i;
                    break;
                }
            }
            // no id col
            if (idColumnIndex == -1) {
                System.out.println("Error: ID column does not exist within the file.");
                return;
            }
            // get current data
            while (scan.hasNextLine()) {
                String line = scan.nextLine();
                String[] values = line.split(",");
                // must fit for headers
                if (values.length > idColumnIndex) {
                    String id = values[idColumnIndex];
                    existingData.put(id, values);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading the existing CSV file: " + e.getMessage());
        }
        // start writing
        try (FileWriter writer = new FileWriter(filename, false)) { // Overwrite file
            String[] headers = {
                    "ID",
                    "First Name",
                    "Last Name",
                    "Date of Birth",
                    "Address",
                    "Phone Number",
                    "Checking Account Number",
                    "Checking Current Balance",
                    "Savings Account Number",
                    "Savings Current Balance",
                    "Credit Account Number",
                    "Credit Max",
                    "Credit Current Balance"
            };
            // write headers
            writer.write(String.join(",", headers));
            writer.write(System.lineSeparator());
            // iterate over customers
            Enumeration<String> keys = customers.keys();
            while (keys.hasMoreElements()) {
                String key = keys.nextElement();
                Customer customer = customers.get(key);
                // get new values
                String[] currentValues = new String[headers.length];
                currentValues[0] = String.valueOf(customer.getId());
                currentValues[1] = customer.getFirstName();
                currentValues[2] = customer.getLastName();
                currentValues[3] = customer.getDob();
                currentValues[4] = customer.getAddress();
                currentValues[5] = customer.getPhoneNum();
                // setup accounts
                String checkingAccountNumber = "", checkingBalance = "";
                String savingsAccountNumber = "", savingsBalance = "";
                String creditAccountNumber = "", creditMax = "", creditBalance = "";
                // get customer's accounts
                ArrayList<Account> accounts = customer.getAccounts();
                if (accounts != null) {
                    for (Account account : accounts) {
                        if (account instanceof Checking checking) {
                            checkingAccountNumber = String.valueOf(checking.getAccountNumber());
                            checkingBalance = String.format("%.2f", checking.getBalance());
                        } else if (account instanceof Savings savings) {
                            savingsAccountNumber = String.valueOf(savings.getAccountNumber());
                            savingsBalance = String.format("%.2f", savings.getBalance());
                        } else if (account instanceof Credit credit) {
                            creditAccountNumber = String.valueOf(credit.getAccountNumber());
                            creditMax = String.valueOf(credit.getCreditMax());
                            creditBalance = String.format("%.2f", credit.getBalance());
                        }
                    }
                }
                // get new information
                currentValues[6] = checkingAccountNumber;
                currentValues[7] = checkingBalance;
                currentValues[8] = savingsAccountNumber;
                currentValues[9] = savingsBalance;
                currentValues[10] = creditAccountNumber;
                currentValues[11] = creditMax;
                currentValues[12] = creditBalance;
                // get current vals
                String[] existingValues = existingData.get(currentValues[0]);
                if (existingValues != null) {
                    // check if worth updating
                    for (int i = 0; i < headers.length; i++) {
                        if (i < existingValues.length) { // check out of bounds
                            if (!currentValues[i].equals(existingValues[i])) {
                                existingValues[i] = currentValues[i]; // update if different
                            }
                        }
                    }
                } else {
                    // if user is new
                    existingData.put(currentValues[0], currentValues);
                }
            }
            // write all updated or new entries back to the file
            for (String[] values : existingData.values()) {
                writer.write(String.join(",", values));
                writer.write(System.lineSeparator());
            }
            System.out.println("\n* * * Successfully exported data to " + filename + " * * *");
        } catch (IOException e) {
            System.out.println("An error occurred while writing to the log file: " + e.getMessage());
        }
    }

    /**
     * Append message to a log (txt).
     *
     * @param filename the filename.
     */
    public void appendLog(String filename, String msg) {
        try (FileWriter myWriter = new FileWriter(filename + ".txt", true)) {
            myWriter.write(msg + System.lineSeparator());
        } catch (IOException e) {
            System.out.println("An error occurred while writing to the log file: " + e.getMessage());
        }
    }
}
