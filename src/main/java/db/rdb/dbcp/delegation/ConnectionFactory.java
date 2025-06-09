package db.rdb.dbcp.delegation;

//Core Java

import java.sql.Connection;
import java.sql.SQLException;

import db.rdb.dbcp.DBCP;
import db.rdb.dbcp.IPool;
import db.rdb.dbcp.IConnection;
import db.rdb.dbcp.IConnectionFactory;

public class ConnectionFactory implements IConnectionFactory {

    @Override
    public IConnection create(IPool pool, Connection con) {
        try {
            if(DBCP.isOracle()) {
                return new ORCLConnection(pool, con);
            } else if (DBCP.isPostgres()) {
                return new PSQLConnection(pool, con);
            } else {
                throw new SQLException(IConnection.NOT_SUPPORTED_MESSAGE);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
