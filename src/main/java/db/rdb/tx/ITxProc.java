package db.rdb.tx;
//Core Java
import java.sql.Statement;

/*
 * 注意：PoolGroupを使う時、複数スレッドから同じITxProcオブジェクトを並列な実行するので、
 * ITxProcの実装は必ずStatelessにする。
 */
public interface ITxProc {
	void execute( Statement stmt ) throws Exception ;
}

