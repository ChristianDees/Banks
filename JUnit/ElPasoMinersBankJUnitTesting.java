import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class ElPasoMinersBankJUnitTesting {

    Customer customerA;
    Customer customerB;
    Account accountA;
    Account accountB;
    Account accountC;
    Account accountD;
    Account accountE;
    Manager manager;
    FileHandler fh;

    @BeforeEach
    void setUp() {
        // setup customers
        customerA = new Customer(1, "John","Smith","12-Jan-03","123 Sesame St.", "123456890", "123");
        customerB = new Customer(2, "John","Doe","14-Aug-01","321 Sesame St.", "0987654321", "123");
        // setup accounts
        accountA = new Checking(1, 123.45);
        accountB = new Checking(2, 123.45);
        accountC = new Checking(3, 123.45);
        // credit account
        accountD = new Credit(4, 0, 1000);
        // savings account
        accountE = new Savings(5, 123.45);
        // assign accounts to customers
        customerA.addAccount(accountA);
        customerA.addAccount(accountC);
        customerA.addAccount(accountD);
        customerA.password = "password123!";

        // setup for manager
        manager = new Manager("admin","admin");
        fh = new FileHandler();
    }

    @AfterEach
    void tearDown() {
        accountA = null;
        accountB = null;
        accountC = null;
        accountD = null;
        accountE = null;
        customerA = null;
        customerB = null;
        manager = null;
        fh = null;
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

    @Test
    @DisplayName("Ensure correct response of generate credit score.")
    void testGenerateCreditScore() {
        assertAll(
                "Credit score return tests",
                () -> assertTrue(customerA.getCreditScore() >= 300 && customerA.getCreditScore() <= 850,
                        "Credit score should be within the valid range (300 - 850)")
        );
    }


    @Test
    @DisplayName("Ensure correct response of password verification.")
    void testVerifyPassword() {
        assertAll(
                "Password verifications",
                () -> assertTrue(customerA.verifyPassword("password123!")),
                () -> assertFalse(customerA.verifyPassword("notMyPassword!"))
        );
    }


    @Test
    @DisplayName("Ensure proper user transactions file.")
    void testTransactionFromFile() {
        assertAll(
                "Transactions file generation verification",
                () -> assertTrue(fh.generateUserTransactionsFile("fake-transactions.txt", customerA, accountA, "11-16-2024","11-16-2024"))
        );
    }

    @Test
    @DisplayName("Ensure proper return of account types.")
    void testGetTypeAccount() {
        assertAll(
                "Verify account type returns.",
                () -> assertEquals("Checking", accountA.getType()),
                () -> assertEquals("Savings", accountE.getType()),
                () -> assertEquals("Credit", accountD.getType())
        );
    }

    @Test
    @DisplayName("Ensure proper return of get phone number.")
    void testGetPhoneNumbers() {
        assertAll(
                "Verify phone number returns.",
                () -> assertEquals("(123) 456-890", customerA.getPhoneNum()),
                () -> assertEquals("(098) 765-4321", customerB.getPhoneNum())
        );
    }
}