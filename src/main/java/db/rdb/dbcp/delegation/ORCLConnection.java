package db.rdb.dbcp.delegation;

import java.sql.Blob;
import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

import db.rdb.dbcp.IPool;

public class ORCLConnection extends Connection {

    protected ORCLConnection(IPool ipool, java.sql.Connection iconnection) throws SQLException {
        super(ipool, iconnection);
    }

    public void lobRelease() {
        try {
            Iterator<Object> iterator = lobs.iterator();
            while(iterator.hasNext()) {
                Object lob = iterator.next();
                try {
                    if (lob instanceof Clob) {
                        ((Clob) lob).free();
                    } else if (lob instanceof Blob) {
                        ((Blob) lob).free();
                    }
                } catch (SQLException e) {
                }
            }
        } finally {
            lobs = new ArrayList<>();
        }
    }

    @Override
    public synchronized void close() throws SQLException {

        connectionCloseCheck();

        if (!isScashClosed()) {
            if (this.lobs.size()>0) {
                lobRelease();
            }
            this.pool.returnTo(this);
        }
    }

    @Override
    public void processConfirmConnectionCondition(java.sql.Connection conn) {
        setValidationQueryTimeoutSchedule();
        try (PreparedStatement pstmt = conn.prepareStatement(validationQuery)) {
            pstmt.executeQuery();
        } catch (Exception ex) {
            checkAndshutdown();
        } finally {
            try {
                checkValidationQueryError();
            } catch (SQLException e1) {
            }
        }
    }
}
