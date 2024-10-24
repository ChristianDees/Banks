/**
 * Represents the bank running
 */
public class RunBank {
    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        FileHandler fh = new FileHandler();
        if (!fh.loadFromCSV("BankUsers")) return;
        InputHandler ui = new InputHandler();
        ui.displayWelcomeMessage();
        ui.handleInput();
        // export once terminated
        fh.exportToCSV("EPMB_Report");
    }
}