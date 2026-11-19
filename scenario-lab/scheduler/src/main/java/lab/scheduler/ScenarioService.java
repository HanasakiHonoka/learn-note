package lab.scheduler;
import org.springframework.stereotype.Service;
import org.springframework.dao.DuplicateKeyException;
import java.util.*;
@Service
public class ScenarioService {
    final Db db;
    public ScenarioService(Db db) {
        this.db=db;
    }
    public long acquire(String owner,long now,long until) {
        if(!Rules.leaseValid(now,until))throw new IllegalArgumentException("lease");
        return db.transaction(()-> {
            if(db.update("UPDATE job_run SET owner=?,lease_until=?,token=token+1 WHERE id=1 AND done=FALSE AND lease_until<=?",owner,until,now)==0)return 0L;
            return db.count("SELECT token FROM job_run WHERE id=1");
        }
        );
    }
    public boolean write(long token,String value) {
        return db.update("UPDATE protected_resource SET payload=?,last_token=? WHERE id=1 AND last_token<?",value,token,token)==1;
    }
    public boolean finish(String owner,long token,long now) {
        return db.update("UPDATE job_run SET done=TRUE WHERE id=1 AND owner=? AND token=? AND lease_until>? AND done=FALSE",owner,token,now)==1;
    }
}
