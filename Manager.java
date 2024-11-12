import java.util.ArrayList;
import java.util.Dictionary;

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
                    boolean rc;
                    switch (action) {
                        case "pays":
                            rc = fromCustomer.send(fromAccount, toAccount, amount, toCustomer);
                            if (!rc && toCustomer!=null) fh.appendLog("EPMB_Error_Log", fromCustomer.getFullName() + " [id=" + fromCustomer.getId() + "]"+ " attempted to send funds to "  + toCustomer.getFullName() + " [id=" + fromCustomer.getId() + "]"+ ".");
                            if (!rc)fh.appendLog("EPMB_Error_Log", fromCustomer.getFullName() + " attempted to send funds without a valid receiving customer.");
                            break;
                        case "transfers":
                            rc = fromCustomer.transfer(fromAccount, toAccount, amount);
                            if (!rc) fh.appendLog("EPMB_Error_Log", fromCustomer.getFullName() + " [id=" + fromCustomer.getId() + "]"+  " attempted to transfer invalidly.");
                            break;
                        case "inquires":
                            fromCustomer.viewAccount(fromAccount, true);
                            break;
                        case "withdraws":
                            rc = fromCustomer.withdraw(fromAccount, amount);
                            if (!rc) fh.appendLog("EPMB_Error_Log", fromCustomer.getFullName()+ " [id=" + fromCustomer.getId() + "]" + " attempted to withdraw funds.");
                            break;
                        case "deposits":
                            rc = fromCustomer.deposit(toAccount, amount);
                            if (!rc) fh.appendLog("EPMB_Error_Log", fromCustomer.getFullName()+ " [id=" + fromCustomer.getId() + "]" + " attempted to deposit funds.");
                            break;
                        default:
                            System.out.println("Invalid action: " + action);
                    }
                } else {
                    System.out.println("Transaction failed: No source account specified.");
                }
            } else {
                System.out.println("Transaction failed: No source customer specified.");
            }
        }
    }

    @Override
    public String getFullName() {
        return this.firstName + " " + this.lastName;
    }
}
