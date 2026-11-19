package lab.seckill;
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
        mvc.perform(get("/api/scenario")).andExpect(status().isOk()).andExpect(content().string(org.hamcrest.Matchers.containsString("seckill")));
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
    @Test void noOversellUnderContention() throws Exception {
        var r=race(24,i->service.buy(i+1,false));
        assertEquals(5,r.stream().filter(Boolean::booleanValue).count());
        assertEquals(0,db.count("SELECT available FROM stock WHERE id=1"));
        assertEquals(5,db.count("SELECT COUNT(*) FROM orders"));
    }
    @Test void duplicateAndRollback() throws Exception {
        race(12,i->service.buy(1,false));
        assertEquals(1,db.count("SELECT COUNT(*) FROM orders"));
        assertEquals(4,db.count("SELECT available FROM stock WHERE id=1"));
        assertThrows(IllegalStateException.class,()->service.buy(2,true));
        assertEquals(4,db.count("SELECT available FROM stock WHERE id=1"));
    }
    @Test void businessHttpRequestPersistsOrder() throws Exception {
        mvc.perform(post("/api/buy").param("user","42")).andExpect(status().isOk()).andExpect(content().string("true"));
        assertEquals(1,db.count("SELECT COUNT(*) FROM orders WHERE user_id=42"));
    }
}
