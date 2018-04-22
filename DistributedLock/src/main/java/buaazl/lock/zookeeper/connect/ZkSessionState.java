package buaazl.lock.zookeeper.connect;

/**
 * Created by Vincent on 2018/4/22.
 */
public class ZkSessionState {
    private final long sessionId;

    private final byte[] sessionPwd;

    public ZkSessionState(long sessionId, byte[] sessionPwd) {
        this.sessionId = sessionId;
        this.sessionPwd = sessionPwd;
    }

    public long getSessionId() {
        return this.sessionId;
    }

    public byte[] getSessionPwd() {
        return this.sessionPwd;
    }
}
