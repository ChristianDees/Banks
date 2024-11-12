import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class CustomerTest {

    Customer customerA;
    Customer customerB;
    Account accountA;
    Account accountB;
    Account accountC;

    @BeforeEach
    void setUp() {
        // setup customers
        customerA = new Customer(1, "John","Smith","12-Jan-03","123 Sesame St.", "(123) 456-7890");
        customerB = new Customer(2, "John","Doe","14-Aug-01","321 Sesame St.", "(098) 765-4321");
        // setup accounts
        accountA = new Checking(1, 123.45);
        accountB = new Checking(2, 123.45);
        accountC = new Checking(3, 123.45);
        // assign accounts to customers
        customerA.addAccount(accountA);
        customerA.addAccount(accountC);

    }

    @AfterEach
    void tearDown() {
        accountA = null;
        accountB = null;
        accountC = null;
        customerA = null;
        customerB = null;
    }

    @Test
    @DisplayName("Ensure correct handling of customer withdrawing funds.")
    void testWithdraw() {
        assertAll(
                "Withdraw return tests",
                () -> assertTrue(customerA.withdraw(accountA, 123.45)),
                () -> assertFalse(customerA.withdraw(accountA, 543.21)),
                () -> assertFalse(customerA.withdraw(accountB, 123.45))
        );
    }

    @Test
    @DisplayName("Ensure correct handling of customer depositing funds.")
    void testDeposit() {
        assertAll(
                "Deposit return tests",
                () -> assertTrue(customerA.deposit(accountA, 123.45)),
                () -> assertFalse(customerA.deposit(accountB, 123.45))
        );
    }

    @Test
    @DisplayName("Ensure correct handling of customer transferring funds.")
    void testTransfer() {
        assertAll(
                "Transfer return tests",
                () -> assertTrue(customerA.transfer(accountA, accountC, 123.45)),
                () -> assertFalse(customerA.transfer(accountA, accountB, 543.21))
        );
    }

    @Test
    @DisplayName("Ensure correct handling of customer sending funds.")
    void testSend() {
        assertAll(
                "Send return tests",
                () -> assertTrue(customerA.send(accountA, accountB, 123.45,customerB)),
                () -> assertFalse(customerA.send(accountA, accountB, 123.45,customerB)),
                () -> assertFalse(customerB.send(accountA, accountC, 123.45,customerA))

        );
    }

    @Test
    @DisplayName("Ensure correct response of full name in correct format.")
    void testGetFullName() {
        assertAll(
                "Withdraw return tests",
                () -> assertEquals("John Smith", customerA.getFullName()),
                () -> assertEquals("John Doe", customerB.getFullName())
        );
    }
}