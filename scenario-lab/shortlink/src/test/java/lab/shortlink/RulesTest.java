package lab.shortlink;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class RulesTest {
    @Test void businessBoundary() {
        assertTrue(Rules.safe("https://example.com"));
        assertFalse(Rules.safe("javascript:alert(1)"));
        assertFalse(Rules.safe("http:///"));
    }
}
