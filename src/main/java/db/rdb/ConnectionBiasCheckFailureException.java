package db.rdb;

public class ConnectionBiasCheckFailureException extends RuntimeException{
    private static final long serialVersionUID = 1L;

    public ConnectionBiasCheckFailureException(String message) {
        super(message);
    }

    public ConnectionBiasCheckFailureException(String message, Throwable cause) {
        super(message, cause);
    }
}
