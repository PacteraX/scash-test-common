package db.rdb;

import java.sql.SQLException;

public class DuplicateKeyException extends SQLException{
    private static final long serialVersionUID = 1L;

    public DuplicateKeyException(Throwable cause) {
        super(cause);
    }

    public DuplicateKeyException(String reason, Throwable cause) {
        super(reason, cause);
    }
}
