package lab.payment;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class RulesTest {
    @Test void businessBoundary() {
        assertTrue(Rules.matches(100,100));
        assertFalse(Rules.matches(100,99));
        assertFalse(Rules.matches(0,0));
    }
}
