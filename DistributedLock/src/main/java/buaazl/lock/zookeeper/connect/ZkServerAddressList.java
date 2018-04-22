package buaazl.lock.zookeeper.connect;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vincent on 2018/4/22.
 */
public class ZkServerAddressList {
    private static final Logger logger = LoggerFactory.getLogger(ZkServerAddressList.class.getName());

    private List<ZkServerAddress> zkServerAddressList = new ArrayList<ZkServerAddress>();

    /**
     * Build zookeeper server address list
     * @param addresses The description of addresses is "host:port,host,host,host:port..."
     *                  if the address does not have port, use default port
     */
    public ZkServerAddressList(String addresses) {
        String[] allAddress = addresses.split(ZkConstant.COMMA);
        for(String address : allAddress) {
            String[] hostPort = address.split(ZkConstant.COLON);
            if(hostPort.length == 1) {
                zkServerAddressList.add(new ZkServerAddress(hostPort[0].trim()));
            } else {
                zkServerAddressList.add(new ZkServerAddress(hostPort[0].trim(), Integer.valueOf(hostPort[1].trim())));
            }
        }
        logger.info("Build ZkServerAddressList, total has " + allAddress.length + " server address");
    }

    public void addServerAddress(String address) {
        String[] hostPort = address.split(ZkConstant.COLON);
        if(hostPort.length == 1) {
            zkServerAddressList.add(new ZkServerAddress(hostPort[0].trim()));
        } else {
            zkServerAddressList.add(new ZkServerAddress(hostPort[0].trim(), Integer.valueOf(hostPort[1].trim())));
        }
    }

    private class ZkServerAddress {
        public String host;
        public int port;

        public ZkServerAddress(String host, int port) {
            this.host = host;
            this.port = port;
        }

        public ZkServerAddress(String host) {
            this(host, ZkConstant.ZK_SERVER_DEFAULT_PORT);
        }
    }
}
