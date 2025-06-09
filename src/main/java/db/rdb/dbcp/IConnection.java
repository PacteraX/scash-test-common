package db.rdb.dbcp;

import java.sql.Connection;

import db.rdb.dbcp.IPool.CONN_SHUTDOWN_EVENT;

public interface IConnection extends Connection {
    String NOT_SUPPORTED_MESSAGE = "Not Supported Yet";

    boolean checkAndshutdown();
    boolean checkAndshutdown(CONN_SHUTDOWN_EVENT event);

    String getGroupName();

    String getDbName();

    long getCreateTime();

    long getOriginalCreateTime();

    void shutdown() throws Throwable;

    boolean isInside();

    void insidePool();

    void outsidePool();

    boolean isScashClosed();
}
