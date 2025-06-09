package db.rdb.dbcp.pool;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import db.rdb.dbcp.IConnection;
import system.config.Configure;

public class PoolQueue {

    private static final int CONNECTON_POLL_TIMEOUT = 30000;
    private static final String CONNECTION_POLL_TIMEOUT_KEY = "dbcp.pool.factory.poll.timeout";
    private final int connectionPollTimeout;
    private final Semaphore semaphore = new Semaphore(0);
    final ConcurrentLinkedQueue<IConnection> queue = new ConcurrentLinkedQueue<>();

    public PoolQueue() {
        this.connectionPollTimeout = getPollTImeout();
    }

    private int getPollTImeout() {
        if (Configure.containsKey(CONNECTION_POLL_TIMEOUT_KEY)) {
            return Configure.getInt(CONNECTION_POLL_TIMEOUT_KEY);
        }
        return CONNECTON_POLL_TIMEOUT;
    }

    public void add(IConnection conn) {
        if(!conn.isInside()) {
            conn.insidePool();
            queue.add(conn);
            semaphore.release();
        }
    }

    public IConnection poll() {
        if (semaphore.tryAcquire()) {
            IConnection conn = this.queue.poll();
            conn.outsidePool();
            return conn;
        }
        return null;
    }

    public int size() {
        return semaphore.availablePermits();
    }

    public IConnection take(long timeout) {
        try {
            if (semaphore.tryAcquire(timeout, TimeUnit.MILLISECONDS)) {
                IConnection conn = this.queue.poll();
                conn.outsidePool();
                return conn;
            }
        } catch (InterruptedException e) {
        }
        return null;
    }

    public IConnection take() {
        return take(connectionPollTimeout);
    }
}
