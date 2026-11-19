package lab.orderexpiry;
import org.springframework.stereotype.Service;
import org.springframework.dao.DuplicateKeyException;
import java.util.*;
@Service
public class ScenarioService {
    final Db db;
    public ScenarioService(Db db) {
        this.db=db;
    }
    public boolean close(long now) {
        if(!Rules.due(now,100))return false;
        return db.transaction(()-> {
            if(db.update("UPDATE orders SET state='CLOSED' WHERE id=1 AND state='PENDING' AND expires_at<=?",now)==0)return false;
            db.update("INSERT INTO release_event VALUES(1)");
            return true;
        }
        );
    }
    public boolean paid() {
        return db.transaction(()-> {
            // Lock serializes channel-success handling with close. CLOSED cannot be silently reopened.
            String state=db.jdbc.queryForObject("SELECT state FROM orders WHERE id=1 FOR UPDATE",String.class);
            if("PENDING".equals(state)) {
                db.update("UPDATE orders SET state='PAID' WHERE id=1");
                return true;
            }
            if("CLOSED".equals(state))db.update("MERGE INTO refund_task KEY(order_id) VALUES(1)");
            return false;
        }
        );
    }
}
