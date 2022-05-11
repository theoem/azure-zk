package org.main;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

public class ZkConnector implements Watcher{
    static ZooKeeper zk;
    int requests = 0;

    public ZooKeeper connect (String hostPort, String root) {
        try {
            System.out.println("Starting ZK:");
            zk = new ZooKeeper(hostPort, 3000, this);
            System.out.println("Finished starting ZK: " + zk);
        } catch (IOException e) {
            System.out.println(e);
            zk = null;
        }

        // Create ZK root znode
        if (zk != null) {
            try {
                Stat s = zk.exists(root, false);
                if (s == null) {
                    zk.create(root, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE,
                            CreateMode.PERSISTENT);
                }
            } catch (KeeperException e) {
                System.out.println("Keeper exception when instantiating ZooKeeper: " + e);
            } catch (InterruptedException e) {
                System.out.println("Interrupted exception");
            }
        }
        return zk;
    }

    public boolean create(String root, int valueInt, boolean eph)
            throws KeeperException, InterruptedException {

        CreateMode mode;
        byte[] value = ByteBuffer.allocate(4).putInt(valueInt).array();

        if (eph) {
            mode = CreateMode.EPHEMERAL_SEQUENTIAL;
        }
        else {
            mode = CreateMode.PERSISTENT_SEQUENTIAL;
        }

        zk.create(root + "/element", value, ZooDefs.Ids.OPEN_ACL_UNSAFE, mode);
        return true;
    }

    public List<String> getChildren(String root) throws KeeperException, InterruptedException{

        List<String> childNodes = null;
        childNodes = zk.getChildren(root, this);
        return childNodes;
    }

    public byte[] read(String root) throws KeeperException, InterruptedException{
        return zk.getData(root, false, null);
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
    }
    public void close() throws InterruptedException {
        zk.close();
    }
}
