package nudge.io.net.udp.test.old;



import java.net.SocketException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.concurrent.Future;

public class Test {

    public static Random RND = new Random();

    public static void testMethod(int i) throws RuntimeException {
        if (i == 0) throw new RuntimeException();
    }

    public static int rndPoisson(double mean) {
        double L = Math.exp(-mean);
        int k = 0;
        double p = 1.0;
        do {
            p = p * RND.nextDouble();
            k++;
        } while (p > L);
        return k - 1;
    }

    public static void main(String[] args) {

        int fg = 100%1000;

        fg = 2%3;



        int getCounter = 2;
        int g;
        for (int i = 0; i < 10; i++) {
            //int x = ++getCounter % 3;

            if ((i & 1) != 0) {
                System.out.println(i);
            }
            //System.out.println(rndPoisson(2) * 3);
            //getCounter = getCounter == 7 ? 0 : ++getCounter;
            //g = getCounter;

        }

        for (int i = 0; i < 100; i++) {
            //int x = ++getCounter % 3;

            //getCounter = getCounter == 7 ? 0 : ++getCounter;
            //g = getCounter;
            System.out.println((rndPoisson(1) + 1)*2);
        }



        LocalDateTime now = LocalDateTime.now();

        LocalDateTime afterOneMinute = now.plusMinutes(1);

        Duration duration = Duration.between(now,afterOneMinute);

        long delay = Math.abs(duration.toMillis());

        Future<Runnable> future;


        /*
        Session1 server = new Session1(3400,"name");
        try {
            server.start();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        server.shutDown();
        System.out.println(20);

        /*

        DatagramSocket socket;

        try {
            socket = new DatagramSocket(0);
            System.out.println(socket.getLocalPort());
            System.out.println(socket.getReceiveBufferSize());

        } catch (SocketException e) {
            e.printStackTrace();
        }

        try {
            testMethod(0);
        } catch (Exception e) {
            e.printStackTrace();
        }

        int part = 15;
        int parts = 16;

        byte data = PacketData.partitionData((byte) part,(byte) parts);


        System.out.println("part: " + PacketData.partitionID(data) +" / "+ PacketData.partitionSum(data));


        /*
        int port = 8192;
        Server server = new Server(port);
        server.start();
        InetAddress address = null;
        try {
            address = InetAddress.getByName("localhost");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        server.send(new byte[] {1,2,3},address,port);

         */
    }




}
