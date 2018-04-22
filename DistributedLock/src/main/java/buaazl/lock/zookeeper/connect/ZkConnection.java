package buaazl.lock.zookeeper.connect;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

/**
 * Created by Vincent on 2018/4/22.
 */
public class ZkConnection {
    private static final Logger logger = LoggerFactory.getLogger(ZkConnection.class.getName());

    private ZooKeeper zooKeeper;

    private ZkSessionState zkSessionState;

    private ZkServerAddressList zkServerAddressList;

    private int sessionTimeout;

    //Makes hashSet thread-safe
    private final Set<Watcher> watchers = Collections.synchronizedSet(new HashSet<Watcher>());

    public ZkConnection(ZkServerAddressList zkServerAddressList, int sessionTimeout) {
        this.zkServerAddressList = zkServerAddressList;
        this.sessionTimeout = sessionTimeout;
    }

    public synchronized ZooKeeper getZooKeeper() throws IOException, InterruptedException {
        if(zooKeeper != null)
            return zooKeeper;

        final CountDownLatch ensureConnected = new CountDownLatch(1);
        Watcher watcher = new Watcher() {
            public void process(WatchedEvent watchedEvent) {
                switch (watchedEvent.getType()) {
                    case None:
                        switch (watchedEvent.getState()) {
                            case Expired:
                                closeConnection();
                                break;
                            case SyncConnected:
                                //After the connection was created between client and server, it will receive this notice
                                //KeeperState.SyncConnected(3)  EventType.None(1)
                                ensureConnected.countDown();
                                break;
                        }
                }
                synchronized (watchers) {
                    for(Watcher eachWatcher : watchers) {
                        eachWatcher.process(watchedEvent);
                    }
                }
            }
        };
        if(zkSessionState != null) {
            zooKeeper = new ZooKeeper(zkServerAddressList.toString(), sessionTimeout, watcher, zkSessionState.getSessionId(), zkSessionState.getSessionPwd());
        } else {
            zooKeeper = new ZooKeeper(zkServerAddressList.toString(), sessionTimeout, watcher);
        }
        zkSessionState = new ZkSessionState(zooKeeper.getSessionId(), zooKeeper.getSessionPasswd());
        ensureConnected.await();
        return zooKeeper;
    }

    public synchronized void closeConnection() {
        if(zooKeeper != null) {
            try {
                zooKeeper.close();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.error("Close connection to zookeeper cause exception: " + e.getMessage());
            } finally {
                zooKeeper = null;
                zkSessionState = null;
            }
        }
    }
}
