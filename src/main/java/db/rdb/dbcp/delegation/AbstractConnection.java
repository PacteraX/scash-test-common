package db.rdb.dbcp.delegation;

//Core Java
import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;

import db.rdb.dbcp.IConnection;

/**
 * this connection wrapper is all methods not supported yet. please override supported methods
 * @author Administrator
 */
abstract class AbstractConnection implements IConnection {
    @Override
    public void clearWarnings() throws SQLException {
        throw new SQLException(NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public void close() throws SQLException {
        throw new SQLException(NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public void commit() throws SQLException {
        throw new SQLException(NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public Array createArrayOf(final String s, final Object[] aobj) throws SQLException {
        throw new SQLException(NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public Blob createBlob() throws SQLException {
        throw new SQLException(NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public Clob createClob() throws SQLException {
        throw new SQLException(NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public NClob createNClob() throws SQLException {
        throw new SQLException(NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public SQLXML createSQLXML() throws SQLException {
        throw new SQLException(NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public Statement createStatement() throws SQLException {
        throw new SQLException(NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public Statement createStatement(final int i, final int j) throws SQLException {
        throw new SQLException(NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public Statement createStatement(final int i, final int j, final int k) throws SQLException {
        throw new SQLException(NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public Struct createStruct(final String s, final Object[] aobj) throws SQLException {
        throw new SQLException(NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public boolean getAutoCommit() throws SQLException {
        throw new SQLException(NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public String getCatalog() throws SQLException {
        throw new SQLException(NOT_SUPPORTED_MESSAGE);

    }

    @Override
    public Properties getClientInfo() throws SQLException {
        throw new SQLException(NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public String getClientInfo(final String s) throws SQLException {
        throw new SQLException(NOT_SUPPORTED_MESSAGE);

    }

    @Override
    public int getHoldability() throws SQLException {
        throw new SQLException(NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        throw new SQLException(NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public int getTransactionIsolation() throws SQLException {
        throw new SQLException(NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public Map<String, Class<?>> getTypeMap() throws SQLException {
        throw new SQLException(NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        throw new SQLException(NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public boolean isClosed() throws SQLException {
        throw new SQLException(NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public boolean isReadOnly() throws SQLException {
        throw new SQLException(NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public boolean isValid(final int i) throws SQLException {
        throw new SQLException(NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public String nativeSQL(final String s) throws SQLException {
        throw new SQLException(NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public CallableStatement prepareCall(final String s) throws SQLException {
        throw new SQLException(NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public CallableStatement prepareCall(final String s, final int i, final int j) throws SQLException {
        throw new SQLException(NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public CallableStatement prepareCall(final String s, final int i, final int j, final int k) throws SQLException {
        throw new SQLException(NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public PreparedStatement prepareStatement(final String s) throws SQLException {
        throw new SQLException(NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public PreparedStatement prepareStatement(final String s, final int i) throws SQLException {
        throw new SQLException(NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public PreparedStatement prepareStatement(final String s, final int[] ai) throws SQLException {
        throw new SQLException(NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public PreparedStatement prepareStatement(final String s, final String[] as) throws SQLException {
        throw new SQLException(NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public PreparedStatement prepareStatement(final String s, final int i, final int j) throws SQLException {
        throw new SQLException(NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public PreparedStatement prepareStatement(final String s, final int i, final int j, final int k) throws SQLException {
        throw new SQLException(NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public void releaseSavepoint(final Savepoint savepoint) throws SQLException {
        throw new SQLException(NOT_SUPPORTED_MESSAGE);

    }

    @Override
    public void rollback() throws SQLException {
        throw new SQLException(NOT_SUPPORTED_MESSAGE);

    }

    @Override
    public void rollback(final Savepoint savepoint) throws SQLException {
        throw new SQLException(NOT_SUPPORTED_MESSAGE);

    }

    @Override
    public void setAutoCommit(final boolean flag) throws SQLException {
        throw new SQLException(NOT_SUPPORTED_MESSAGE);

    }

    @Override
    public void setCatalog(final String s) throws SQLException {
        throw new SQLException(NOT_SUPPORTED_MESSAGE);

    }

    @Override
    public void setClientInfo(final Properties properties) throws SQLClientInfoException {
        throw new SQLClientInfoException();
    }

    @Override
    public void setClientInfo(final String s, final String s1) throws SQLClientInfoException {
        throw new SQLClientInfoException();
    }

    @Override
    public void setHoldability(final int i) throws SQLException {
        throw new SQLException(NOT_SUPPORTED_MESSAGE);

    }

    @Override
    public void setReadOnly(final boolean flag) throws SQLException {
        throw new SQLException(NOT_SUPPORTED_MESSAGE);

    }

    @Override
    public Savepoint setSavepoint() throws SQLException {
        throw new SQLException(NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public Savepoint setSavepoint(final String s) throws SQLException {
        throw new SQLException(NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public void setTransactionIsolation(final int i) throws SQLException {
        throw new SQLException(NOT_SUPPORTED_MESSAGE);

    }

    @Override
    public void setTypeMap(final Map<String, Class<?>> arg0) throws SQLException {
        throw new SQLException(NOT_SUPPORTED_MESSAGE);

    }

    @Override
    public boolean isWrapperFor(final Class<?> arg0) throws SQLException {
        throw new SQLException(NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public <T> T unwrap(final Class<T> arg0) throws SQLException {
        throw new SQLException(NOT_SUPPORTED_MESSAGE);
    }
}
