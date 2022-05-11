package org.main;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.zookeeper.KeeperException;

import java.util.Arrays;
import java.util.List;

public class SingleSystemImage {

    public static void main(String[] args) {

        if (args.length < 5) {
            System.err
                    .println("USAGE: SingleSystemImage host1 " +
                            "host2 host3 port root");
            System.exit(2);
        }
        String host1 = args[0];
        String host2 = args[1];
        String host3 = args[2];
        String port = args[3];
        String root = args[4];

        // Setting up the ZooKeeper connection to the three members
        List<String> hosts = Arrays.asList(host1, host2, host3);
        List<ZkConnector> connectors = Arrays.asList(new ZkConnector(), new ZkConnector(), new ZkConnector());

        int index = 0;
        for (ZkConnector conn:connectors) {
            conn.connect(hosts.get(index) + ":" + port, root);
            index++;
        }


        System.out.println("Listing all children of path: " + root + " on host: " + hosts.get(0));
        try {
            System.out.println(connectors.get(0).getChildren(root));
            System.out.println("Sending create request to the host: " + hosts.get(1));
            connectors.get(1).create(root, 0, false);

            index = 0;
            for (ZkConnector conn: connectors) {
                System.out.println("Listing all children of path: " + root + " on host: " + hosts.get(index));
                System.out.println(conn.getChildren(root));
                index++;
            }
        } catch (KeeperException | InterruptedException e) {
            System.out.println(e);
        }

    }
}
