package lab.qrlogin;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class RulesTest {
    @Test void businessBoundary() {
        assertEquals(64,Rules.hash("x").length());
        assertNotEquals(Rules.hash("browser1"),Rules.hash("browser2"));
    }
}
