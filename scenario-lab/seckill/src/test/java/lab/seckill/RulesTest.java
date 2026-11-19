package lab.seckill;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class RulesTest {
    @Test void businessBoundary() {
        assertTrue(Rules.valid(1));
        assertFalse(Rules.valid(0));
    }
}
