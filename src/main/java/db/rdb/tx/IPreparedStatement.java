/**
 *
 */
package db.rdb.tx;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public interface IPreparedStatement extends PreparedStatement{
    public enum OPERATION {
        INSERT,
        UPDATE,
        DELETE,
        CREATE_TABLE,
        DROP_TABLE
    }

    public enum EXECUTE_BATCH_RESULT{
        NOT_EXECUTED,
        SUCCESS,
        TIMEOUT_ERROR,
        LOGICAL_ERROR,
        SYSTEM_ERROR,
        FAILOVER_ERROR,
        ROLLBACK
    }

    public void addBatch(
            String sql,
            List<Object> params,
            String tableName,
            String wherePhrase,
            List<List<Object>> keysList,
            OPERATION operation)throws SQLException;

    public EXECUTE_BATCH_RESULT getExecuteBatchResult();
    public void rollback() throws SQLException;
    public PreparedStatement prepareStatement(String groupName) throws SQLException ;
}
