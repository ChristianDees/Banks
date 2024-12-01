/**
 * Represents an exception for an invalid currency input
 */
public class InvalidCurrencyFormat extends Exception {

    /**
     * Creation of an invalid currency format exception
     * @param message of what to say to error.
     */
    public InvalidCurrencyFormat(String message){
        super(message);
    }
}
