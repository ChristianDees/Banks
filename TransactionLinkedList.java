import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Linked list for transactions of an account.
 */
public class TransactionLinkedList {
    private TransactionNode head;
    public final double startingBalance;

    /**
     * Constructor of LL
     *
     * @param startingBalance starting balance before any transactions.
     */
    public TransactionLinkedList(double startingBalance) {
        this.head = null;
        this.startingBalance = startingBalance;
    }

    /**
     * Adds a transaction with its respective attributes.
     *
     * @param date          of transaction.
     * @param description   of transaction.
     * @param amount        transacted.
     * @param newBalance    after transaction.
     */
    public void addTransaction(String date, String description, double amount, double newBalance) {
        TransactionNode newNode = new TransactionNode(date, description, amount, newBalance);
        if (head == null) head = newNode;
        else {
            TransactionNode current = head;
            while (current.next != null) current = current.next;
            current.next = newNode;
        }
    }

    /**
     * Get list of transactions between a start and end range of dates.
     *
     * @param startDate         lower bound.
     * @param endDate           upper bound.
     * @return                  list of transactions within that bound.
     * @throws ParseException   for dates.
     */
    public List<TransactionNode> getTransactionsBetweenDates(String startDate, String endDate) throws ParseException {
        List<TransactionNode> result = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
        java.util.Date start = sdf.parse(startDate);
        java.util.Date end = sdf.parse(endDate);
        TransactionNode current = head;
        while (current != null) {
            java.util.Date transactionDate = sdf.parse(current.date);
            if ((transactionDate.equals(start) || transactionDate.after(start)) &&
                    (transactionDate.equals(end) || transactionDate.before(end))) {
                result.add(current);
            }
            current = current.next;
        }
        return result;
    }

}
