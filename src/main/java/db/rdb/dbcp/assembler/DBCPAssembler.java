package db.rdb.dbcp.assembler;

import db.rdb.dbcp.DBCP;
import db.rdb.dbcp.delegation.ConnectionFactory;
import db.rdb.dbcp.pool.PoolFactory;
import db.rdb.dbcp.pool.PoolManager;
import db.rdb.dbcp.pool.group.AtomicPoolGroupFactory;
import db.rdb.dbcp.schedule.ScheduleManager;
import db.rdb.dbcp.schedule.ScheduleManagerFactory;
import system.assembler.IAssembler;

public class DBCPAssembler extends BaseDBCPAssembler implements IAssembler {
    @Override
    public void doAssemble() {
        register(new ConnectionFactory());
        PoolManager poolManager = new PoolManager();
        register(poolManager);
        // entry to finalize.
        register(new PoolFactory());
        register(new AtomicPoolGroupFactory());
        ScheduleManager scheduleManager = (ScheduleManager )new ScheduleManagerFactory().create();
        register(scheduleManager);
        DBCP.init();
    }
}
