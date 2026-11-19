package lab.ranking;
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
        mvc.perform(get("/api/scenario")).andExpect(status().isOk()).andExpect(content().string(org.hamcrest.Matchers.containsString("ranking")));
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
    @Test void duplicatedEventsDoNotInflateScore() throws Exception {
        race(16,i->service.add("same",10));
        assertEquals(10,db.count("SELECT points FROM score WHERE user_id=1"));
        assertEquals(1,db.count("SELECT version FROM score WHERE user_id=1"));
    }
    @Test void outOfOrderProjectionCannotOverwriteNewer() {
        assertTrue(service.project(30,3));
        assertFalse(service.project(10,1));
        assertFalse(service.project(30,3));
        assertEquals(30,db.count("SELECT points FROM rank_view WHERE user_id=1"));
    }
}
