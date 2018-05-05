package buaazl.lock.zookeeper;

import buaazl.lock.DistributedLock;
import buaazl.lock.Lock;
import buaazl.lock.zookeeper.connect.ZkConnection;
import org.apache.zookeeper.KeeperException;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by Vincent on 2018/4/23.
 */
public class ZkDtLockTest {

    private static final Logger logger = LoggerFactory.getLogger(ZkDtLockTest.class.getName());

    @Test
    public void testConnection() throws IOException, InterruptedException, KeeperException {
        ZkConnection zkConnection = ZkConnectionBuilderTest.getZkConnection();
        System.out.println("查看根节点:ls / => " + zkConnection.getZooKeeper().getChildren("/", true));
    }

    @Test
    public void testLock() {
        logger.info("execute testLock...");
        Lock dtLock = new ZkDtLock(ZkConnectionBuilderTest.getZkConnection(), "/dtLock");
        dtLock.lock();
        logger.info("dtLock info: " + dtLock.lockInfo());
        assert dtLock.getLocked() == true;
        dtLock.unLock();
        assert dtLock.getLocked() == false;
    }
}
