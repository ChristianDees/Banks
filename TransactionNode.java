/**
 * Transaction Node with its respective attributes for a transaction.
 */
public class TransactionNode {
    String date;
    String description;
    double amount;
    double newBalance;
    TransactionNode next;

    /**
     * Constructor to create a transaction node.
     *
     * @param date          of transaction.
     * @param description   of transaction.
     * @param amount        transacted.
     * @param newBalance    after transaction.
     */
    public TransactionNode(String date, String description, double amount, double newBalance) {
        this.date = date;
        this.description = description;
        this.amount = amount;
        this.newBalance = newBalance;
        this.next = null;
    }
}

