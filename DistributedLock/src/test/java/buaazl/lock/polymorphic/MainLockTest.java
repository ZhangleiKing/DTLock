package buaazl.lock.polymorphic;

import org.junit.Test;

/**
 * Created by Vincent on 2018/4/22.
 */
public class MainLockTest {
    private LockTest lock;

    @Test
    public void testLockPoly() {
        String type = "zookeeper";
        if(type.equals("zookeeper")) {
            lock = new ZkLockTest();
        }else if (type.equals("redis")) {
            lock = new RedisLockTest();
        }
        lock.lock();
    }
}
