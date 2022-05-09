package org.main;

import org.apache.zookeeper.KeeperException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class StressTest {

    private static class Client implements Runnable {
        private String id;
        private Thread t;
        private String root;
        private String hostPort;
        private int count;
        private double ratioWrite;
        private ZkConnector conn;


        Client(String hostPort, String root, int count, double ratioWrite, int id) {
            this.root = root;
            this.hostPort = hostPort;
            this.count = count;
            this.ratioWrite = ratioWrite;
            this.id = String.valueOf(id);
            System.out.println("Creating " + id);
        }

        @Override
        public void run() {
            System.out.println("Running " + id);

            conn = new ZkConnector();
            test();

            try{
                conn.close();
            } catch (InterruptedException e) {
                System.out.println(e);
            }

            System.out.println("Thread " + id + " exiting.");
        }

        public void test() {
            List<Character> operations = operationsList(count, ratioWrite);
            conn.connect(hostPort, root);

            for (char o: operations) {
                try{
                    if (o == 'w') {
                        conn.create(root, 0, false);
                    }
                    else {
                        //conn.getChildren(root);
                        conn.read(root);
                    }
                } catch (KeeperException | InterruptedException e){
                    System.out.println(e);
                }
            }
        }
        public static List<Character> operationsList(int count, double ratioWrite) {
            List<Character> operations = new ArrayList<>();
            int countWrite = (int) (count * ratioWrite);

            for (int i = 0; i < count; i++) {
                operations.add('r');
            }

            for (int i = 0; i < countWrite; i++) {
                operations.set(i, 'w');
            }
            Collections.shuffle(operations);

            return operations;
        }
        public void start () {
            System.out.println("Starting " +  id );
            if (t == null) {
                t = new Thread (this, id);
                t.start ();
            }
        }
    }

    public static void main(String[] args) {

        if (args.length < 5) {
            System.err
                    .println("USAGE: StressTest hostPort root countOperation" +
                            "ratioWrite clientCount");
            System.exit(2);
        }

        String hostPort = args[0];
        String root = args[1];
        int count = Integer.parseInt(args[2]);
        double ratioWrite = Double.parseDouble(args[3]);
        int clientCount = Integer.parseInt(args[4]);

        int batchNum = 10;
        int batchSize = clientCount / batchNum;


        for (int i = 0; i < batchNum; i++) {
            for (int j = 0; j < batchSize; i++) {
                Client client = new Client(hostPort, root, count, ratioWrite, j);
                client.start();
            }
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (Exception e) {
                System.out.println(e);
            }
        }


    }
}
