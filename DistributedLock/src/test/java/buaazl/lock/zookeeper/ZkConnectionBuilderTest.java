package buaazl.lock.zookeeper;

import buaazl.lock.zookeeper.connect.ZkConnection;
import buaazl.lock.zookeeper.connect.ZkServerAddressList;
import org.junit.Test;

/**
 * Created by Vincent on 2018/4/23.
 */
public class ZkConnectionBuilderTest {

    public static ZkConnection getZkConnection() {
        ZkServerAddressList zkServerAddressList = new ZkServerAddressList("127.0.0.1:2181");
        ZkConnection zkConnection = new ZkConnection(zkServerAddressList, 1000*3600);
        return zkConnection;
    }
}
