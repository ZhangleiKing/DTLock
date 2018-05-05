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
    boolean tryLock();

    /**
     * try to get lock, if can, then get lock; otherwise, it will wait millisTimeout before return.
     * @param millisTimeout
     * @return
     */
    boolean tryLock(long millisTimeout);

    /**
     * release lock
     */
    void unLock();

    /**
     * if got lock, return true
     * @return
     */
    boolean getLocked();

    String lockInfo();
}
