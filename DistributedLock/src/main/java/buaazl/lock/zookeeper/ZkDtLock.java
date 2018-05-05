package buaazl.lock.zookeeper;

import buaazl.lock.DistributedLock;
import buaazl.lock.Lock;
import buaazl.lock.exception.ZkLockException;
import buaazl.lock.zookeeper.connect.ZkConnection;
import buaazl.lock.zookeeper.connect.ZkConstant;
import buaazl.lock.zookeeper.connect.ZkUtil;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * Created by Vincent on 2018/4/22.
 */
public class ZkDtLock extends DistributedLock{
    private static final Logger logger = LoggerFactory.getLogger(ZkDtLock.class);

    private String lockPath;

    private final ZkConnection zkConnection;

    //zk uses ACL to control the access rights to node(no inheritance relationship),schema:id:permission
    private List<ACL> aclList;

    private LockWatcher watcher;

    //Has got lock or not
    private volatile boolean isLocked;

    //The id of lock which is being required now(has not gotten, requiring...)
    private String currentLockId;

    private String currentLockedNodePath;

    public ZkDtLock(ZkConnection zkConnection, String lockPath) {
        this(zkConnection, ZooDefs.Ids.OPEN_ACL_UNSAFE, lockPath);
    }

    public ZkDtLock(ZkConnection zkConnection, List<ACL> aclList, String lockPath) {
        this.zkConnection = zkConnection;
        this.aclList = aclList;
        this.lockPath = lockPath;
    }

    private void init() throws IOException, InterruptedException, KeeperException {
        ZkUtil.ensurePathExist(zkConnection, aclList, lockPath);
        currentLockedNodePath = zkConnection.getZooKeeper().create(lockPath + ZkConstant.ZL_LOCK_MARKUP, null, aclList, CreateMode.EPHEMERAL_SEQUENTIAL);
        currentLockId = currentLockedNodePath.substring(currentLockedNodePath.lastIndexOf("/") + 1);
        logger.info("currentLockId: " + currentLockId);
        this.watcher = new LockWatcher();
    }

    public void lock() {
        if(isLocked) {
            throw new ZkLockException("Have gotten lock, please release lock first...");
        }

        try {
            init();
            watcher.canLockForCurrentId();
            if(!isLocked) {
                //This lock requirement failed
                throw new ZkLockException("Get lock failed...");
            }
        } catch (InterruptedException e) {
            abandonAttemptGetLock();
            throw new ZkLockException("InterruptedException: " + e.getMessage());
        } catch (KeeperException e) {
            abandonAttemptGetLock();
            throw new ZkLockException("KeeperException: " + e.getMessage());
        } catch (IOException e) {
            abandonAttemptGetLock();
            throw new ZkLockException("IOException: " + e.getMessage());
        }

    }

    public boolean tryLock() {

        return false;
    }

    public boolean tryLock(long millisTimeout) {
        if(isLocked) {
            throw new ZkLockException("Have not gotten lock, please get lock first...");
        }
        try {
            init();
            watcher.canLockForCurrentId();
            if(!isLocked) {
                //if not get lock, then wait a period of time and try to obtain again
                Thread.sleep(millisTimeout);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void unLock() {
        if(currentLockId == null) {
            throw new ZkLockException("Have not gotten lock, please get lock first...");
        }
        if(isLocked)
            cleanUp();
    }

    private void abandonAttemptGetLock() {
        cleanUp();
        isLocked = false;
    }

    private void cleanUp() {
        logger.info("Starting to clean up...");
        try {
            Stat stat = zkConnection.getZooKeeper().exists(currentLockedNodePath, false);
            if(stat != null) {
                zkConnection.getZooKeeper().delete(currentLockedNodePath, ZkConstant.ZK_ZNODE_DATA_ALLVERSION);
            } else {
                //Lock id not exist
                logger.warn("Carrying out clean up lock, but there is no znode lock to delete...");
            }
        } catch (Exception e) {
            throw new ZkLockException(e);
        }
        isLocked = false;
        currentLockId = null;
        currentLockedNodePath = null;
        logger.info("Lock clean up finished !");
    }

    public boolean getLocked() {
        return isLocked;
    }

    public String lockInfo() {
        return "LockPath = " + this.lockPath + ", isLocked = " + this.isLocked + ", currentLockId = " + this.currentLockId;
    }


    private class LockWatcher implements Watcher {
        /**
         * If there exists znode being deleted, then listen and check that can get lock or not
         * @param watchedEvent
         */
        public void process(WatchedEvent watchedEvent) {
            if(watchedEvent.getType() == Event.EventType.NodeDeleted) {
                canLockForCurrentId();
            }
        }

        private synchronized void canLockForCurrentId() {
            if(currentLockId == null || currentLockId.length() == 0) {
                throw new ZkLockException("Require lock exception,can not get lock id...");
            }

            try {
                //"null" means not setting watch
                List<String> waitLockIds = zkConnection.getZooKeeper().getChildren(lockPath, null);
                Collections.sort(waitLockIds);
                int currentLockIdIndex = waitLockIds.indexOf(currentLockId);
                logger.info("currentLockIdIndex: " + currentLockIdIndex);
                /**
                 * If current lock id is the first, then can get lock
                 * else, check the ahead znode exist or not; If not exist, means the ahead node has given up the lock
                 */
                if(currentLockIdIndex == 0) {
                    isLocked = true;
                } else {
                    String aheadLockId = waitLockIds.get(currentLockIdIndex - 1);
                    Stat stat = zkConnection.getZooKeeper().exists(lockPath + "/" + aheadLockId, this);
                    if(stat == null) {
                        //recheck currentLockId can get lock or not
                        canLockForCurrentId();
                    }
                }
            } catch (InterruptedException e) {
                abandonAttemptGetLock();
            } catch (KeeperException e) {
                abandonAttemptGetLock();
            } catch (IOException e) {
                abandonAttemptGetLock();
            }
        }
    }
}
