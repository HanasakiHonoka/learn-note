package lab.im;
import org.springframework.stereotype.Service;
import org.springframework.dao.DuplicateKeyException;
import java.util.*;
@Service
public class ScenarioService {
    final Db db;
    public ScenarioService(Db db) {
        this.db=db;
    }
    public long send(long sender,String clientId,String body) {
        if(!Rules.validBody(body))throw new IllegalArgumentException("empty or oversized message");
        return db.transaction(()-> {
            db.jdbc.queryForObject("SELECT next_seq FROM conversation WHERE id=1 FOR UPDATE",Long.class);
            var existing=db.jdbc.queryForList("SELECT seq FROM message WHERE sender=? AND client_id=?",Long.class,sender,clientId);
            if(!existing.isEmpty())return existing.get(0);
            long seq=db.count("SELECT next_seq FROM conversation WHERE id=1")+1;
            db.update("UPDATE conversation SET next_seq=? WHERE id=1",seq);
            db.update("INSERT INTO message(conversation_id,sender,client_id,seq,body) VALUES(1,?,?,?,?)",sender,clientId,seq,body);
            return seq;
        }
        );
    }
    public List<Long> pull(long after) {
        return db.jdbc.queryForList("SELECT seq FROM message WHERE conversation_id=1 AND seq>? ORDER BY seq",Long.class,after);
    }
}
