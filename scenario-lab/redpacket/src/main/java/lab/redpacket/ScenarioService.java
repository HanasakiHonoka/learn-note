package lab.redpacket;
import org.springframework.stereotype.Service;
import org.springframework.dao.DuplicateKeyException;
import java.util.*;
@Service
public class ScenarioService {
    final Db db;
    public ScenarioService(Db db) {
        this.db=db;
    }
    public boolean claim(long user) {
        try {
            return db.transaction(()-> {
                var row=db.jdbc.queryForMap("SELECT remaining,pieces FROM packet WHERE id=1 FOR UPDATE");
                long remaining=((Number)row.get("remaining")).longValue();
                int pieces=((Number)row.get("pieces")).intValue();
                if(pieces==0)return false;
                long amount=Rules.share(remaining,pieces);
                db.update("INSERT INTO claim VALUES(1,?,?)",user,amount);
                db.update("UPDATE packet SET remaining=remaining-?,pieces=pieces-1 WHERE id=1",amount);
                return true;
            }
            );
        }
        catch(DuplicateKeyException e) {
            return false;
        }
    }
    // Deterministic winning branch: exercise inventory and idempotency, not probability.
    public boolean draw(String request) {
        try {
            return db.transaction(()-> {
                if(db.update("UPDATE prize SET available=available-1 WHERE id=1 AND available>0")==0)return false;
                db.update("INSERT INTO draw_result VALUES(?,1)",request);
                return true;
            }
            );
        }
        catch(DuplicateKeyException e) {
            return false;
        }
    }
}
