package db.rdb.tx;

public interface ITransaction {
	void submit(ITxProc txProc) ;
}
