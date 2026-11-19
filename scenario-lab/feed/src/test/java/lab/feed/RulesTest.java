package lab.feed;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class RulesTest {
    @Test void businessBoundary() {
        assertTrue(Rules.push(false));
        assertFalse(Rules.push(true));
    }
}
