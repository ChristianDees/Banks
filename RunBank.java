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
        fh.loadFromCSV("bankUsers.csv");
        InputHandler ui = new InputHandler();
        ui.displayWelcomeMessage();
        ui.handleInput();
        fh.exportToCSV("bankUsers-new.csv");
    }
}