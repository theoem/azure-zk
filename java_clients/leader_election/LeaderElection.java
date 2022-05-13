package org.application;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


/**
 * Adapted from https://www.allprogrammingtutorials.com/tutorials/leader-election-using-apache-zookeeper.php
 *
 */
public class LeaderElection {

    public static void main(String[] args) {
        if(args.length < 2) {
            System.err.println("Usage: LeaderElection client_id host:port");
            System.exit(2);
        }

        int id = Integer.parseInt(args[0]);
        String hostPort = args[1];

        new NodeWatcher(id, hostPort);


        final ExecutorService service = Executors.newSingleThreadExecutor();

        final Future<?> status = service.submit(new NodeWatcher(id, hostPort));

        try {
            status.get();
        } catch (InterruptedException | ExecutionException e) {
            System.out.println(e);
            service.shutdown();
        }
    }
}
