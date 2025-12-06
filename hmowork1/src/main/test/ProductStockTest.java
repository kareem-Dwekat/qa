package main.test;

import main.java.ProductStock;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ProductStock Unit Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProductStockTest {

    ProductStock stock;

    @BeforeAll
    static void beforeAll() {
        System.out.println("Starting ProductStock tests...");
    }

    @BeforeEach
    void setUp() {
        stock = new ProductStock("P1", "A1", 10, 3, 50);
    }

    @AfterEach
    void tearDown() {
        System.out.println("Test finished.");
    }

    @AfterAll
    static void afterAll() {
        System.out.println("All ProductStock tests completed.");
    }

    // ---------------------------------------------------
    // Constructor Tests
    // ---------------------------------------------------

    @Test
    @Tag("sanity")
    @DisplayName("Valid constructor should create object")
    void testValidConstructor() {
        ProductStock ps = new ProductStock("X1", "L1", 5, 2, 20);
        assertAll(
                () -> assertEquals("X1", ps.getProductId()),
                () -> assertEquals("L1", ps.getLocation()),
                () -> assertEquals(5, ps.getOnHand())
        );
    }

    @Test
    @Tag("regression")
    @DisplayName("Constructor throws for invalid parameters")
    void testConstructorInvalid() {
        assertThrows(IllegalArgumentException.class,
                () -> new ProductStock("", "A1", 5, 0, 10));
        assertThrows(IllegalArgumentException.class,
                () -> new ProductStock("P1", "", 5, 0, 10));
        assertThrows(IllegalArgumentException.class,
                () -> new ProductStock("P1", "A1", -1, 0, 10));
        assertThrows(IllegalArgumentException.class,
                () -> new ProductStock("P1", "A1", 5, -1, 10));
        assertThrows(IllegalArgumentException.class,
                () -> new ProductStock("P1", "A1", 5, 1, 0));
        assertThrows(IllegalArgumentException.class,
                () -> new ProductStock("P1", "A1", 100, 1, 50));
    }

    // ---------------------------------------------------
    // addStock Tests
    // ---------------------------------------------------

    @Test
    @DisplayName("Add stock normally increments onHand")
    void testAddStockNormal() {
        stock.addStock(5);
        assertEquals(15, stock.getOnHand());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 5, 10})
    @DisplayName("Parameterized addStock test")
    void testAddStockDifferentAmounts(int amount) {
        stock.addStock(amount);
        assertEquals(10 + amount, stock.getOnHand());
    }

    @Test
    @DisplayName("addStock throws for invalid or exceeding capacity")
    void testAddStockErrors() {
        assertThrows(IllegalArgumentException.class, () -> stock.addStock(0));
        assertThrows(IllegalArgumentException.class, () -> stock.addStock(-5));
        assertThrows(IllegalStateException.class, () -> stock.addStock(1000));
    }

    // ---------------------------------------------------
    // Location Change Tests
    // ---------------------------------------------------

    @Test
    @DisplayName("Change location works")
    void testChangeLocation() {
        stock.changeLocation("B2");
        assertEquals("B2", stock.getLocation());
    }

    @Test
    @DisplayName("Change location invalid")
    void testChangeLocationInvalid() {
        assertThrows(IllegalArgumentException.class, () -> stock.changeLocation(""));
        assertThrows(IllegalArgumentException.class, () -> stock.changeLocation(null));
    }

    // ---------------------------------------------------
    // reserve Tests
    // ---------------------------------------------------

    @Test
    @DisplayName("Reserve reduces available")
    void testReserveNormal() {
        stock.reserve(3);
        assertAll(
                () -> assertEquals(3, stock.getReserved()),
                () -> assertEquals(7, stock.getAvailable())
        );
    }

    @Test
    @DisplayName("Reserve throws when amount > available")
    void testReserveError() {
        assertThrows(IllegalStateException.class, () -> stock.reserve(20));
    }

    // ---------------------------------------------------
    // releaseReservation Tests
    // ---------------------------------------------------

    @Test
    @DisplayName("Release reservation correctly updates reserved")
    void testReleaseReservation() {
        stock.reserve(5);
        stock.releaseReservation(2);
        assertEquals(3, stock.getReserved());
    }

    @Test
    @DisplayName("Cannot release more than reserved")
    void testReleaseReservationError() {
        stock.reserve(3);
        assertThrows(IllegalStateException.class, () -> stock.releaseReservation(5));
    }

    // ---------------------------------------------------
    // shipReserved Tests
    // ---------------------------------------------------

    @Test
    @DisplayName("Ship reserved reduces onHand + reserved")
    void testShipReserved() {
        stock.reserve(4);
        stock.shipReserved(4);
        assertEquals(6, stock.getOnHand());
        assertEquals(0, stock.getReserved());
    }

    @Test
    @DisplayName("ShipReserved throws if shipping more than reserved")
    void testShipReservedError() {
        stock.reserve(2);
        assertThrows(IllegalStateException.class, () -> stock.shipReserved(5));
    }

    // ---------------------------------------------------
    // removeDamaged Tests
    // ---------------------------------------------------

    @Test
    @DisplayName("removeDamaged reduces onHand")
    void testRemoveDamaged() {
        stock.removeDamaged(3);
        assertEquals(7, stock.getOnHand());
    }

    @Test
    @DisplayName("removeDamaged cannot exceed onHand")
    void testRemoveDamagedError() {
        assertThrows(IllegalStateException.class, () -> stock.removeDamaged(100));
    }

    // ---------------------------------------------------
    // reorder logic
    // ---------------------------------------------------

    @Test
    @DisplayName("Reorder needed when available < threshold")
    void testReorderNeeded() {
        stock.reserve(8);
        assertTrue(stock.isReorderNeeded());
    }

    // ---------------------------------------------------
    // update thresholds and capacity
    // ---------------------------------------------------

    @Test
    @DisplayName("Update reorder threshold works")
    void testUpdateReorderThreshold() {
        stock.updateReorderThreshold(10);
        assertEquals(10, stock.getReorderThreshold());
    }

    @Test
    @DisplayName("updateReorderThreshold error")
    void testUpdateReorderThresholdError() {
        assertThrows(IllegalArgumentException.class, () -> stock.updateReorderThreshold(-1));
        assertThrows(IllegalArgumentException.class, () -> stock.updateReorderThreshold(100000));
    }

    @Test
    @DisplayName("updateMaxCapacity works")
    void testUpdateMaxCapacity() {
        stock.updateMaxCapacity(100);
        assertEquals(100, stock.getMaxCapacity());
    }

    @Test
    @DisplayName("updateMaxCapacity cannot be < onHand")
    void testUpdateMaxCapacityError() {
        assertThrows(IllegalStateException.class, () -> stock.updateMaxCapacity(5));
    }

    // ---------------------------------------------------
    // Disabled Test (Future)
    // ---------------------------------------------------

    @Test
    @Disabled("Feature will be implemented in future release")
    void futureFeatureTest() {
        fail("Not implemented");
    }

    // ---------------------------------------------------
    // Timeout Test
    // ---------------------------------------------------

    @Test
    @Timeout(1)
    @DisplayName("Timeout Example Test")
    void testTimeout() {
        assertTrue(true);
    }
}