package lab.payment;
import org.springframework.stereotype.Service;
import org.springframework.dao.DuplicateKeyException;
import java.util.*;
@Service
public class ScenarioService {
    final Db db;
    public ScenarioService(Db db) {
        this.db=db;
    }
    public boolean confirm(long amount,boolean crash) {
        return db.transaction(()-> {
            long expected=db.count("SELECT amount FROM payment WHERE id=1");
            if(!Rules.matches(expected,amount))throw new IllegalArgumentException("amount mismatch");
            if(db.update("UPDATE payment SET state='SUCCESS' WHERE id=1 AND state='PROCESSING'")==0)return false;
            db.update("INSERT INTO ledger VALUES(1,?)",amount);
            db.update("INSERT INTO outbox(payment_id) VALUES(1)");
            if(crash)throw new IllegalStateException("crash before commit");
            return true;
        }
        );
    }
    public boolean refund(String request,long amount) {
        if(amount<=0)throw new IllegalArgumentException("amount");
        try {
            return db.transaction(()-> {
                if(db.update("UPDATE payment SET refunded=refunded+? WHERE id=1 AND state='SUCCESS' AND refunded+?<=amount",amount,amount)==0)return false;
                db.update("INSERT INTO refund VALUES(?,1,?)",request,amount);
                return true;
            }
            );
        }
        catch(DuplicateKeyException e) {
            return false;
        }
    }
}
