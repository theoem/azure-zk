package org.application;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.List;

/**
 * Adapted from https://www.allprogrammingtutorials.com/tutorials/leader-election-using-apache-zookeeper.php
 *
 */

public class ZkConnector {

    ZooKeeper zk;

    public ZkConnector(String hostPort, NodeWatcher nodeWatcher) throws IOException {
        zk = new ZooKeeper(hostPort, 3000, nodeWatcher);
    }

    public String create(String znode, boolean watch, boolean eph) {
        String path = null;
        Stat stat = null;
        CreateMode mode;

        if (eph) {
            mode = CreateMode.EPHEMERAL_SEQUENTIAL;
        }
        else {
            mode = CreateMode.PERSISTENT;
        }
        try {
            stat =  zk.exists(znode, watch);
            if (stat == null) {
                path = zk.create(znode, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, mode);
            }
            else {
                path = znode;
            }

        } catch (KeeperException | InterruptedException e) {
            System.out.println(e);
        }

        return path;
    }

    public List<String> getChildren(String root, boolean watch){
        List<String> childNodes = null;

        try {
            childNodes = zk.getChildren(root, watch);
        } catch (InterruptedException | KeeperException e) {
            System.out.println(e);
        }
        return childNodes;
    }

    public boolean checkNode(final String node, final boolean watch) {

        boolean watched = false;
        try {
            final Stat nodeStat =  zk.exists(node, watch);

            if(nodeStat != null) {
                watched = true;
            }

        } catch (KeeperException | InterruptedException e) {
            throw new IllegalStateException(e);
        }

        return watched;
    }

}
