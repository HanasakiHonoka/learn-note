package lab.cache;
import org.springframework.stereotype.Service;
import org.springframework.dao.DuplicateKeyException;
import java.util.*;
@Service
public class ScenarioService {
    final Db db;
    public ScenarioService(Db db) {
        this.db=db;
    }
    public record Snapshot(long price,long version) {
    }
    public Snapshot readDb() {
        return db.jdbc.queryForObject("SELECT price,version FROM product WHERE id=1",(r,n)->new Snapshot(r.getLong(1),r.getLong(2)));
    }
    public void updatePrice(long price) {
        db.transaction(()-> {
            db.update("UPDATE product SET price=?,version=version+1 WHERE id=1",price);
            Snapshot current=readDb();
            db.update("UPDATE cache_entry SET price=?,version=? WHERE id=1",current.price(),current.version());
            return null;
        }
        );
    }
    public boolean fill(Snapshot old) {
        if(!Rules.versionValid(old.version()))throw new IllegalArgumentException("version");
        return db.update("UPDATE cache_entry SET price=?,version=? WHERE id=1 AND version<=?",old.price(),old.version(),old.version())==1;
    }
    public long cachedPrice() {
        return db.count("SELECT price FROM cache_entry WHERE id=1");
    }
}
