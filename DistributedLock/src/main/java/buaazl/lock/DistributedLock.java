package buaazl.lock;


public class DistributedLock implements Lock{
    private Lock lock;
    private String type;

    public DistributedLock() {

    }

    public void lock() {
        lock.lock();
    }

    public boolean tryLock() {
        return lock.tryLock();
    }

    public boolean tryLock(long millisTimeout) {
        return lock.tryLock(millisTimeout);
    }

    public void unLock() {
        lock.unLock();
    }

    public boolean getLocked() {
        return false;
    }

    public String lockInfo() {
        return lock.lockInfo();
    }
}
