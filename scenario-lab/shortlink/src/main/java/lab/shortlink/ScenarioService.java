package lab.shortlink;
import org.springframework.stereotype.Service;
import org.springframework.dao.DuplicateKeyException;
import java.util.*;
@Service
public class ScenarioService {
    final Db db;
    public ScenarioService(Db db) {
        this.db=db;
    }
    public String create(String target,long expires,java.util.function.Supplier<String> codes) {
        if(!Rules.safe(target))throw new IllegalArgumentException("http(s) required");
        for(int i=0;i<5;i++) {
            String code=codes.get();
            try {
                db.update("INSERT INTO short_link VALUES(?,?,?)",code,target,expires);
                return code;
            }
            catch(DuplicateKeyException ignored) {
            }
        }
        throw new IllegalStateException("collision budget exhausted");
    }
    public String resolve(String code,long now) {
        var rows=db.jdbc.queryForList("SELECT target FROM short_link WHERE code=? AND expires_at>?",String.class,code,now);
        if(rows.isEmpty())throw new NoSuchElementException("expired or missing");
        return rows.get(0);
    }
}
