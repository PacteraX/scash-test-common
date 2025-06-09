package db.rdb;

import java.util.concurrent.atomic.AtomicBoolean;

public class DBConnectionBlockCtrl {

    private final static AtomicBoolean blockage = new AtomicBoolean(false);

    public static void setBlockage() {
        blockage.compareAndSet(false, true);
    }

    public static void offBlockage() {
        blockage.compareAndSet(true, false);
    }

    public static boolean isBlockage() {
        return blockage.get();
    }
}
