package buaazl.lock;

public interface Lock {

    /**
     * try to get lock, otherwise blocked until get the lock
     */
    public void lock();

    /**
     * try to get lock, if can, then get lock; otherwise, return immediately
     * @return
     */
    public boolean tryLock();

    /**
     * try to get lock, if can, then get lock; otherwise, it will wait millisTimeout before return.
     * @param millisTimeout
     * @return
     */
    public boolean tryLock(long millisTimeout);

    /**
     * release lock
     */
    public void unLock();
}
