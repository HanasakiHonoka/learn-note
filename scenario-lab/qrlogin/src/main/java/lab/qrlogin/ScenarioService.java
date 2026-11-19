package lab.qrlogin;
import org.springframework.stereotype.Service;
import org.springframework.dao.DuplicateKeyException;
import java.util.*;
@Service
public class ScenarioService {
    final Db db;
    public ScenarioService(Db db) {
        this.db=db;
    }
    public void create(String id,String secret,long expires) {
        db.update("INSERT INTO scene(id,secret_hash,state,expires_at) VALUES(?,?,'NEW',?)",id,Rules.hash(secret),expires);
    }
    public boolean scan(String id,long user,long now) {
        return db.update("UPDATE scene SET state='SCANNED',user_id=? WHERE id=? AND state='NEW' AND expires_at>?",user,id,now)==1;
    }
    public boolean confirm(String id,long user,long now) {
        return db.update("UPDATE scene SET state='CONFIRMED' WHERE id=? AND user_id=? AND state='SCANNED' AND expires_at>?",id,user,now)==1;
    }
    public boolean redeem(String id,String secret,long now) {
        return db.update("UPDATE scene SET state='CONSUMED' WHERE id=? AND secret_hash=? AND state='CONFIRMED' AND expires_at>?",id,Rules.hash(secret),now)==1;
    }
}
