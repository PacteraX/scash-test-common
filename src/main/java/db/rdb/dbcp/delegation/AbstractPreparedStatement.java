package db.rdb.dbcp.delegation;

//Core Java
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.NClob;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;

import db.rdb.dbcp.IConnection;

/**
 * this connection wrapper is all methods not supported yet. please override supported methods
 * @author Administrator
 */
public abstract class AbstractPreparedStatement implements PreparedStatement {
    @Override
    public void addBatch() throws SQLException {
        throw new SQLException(IConnection.NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public void clearParameters() throws SQLException {
        throw new SQLException(IConnection.NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public boolean execute() throws SQLException {
        throw new SQLException(IConnection.NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public ResultSet executeQuery() throws SQLException {
        throw new SQLException(IConnection.NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public int executeUpdate() throws SQLException {
        throw new SQLException(IConnection.NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        throw new SQLException(IConnection.NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public ParameterMetaData getParameterMetaData() throws SQLException {
        throw new SQLException(IConnection.NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public void setArray(final int i, final Array array) throws SQLException {
        throw new SQLException(IConnection.NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public void setAsciiStream(final int i, final InputStream inputstream) throws SQLException {
        throw new SQLException(IConnection.NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public void setAsciiStream(final int i, final InputStream inputstream, final int j) throws SQLException {
        throw new SQLException(IConnection.NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public void setAsciiStream(final int i, final InputStream inputstream, final long l) throws SQLException {
        throw new SQLException(IConnection.NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public void setBigDecimal(final int i, final BigDecimal bigdecimal) throws SQLException {
        throw new SQLException(IConnection.NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public void setBinaryStream(final int i, final InputStream inputstream) throws SQLException {
        throw new SQLException(IConnection.NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public void setBinaryStream(final int i, final InputStream inputstream, final int j) throws SQLException {
        throw new SQLException(IConnection.NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public void setBinaryStream(final int i, final InputStream inputstream, final long l) throws SQLException {
        throw new SQLException(IConnection.NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public void setBlob(final int i, final Blob blob) throws SQLException {
        throw new SQLException(IConnection.NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public void setBlob(final int i, final InputStream inputstream) throws SQLException {
        throw new SQLException(IConnection.NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public void setBlob(final int i, final InputStream inputstream, final long l) throws SQLException {
        throw new SQLException(IConnection.NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public void setBoolean(final int i, final boolean flag) throws SQLException {
        throw new SQLException(IConnection.NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public void setByte(final int i, final byte byte0) throws SQLException {
        throw new SQLException(IConnection.NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public void setBytes(final int i, final byte[] abyte0) throws SQLException {
        throw new SQLException(IConnection.NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public void setCharacterStream(final int i, final Reader reader) throws SQLException {
        throw new SQLException(IConnection.NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public void setCharacterStream(final int i, final Reader reader, final int j) throws SQLException {
        throw new SQLException(IConnection.NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public void setCharacterStream(final int i, final Reader reader, final long l) throws SQLException {
        throw new SQLException(IConnection.NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public void setClob(final int i, final Clob clob) throws SQLException {
        throw new SQLException(IConnection.NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public void setClob(final int i, final Reader reader) throws SQLException {
        throw new SQLException(IConnection.NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public void setClob(final int i, final Reader reader, final long l) throws SQLException {
        throw new SQLException(IConnection.NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public void setDate(final int i, final Date date) throws SQLException {
        throw new SQLException(IConnection.NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public void setDate(final int i, final Date date, final Calendar calendar) throws SQLException {
        throw new SQLException(IConnection.NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public void setDouble(final int i, final double d) throws SQLException {
        throw new SQLException(IConnection.NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public void setFloat(final int i, final float f) throws SQLException {
        throw new SQLException(IConnection.NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public void setInt(final int i, final int j) throws SQLException {
        throw new SQLException(IConnection.NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public void setLong(final int i, final long l) throws SQLException {
        throw new SQLException(IConnection.NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public void setNCharacterStream(final int i, final Reader reader) throws SQLException {
        throw new SQLException(IConnection.NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public void setNCharacterStream(final int i, final Reader reader, final long l) throws SQLException {
        throw new SQLException(IConnection.NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public void setNClob(final int i, final NClob nclob) throws SQLException {
        throw new SQLException(IConnection.NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public void setNClob(final int i, final Reader reader) throws SQLException {
        throw new SQLException(IConnection.NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public void setNClob(final int i, final Reader reader, final long l) throws SQLException {
        throw new SQLException(IConnection.NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public void setNString(final int i, final String s) throws SQLException {
        throw new SQLException(IConnection.NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public void setNull(final int i, final int j) throws SQLException {
        throw new SQLException(IConnection.NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public void setNull(final int i, final int j, final String s) throws SQLException {
        throw new SQLException(IConnection.NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public void setObject(final int i, final Object obj) throws SQLException {
        throw new SQLException(IConnection.NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public void setObject(final int i, final Object obj, final int j) throws SQLException {
        throw new SQLException(IConnection.NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public void setObject(final int i, final Object obj, final int j, final int k) throws SQLException {
        throw new SQLException(IConnection.NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public void setRef(final int i, final Ref ref) throws SQLException {
        throw new SQLException(IConnection.NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public void setRowId(final int i, final RowId rowid) throws SQLException {
        throw new SQLException(IConnection.NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public void setSQLXML(final int i, final SQLXML sqlxml) throws SQLException {
        throw new SQLException(IConnection.NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public void setShort(final int i, final short word0) throws SQLException {
        throw new SQLException(IConnection.NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public void setString(final int i, final String s) throws SQLException {
        throw new SQLException(IConnection.NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public void setTime(final int i, final Time time) throws SQLException {
        throw new SQLException(IConnection.NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public void setTime(final int i, final Time time, final Calendar calendar) throws SQLException {
        throw new SQLException(IConnection.NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public void setTimestamp(final int i, final Timestamp timestamp) throws SQLException {
        throw new SQLException(IConnection.NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public void setTimestamp(final int i, final Timestamp timestamp, final Calendar calendar) throws SQLException {
        throw new SQLException(IConnection.NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public void setURL(final int i, final URL url) throws SQLException {
        throw new SQLException(IConnection.NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public void setUnicodeStream(final int i, final InputStream inputstream, final int j) throws SQLException {
        throw new SQLException(IConnection.NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public void addBatch(final String s) throws SQLException {
        throw new SQLException(IConnection.NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public void cancel() throws SQLException {
        throw new SQLException(IConnection.NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public void clearBatch() throws SQLException {
        throw new SQLException(IConnection.NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public void clearWarnings() throws SQLException {
        throw new SQLException(IConnection.NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public void close() throws SQLException {
        throw new SQLException(IConnection.NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public boolean execute(final String s) throws SQLException {
        throw new SQLException(IConnection.NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public boolean execute(final String s, final int i) throws SQLException {
        throw new SQLException(IConnection.NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public boolean execute(final String s, final int[] ai) throws SQLException {
        throw new SQLException(IConnection.NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public boolean execute(final String s, final String[] as) throws SQLException {
        throw new SQLException(IConnection.NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public int[] executeBatch() throws SQLException {
        throw new SQLException(IConnection.NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public ResultSet executeQuery(final String s) throws SQLException {
        throw new SQLException(IConnection.NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public int executeUpdate(final String s) throws SQLException {
        throw new SQLException(IConnection.NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public int executeUpdate(final String s, final int i) throws SQLException {
        throw new SQLException(IConnection.NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public int executeUpdate(final String s, final int[] ai) throws SQLException {
        throw new SQLException(IConnection.NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public int executeUpdate(final String s, final String[] as) throws SQLException {
        throw new SQLException(IConnection.NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public Connection getConnection() throws SQLException {
        throw new SQLException(IConnection.NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public int getFetchDirection() throws SQLException {
        throw new SQLException(IConnection.NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public int getFetchSize() throws SQLException {
        throw new SQLException(IConnection.NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public ResultSet getGeneratedKeys() throws SQLException {
        throw new SQLException(IConnection.NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public int getMaxFieldSize() throws SQLException {
        throw new SQLException(IConnection.NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public int getMaxRows() throws SQLException {
        throw new SQLException(IConnection.NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public boolean getMoreResults() throws SQLException {
        throw new SQLException(IConnection.NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public boolean getMoreResults(final int i) throws SQLException {
        throw new SQLException(IConnection.NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public int getQueryTimeout() throws SQLException {
        throw new SQLException(IConnection.NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public ResultSet getResultSet() throws SQLException {
        throw new SQLException(IConnection.NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public int getResultSetConcurrency() throws SQLException {
        throw new SQLException(IConnection.NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public int getResultSetHoldability() throws SQLException {
        throw new SQLException(IConnection.NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public int getResultSetType() throws SQLException {
        throw new SQLException(IConnection.NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public int getUpdateCount() throws SQLException {
        throw new SQLException(IConnection.NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        throw new SQLException(IConnection.NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public boolean isClosed() throws SQLException {
        throw new SQLException(IConnection.NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public boolean isPoolable() throws SQLException {
        throw new SQLException(IConnection.NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public void setCursorName(final String s) throws SQLException {
        throw new SQLException(IConnection.NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public void setEscapeProcessing(final boolean flag) throws SQLException {
        throw new SQLException(IConnection.NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public void setFetchDirection(final int i) throws SQLException {
        throw new SQLException(IConnection.NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public void setFetchSize(final int i) throws SQLException {
        throw new SQLException(IConnection.NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public void setMaxFieldSize(final int i) throws SQLException {
        throw new SQLException(IConnection.NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public void setMaxRows(final int i) throws SQLException {
        throw new SQLException(IConnection.NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public void setPoolable(final boolean flag) throws SQLException {
        throw new SQLException(IConnection.NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public void setQueryTimeout(final int i) throws SQLException {
        throw new SQLException(IConnection.NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public boolean isWrapperFor(final Class<?> arg0) throws SQLException {
        throw new SQLException(IConnection.NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public <T> T unwrap(final Class<T> arg0) throws SQLException {
        throw new SQLException(IConnection.NOT_SUPPORTED_MESSAGE);
    }
}
