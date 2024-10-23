import java.util.Dictionary;
import java.util.Hashtable;

public abstract class UserInterface {
    public static boolean exit = false;
    public static final Dictionary<String, Customer> customers = new Hashtable<>();
    public static final Dictionary<Integer, Checking> checkingAccounts = new Hashtable<>();
    public static final Dictionary<Integer, Savings> savingAccounts = new Hashtable<>();
    public static final Dictionary<Integer, Credit> creditAccounts = new Hashtable<>();

    /**
     * Determine if input is of 'exit' and set global exit variable.
     *
     * @param input         The scanner object to continue taking input.
     * @return              The status of if the program should be exited.
     */
    public static boolean checkExit(String input) {
        return input.equalsIgnoreCase("exit") && (exit = true);
    }
}
