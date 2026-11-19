package lab.qrlogin;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.jdbc.Sql;
import java.util.*;
import java.util.concurrent.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@SpringBootTest
@AutoConfigureMockMvc
@Sql("/schema.sql")
class ScenarioIT {
    @Autowired ScenarioService service;
    @Autowired Db db;
    @Autowired MockMvc mvc;
    @Test void applicationAndHttpStart() throws Exception {
        mvc.perform(get("/api/scenario")).andExpect(status().isOk()).andExpect(content().string(org.hamcrest.Matchers.containsString("qrlogin")));
    }
    // A start gate creates overlapping transactions; every failure is propagated via Future.get.
    static List<Boolean> race(int n, java.util.function.IntFunction<Boolean> action) throws Exception {
        var pool=Executors.newFixedThreadPool(n);
        var start=new CountDownLatch(1);
        try {
            var tasks=new ArrayList<Future<Boolean>>();
            for(int i=0;i<n;i++) {
                final int id=i;
                tasks.add(pool.submit(()-> {
                    start.await();
                    return action.apply(id);
                }
                ));
            }
            start.countDown();
            var results=new ArrayList<Boolean>();
            for(var f:tasks)results.add(f.get(20,TimeUnit.SECONDS));
            return results;
        }
        finally {
            pool.shutdownNow();
        }
    }
    @Test void exactlyOneRedemption() throws Exception {
        service.create("a","browser-secret",100);
        service.scan("a",1,90);
        service.confirm("a",1,90);
        var r=race(16,i->service.redeem("a","browser-secret",99));
        assertEquals(1,r.stream().filter(Boolean::booleanValue).count());
    }
    @Test void copiedQrAndExpiredSceneCannotRedeem() {
        service.create("a","secret",100);
        assertFalse(service.redeem("a","secret",90));
        assertTrue(service.scan("a",1,90));
        assertFalse(service.confirm("a",2,90));
        assertTrue(service.confirm("a",1,90));
        assertFalse(service.redeem("a","wrong-browser",90));
        assertFalse(service.redeem("a","secret",100));
        assertTrue(service.redeem("a","secret",99));
    }
}
