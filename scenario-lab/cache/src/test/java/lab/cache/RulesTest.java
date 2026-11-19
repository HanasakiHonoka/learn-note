package lab.cache;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class RulesTest {
    @Test void businessBoundary() {
        assertTrue(Rules.versionValid(1));
        assertFalse(Rules.versionValid(0));
    }
}
