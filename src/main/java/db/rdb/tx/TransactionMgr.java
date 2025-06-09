package db.rdb.tx;

import db.rdb.dbcp.BaseDBCP;
//Core Java

public class TransactionMgr {

   //     --------------- Public methods --------------- //
    public static void setDefaultTransaction(ITransaction tx){
        BaseDBCP.register(tx);
    }

    public static ITransaction getDefaultTransaction() {
        return BaseDBCP.getTransaction();
    }

    public static ITransaction getTransaction() {
        return getDefaultTransaction();
    }

    public static void begin( ITxProc proc ) {
        BaseDBCP.getTransaction().submit( proc );
    }

    /**
     * @param name
     * @param tx
     */
    @Deprecated
    public static void addTransaction(String name, ITransaction tx){
    }

    /**
     * @param name
     */
    @Deprecated
    public static ITransaction getTransaction(String name){
       return getTransaction();
    }

    /**
     * @param dbName
     */
    @Deprecated
    public static void begin( ITxProc proc, String dbName) {
        BaseDBCP.getTransaction().submit( proc );
    }
}
