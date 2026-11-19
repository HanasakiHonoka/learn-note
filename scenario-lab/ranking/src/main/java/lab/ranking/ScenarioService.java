package lab.ranking;
import org.springframework.stereotype.Service;
import org.springframework.dao.DuplicateKeyException;
import java.util.*;
@Service
public class ScenarioService {
    final Db db;
    public ScenarioService(Db db) {
        this.db=db;
    }
    public boolean add(String event,long delta) {
        if(delta<=0)throw new IllegalArgumentException("positive delta");
        try {
            return db.transaction(()-> {
                db.update("INSERT INTO score_event VALUES(?,1,?)",event,delta);
                db.update("UPDATE score SET points=points+?,version=version+1 WHERE user_id=1",delta);
                return true;
            }
            );
        }
        catch(DuplicateKeyException e) {
            return false;
        }
    }
    public boolean project(long points,long version) {
        if(!Rules.newer(version,0))return false;
        return db.update("UPDATE rank_view SET points=?,version=? WHERE user_id=1 AND version<?",points,version,version)==1;
    }
}
