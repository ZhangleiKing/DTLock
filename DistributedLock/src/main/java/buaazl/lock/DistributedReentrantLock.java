package buaazl.lock;

/**
 * Created by Vincent on 2018/5/10.
 */
public class DistributedReentrantLock implements Lock {
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

    public boolean getLocked() {
        return false;
    }

    public String lockInfo() {
        return null;
    }
}
