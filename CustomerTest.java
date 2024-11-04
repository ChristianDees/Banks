import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class CustomerTest {

    Customer customer;
    Account customerOwnedAccount = new Checking(1, 123.45);
    Account nonCustomerOwnedAccount = new Checking(2, 123.45);

    @BeforeEach
    void setUp() {
        customer = new Customer(1, "John","Smith","12-Jan-03","123 Sesame St.", "(123) 456-7890");
        customer.addAccount(customerOwnedAccount);
    }
    
    @AfterEach
    void tearDown() {
        customer = null;
    }

    @Test
    @DisplayName("Ensure correct handling of customer withdrawing.")
    void testWithdraw() {
        assertAll(
                "Withdraw return tests",
                () -> assertTrue(customer.withdraw(customerOwnedAccount, 123.45)),
                () -> assertFalse(customer.withdraw(customerOwnedAccount, 543.21)),
                () -> assertFalse(customer.withdraw(nonCustomerOwnedAccount, 123.45))
        );
    }
}