package lab.im;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class RulesTest {
    @Test void businessBoundary() {
        assertTrue(Rules.validBody("hello"));
        assertFalse(Rules.validBody(" "));
        assertFalse(Rules.validBody("x".repeat(1001)));
    }
}
