package lab.scheduler;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class RulesTest {
    @Test void businessBoundary() {
        assertTrue(Rules.leaseValid(10,11));
        assertFalse(Rules.leaseValid(10,10));
    }
}
