package lab.feed;
import org.springframework.stereotype.Service;
import org.springframework.dao.DuplicateKeyException;
import java.util.*;
@Service
public class ScenarioService {
    final Db db;
    public ScenarioService(Db db) {
        this.db=db;
    }
    public void publish(long id,long author,long at,boolean celebrity) {
        db.transaction(()-> {
            db.update("INSERT INTO post(id,author_id,created_at,celebrity) VALUES(?,?,?,?)",id,author,at,celebrity);
            if(Rules.push(celebrity)) db.update("INSERT INTO inbox SELECT reader_id,? FROM friendship WHERE author_id=?",id,author);
            return null;
        }
        );
    }
    public List<Long> timeline(long reader,long beforeTime,long beforeId) {
        // Pull candidate posts, then re-check current relationship and deletion, including pushed entries.
        return db.jdbc.queryForList("SELECT p.id FROM post p JOIN friendship f ON f.author_id=p.author_id AND f.reader_id=? WHERE (p.celebrity=TRUE OR EXISTS(SELECT 1 FROM inbox i WHERE i.reader_id=f.reader_id AND i.post_id=p.id)) AND p.deleted=FALSE AND (p.created_at<? OR (p.created_at=? AND p.id<?)) ORDER BY p.created_at DESC,p.id DESC LIMIT 20",Long.class,reader,beforeTime,beforeTime,beforeId);
    }
    public void revoke(long reader,long author) {
        db.update("DELETE FROM friendship WHERE reader_id=? AND author_id=?",reader,author);
    }
}
