package buaazl.lock.exception;

/**
 * Created by Vincent on 2018/4/22.
 */
public class ZkLockException extends RuntimeException {

    public ZkLockException(String msg, Exception e) {
        super(msg, e);
    }

    public ZkLockException(String msg) {
        super(msg);
    }

    public ZkLockException(Exception e) {
        super(e);
    }
}
