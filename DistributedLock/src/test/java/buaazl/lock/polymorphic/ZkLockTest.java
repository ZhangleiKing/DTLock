package buaazl.lock.polymorphic;

/**
 * Created by Vincent on 2018/4/22.
 */
public class ZkLockTest implements LockTest {

    public void lock() {
        System.out.println("This is ZkLockTest lock()");
    }
}
