package lab.ranking;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class RulesTest {
    @Test void businessBoundary() {
        assertTrue(Rules.newer(2,1));
        assertFalse(Rules.newer(1,2));
        assertFalse(Rules.newer(2,2));
    }
}
