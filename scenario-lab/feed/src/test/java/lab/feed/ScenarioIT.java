package lab.feed;
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
        mvc.perform(get("/api/scenario")).andExpect(status().isOk()).andExpect(content().string(org.hamcrest.Matchers.containsString("feed")));
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
    @Test void pushPullAndStableCursor() {
        service.publish(1,1,100,false);
        service.publish(2,2,100,true);
        assertEquals(1,db.count("SELECT COUNT(*) FROM inbox"));
        assertEquals(List.of(2L,1L),service.timeline(10,101,Long.MAX_VALUE));
        assertEquals(List.of(1L),service.timeline(10,100,2));
    }
    @Test void revocationFiltersStaleInbox() {
        service.publish(1,1,100,false);
        service.revoke(10,1);
        assertEquals(1,db.count("SELECT COUNT(*) FROM inbox"));
        assertTrue(service.timeline(10,101,99).isEmpty());
    }
}
