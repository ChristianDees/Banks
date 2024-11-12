import java.util.ArrayList;
import java.util.Dictionary;
import java.util.LinkedList;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Set;

import static java.lang.System.out;

public class Manager implements Person{

    String firstName;
    String lastName;

    /**
     * Constructs a new Person with the specified attributes.
     *
     * @param firstName The first name of the person.
     * @param lastName  The last name of the person.
     */
    Manager(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    /**
     * Performs transaction from a provided file.
     *
     * @param filename  file that has the transactions.
     */
    public void transactFromFile(String filename) {
        FileHandler fh = new FileHandler();
        ArrayList<Dictionary<String, String>> allRecords = fh.getAllRecordsFromCSV("Transactions/"+filename);

        for (Dictionary<String, String> recordDict : allRecords) {
            String fromCustomerName = (recordDict.get("From First Name") + recordDict.get("From Last Name")).toLowerCase();
            Customer fromCustomer = BankDatabase.customers.get(fromCustomerName);

            if (fromCustomer != null) {
                String action = recordDict.get("Action").toLowerCase();
                String toCustomerName = (recordDict.get("To First Name") + recordDict.get("To Last Name")).toLowerCase();
                Customer toCustomer = BankDatabase.customers.get(toCustomerName);

                Account fromAccount = null;
                for (Account account : fromCustomer.getAccounts()) {
                    if (account.getType().equalsIgnoreCase(recordDict.get("From Where"))) {
                        fromAccount = account;
                        break;
                    }
                }

                Account toAccount = null;
                if (toCustomer != null) {
                    for (Account account : toCustomer.getAccounts()) {
                        if (account.getType().equalsIgnoreCase(recordDict.get("To Where"))) {
                            toAccount = account;
                            break;
                        }
                    }
                }

                if (fromAccount != null) {
                    double amount = recordDict.get("Action Amount").isEmpty() ? 0 : Double.parseDouble(recordDict.get("Action Amount"));
                    boolean rc = false;

                    switch (action) {
                        case "pays":
                            rc = fromCustomer.send(fromAccount, toAccount, amount, toCustomer);
                            break;
                        case "transfers":
                            rc = fromCustomer.transfer(fromAccount, toAccount, amount);
                            break;
                        case "inquires":
                            fromCustomer.viewAccount(fromAccount, true);
                            break;
                        case "withdraws":
                            rc = fromCustomer.withdraw(fromAccount, amount);
                            break;
                        case "deposits":
                            rc = fromCustomer.deposit(toAccount, amount);
                            break;
                        default:
                            System.out.println("Invalid action: " + action);
                    }
                    if (!rc && !action.equals("inquires")) {
                        System.out.println("Action failed for " + action);
                    }
                } else {
                    System.out.println("From account does not exist.");
                }
            } else {
                System.out.println("Customer from does not exist.");
            }
        }
    }


    @Override
    public String getFullName() {
        return this.firstName + " " + this.lastName;
    }
}
