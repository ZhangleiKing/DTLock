package buaazl.lock.zookeeper.connect;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.data.ACL;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Vincent on 2018/4/22.
 */
public class ZkUtil {

    /**
     * Ensure the path existing, if not, then create this path
     * @param zkConnection
     * @param aclList
     * @param path
     */
    public static void ensurePathExist(ZkConnection zkConnection, List<ACL> aclList, String path) throws IOException, InterruptedException, KeeperException {
        int pathLen = path.length();
        if(path.lastIndexOf('/') == pathLen-1) {
            path = path.substring(0, pathLen-1);
        }
        //If not exist, then create
        if(zkConnection.getZooKeeper().exists(path, false) == null) {
            zkConnection.getZooKeeper().create(path, null, aclList, CreateMode.PERSISTENT);
        }
    }

    /**
     * Filter the znode without lock, and sort all the lock id
     * eg: /path/buaazl.lock_1, /path/buaazl.lock_2, and the waitedIds contains <1,2>
     * @param currentIds
     * @return
     */
    public static List<String> getAllSortedLockedIds(List<String> currentIds) {
        List<String> waitedIds = new ArrayList<String>(currentIds.size());
        for(String lockId : currentIds) {
            int idx = lockId.indexOf(ZkConstant.ZL_LOCK_MARKUP.substring(1));
            if(idx >= 0) {
                waitedIds.add(lockId.substring(idx + ZkConstant.ZL_LOCK_MARKUP.substring(1).length()));
            }
        }
        Collections.sort(waitedIds);
        return waitedIds;
    }
}
