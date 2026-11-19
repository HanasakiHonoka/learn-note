package lab.orderexpiry;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class RulesTest {
    @Test void businessBoundary() {
        assertFalse(Rules.due(99,100));
        assertTrue(Rules.due(100,100));
    }
}
