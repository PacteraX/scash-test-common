package db.rdb.dbcp.delegation;

import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.util.TimerTask;

/**
 * @author satoh
 */
public class RollbackTimeoutChecker extends TimerTask {
    private SQLException error;
    private Connection connection;
    private long startTime;

    public RollbackTimeoutChecker(Connection con) {
        super();
        this.connection = con;
        this.startTime = System.nanoTime();
    }

    public long getStartTime() {
        return startTime;
    }

    @Override
    public synchronized final void run() {
        this.error = new SQLTimeoutException("Rollback timeout error.");
        try {
            while (!connection.checkAndshutdown()) {
                Thread.sleep((connection.getPool().getSource().getRollbackSleep()));
            }
        } catch (Throwable e) {
        }
        this.connection = null;
    }

    /**
     * @return the error
     */
    public synchronized SQLException getError() {
        SQLException ex = this.error;
        return ex;
    }
}

