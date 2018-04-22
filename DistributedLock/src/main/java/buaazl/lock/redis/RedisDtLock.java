package buaazl.lock.redis;

import buaazl.lock.Lock;

/**
 * Created by Vincent on 2018/4/22.
 */
public class RedisDtLock implements Lock {
    public void lock() {

    }

    public boolean tryLock() {
        return false;
    }

    public boolean tryLock(long millisTimeout) {
        return false;
    }

    public void unLock() {

    }
}
