package db.rdb.dbcp;

import java.util.Collection;

/**
 * connection pool group
 * @author KSF M.Sugawara
 * @author modified HSK Satoh
 */
public interface IPoolGroup {

    void addPool(IPool pool);

//    IPool getPool();

    Collection<IPool> getPoolList();

    String getGroupName();

    boolean isAlias();
}
