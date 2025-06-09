package db.rdb.dbcp.assembler;

import db.rdb.dbcp.BaseDBCP;
import db.rdb.dbcp.IConnectionFactory;
import db.rdb.dbcp.IPoolFactory;
import db.rdb.dbcp.IPoolGroupFactory;
import db.rdb.dbcp.IPoolManager;
import db.rdb.dbcp.IScheduleManager;
import db.rdb.tx.ITransaction;
import system.assembler.IAssembler;

public abstract class BaseDBCPAssembler implements IAssembler{

    public void register(IPoolManager f) {
        BaseDBCP.register(f);
    }

    public void register(IConnectionFactory f) {
        BaseDBCP.registerFactory(f);
    }

    public void register(IPoolFactory f) {
        BaseDBCP.registerFactory(f);
    }

    public void register(IPoolGroupFactory f) {
        BaseDBCP.registerFactory(f);
    }

    public void register(IScheduleManager f) {
        BaseDBCP.registerFactory(f);
    }

    public void register(ITransaction tx) {
        BaseDBCP.register(tx);
    }
}
