package lab.qrlogin;
import org.springframework.stereotype.Component;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import java.util.function.Supplier;
@Component
public class Db {
    final JdbcTemplate jdbc;
    final TransactionTemplate tx;
    public Db(JdbcTemplate jdbc, PlatformTransactionManager manager) {
        this.jdbc=jdbc;
        this.tx=new TransactionTemplate(manager);
    }
    public int update(String sql,Object... args) {
        return jdbc.update(sql,args);
    }
    public long count(String sql,Object... args) {
        return jdbc.queryForObject(sql,Long.class,args);
    }
    public <T> T transaction(Supplier<T> action) {
        return tx.execute(status -> action.get());
    }
}
