package org.application;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * Adapted from https://www.allprogrammingtutorials.com/tutorials/leader-election-using-apache-zookeeper.php
 *
 */

public class NodeWatcher implements Watcher, Runnable{
    int id;
    String leaderPath = "/ELECTION";
    String prefix = "/guid-n_";
    String znodePath;
    String watchPath;
    ZkConnector conn;

    public NodeWatcher(int id, String hostPort) {
        this.id = id;
        try {
            conn = new ZkConnector(hostPort, this);
        } catch (IOException e) {
            System.out.println(e);
        }

    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        System.out.println("Event received from client ID " + id + ": " + watchedEvent);

        final Event.EventType eventType = watchedEvent.getType();
        if(Event.EventType.NodeDeleted.equals(eventType)) {
            if(watchedEvent.getPath().equalsIgnoreCase(watchPath)) {
                attemptForLeaderPosition();
            }
        }
    }

    private void attemptForLeaderPosition() {

        final List<String> childNodePaths = conn.getChildren(leaderPath, false);

        Collections.sort(childNodePaths);

        int index = childNodePaths.indexOf(znodePath.substring(znodePath.lastIndexOf('/') + 1));
        if(index == 0) {
            System.out.println("[Process: " + id + "] I am the new leader!");
        } else {
            final String watchedNodeShortPath = childNodePaths.get(index - 1);

            watchPath = leaderPath + "/" + watchedNodeShortPath;

            System.out.println("[Process: " + id + "] - Setting watch on node with path: " + watchPath);
            conn.checkNode(watchPath, true);
        }
    }


    @Override
    public void run() {
        System.out.println("Process with id: " + id + " has started!");

        final String rootNodePath = conn.create(leaderPath, false, false);
        if(rootNodePath == null) {
            throw new IllegalStateException("Unable to create/access leader election root node with path: " + leaderPath);
        }

        znodePath = conn.create(rootNodePath + prefix, false, true);

        System.out.println("[Process: " + id + "] Process node created with path: " + znodePath);

        attemptForLeaderPosition();
    }
}
