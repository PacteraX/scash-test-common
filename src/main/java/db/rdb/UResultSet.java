/**
 * 
 */
package db.rdb;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

/**
 * @author Yaowu Lan
 *
 */
@Deprecated
public class UResultSet {

	public static Object getValue(ResultSet results, int index, int type) throws SQLException {
		switch(type){
			case Types.INTEGER :
				return results.getInt(index);
			case Types.BIGINT :
				return results.getLong(index);
			case Types.DOUBLE :
				return results.getDouble(index);
			case Types.SMALLINT :
				return results.getShort(index);
			case Types.TINYINT :
				return results.getShort(index);
			case Types.VARCHAR :
				return getBytes(results, index);
			case Types.CHAR :
				return getBytes(results, index);
			default :
				return results.getObject(index);
		}
	}

	public static byte[] getBytes(ResultSet results, int index) throws SQLException{
		try{
			return results.getBytes(index);
		}catch(SQLException e){
			return results.getObject(index).toString().getBytes();
		}
	}
}
