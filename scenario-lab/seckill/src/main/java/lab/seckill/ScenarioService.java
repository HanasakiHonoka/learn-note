package lab.seckill;
import org.springframework.stereotype.Service;
import org.springframework.dao.DuplicateKeyException;
import java.util.*;
@Service
public class ScenarioService {
    final Db db;
    public ScenarioService(Db db) {
        this.db=db;
    }
    public boolean buy(long user,boolean fail) {
        if(!Rules.valid(user))throw new IllegalArgumentException("user");
        try {
            return db.transaction(()-> {
                if(db.update("UPDATE stock SET available=available-1 WHERE id=1 AND available>0")==0)return false;
                db.update("INSERT INTO orders(activity_id,user_id) VALUES(1,?)",user);
                if(fail)throw new IllegalStateException("injected crash after order insert");
                return true;
            }
            );
        }
        catch(DuplicateKeyException e) {
            return false;
        }
    }
}
