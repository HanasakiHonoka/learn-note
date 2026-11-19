package lab.redpacket;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class RulesTest {
    @Test void businessBoundary() {
        assertEquals(7,Rules.share(7,1));
        assertEquals(2,Rules.share(5,2));
        assertThrows(IllegalArgumentException.class,()->Rules.share(1,2));
    }
}
