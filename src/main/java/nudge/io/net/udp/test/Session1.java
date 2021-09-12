package nudge.io.net.udp.test;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Session1 {


    private int port                            = 0;
    private DatagramSocket socket;
    private UDPServer server;


    private volatile boolean running            = false;

    public static final int BUFFER_SIZE         = 1024;

    private static final int EXC_TIMEOUT        = 3000;
    private static final int SO_TIMEOUT         = 2000;
    private static final int CORE_POOL_SIZE     = 8;
    private static final int MAX_POOL_SIZE      = 16;
    private static final int KEEP_ALIVE_TIME    = 3000;
    private static final int QUEUE_CAP          = 96;

    private final String name;

    private final ExecutorService executor =

            new ThreadPoolExecutor(
                    CORE_POOL_SIZE,
                    MAX_POOL_SIZE,
                    KEEP_ALIVE_TIME,
                    TimeUnit.MILLISECONDS,
                    new ArrayBlockingQueue<>(QUEUE_CAP)
            );

    public Session1(int port, String name) {
        this.name = name;
        if (port < 0 || port > 0xFFFF) return;
        this.port = port;
    }

    public void start() throws SocketException, SecurityException{
            socket = new DatagramSocket(port);
            port = socket.getLocalPort();
            socket.setSoTimeout(SO_TIMEOUT);
            running = true;
            executor().execute(receive());
    }

    private synchronized ExecutorService executor() {
        return executor;
    }

    private Runnable receive() {
        return new Runnable() {
            @Override
            public void run() {
                boolean timedOut = false;
                DatagramPacket incoming = null;
                while (isRunning()) {
                    try {
                        if (!timedOut) incoming = createBuffer();
                            socket.receive(incoming);
                            timedOut = false;

                            executor().execute(process(incoming));
                    } catch (SocketTimeoutException e) {
                        timedOut = true;
                    } catch (IOException e) {
                        timedOut = false;
                        e.printStackTrace();
                    }
                }
            }
            private DatagramPacket createBuffer() {
                return new DatagramPacket(
                        new byte[BUFFER_SIZE],
                        BUFFER_SIZE);
            }
        };
    }

    private synchronized Runnable process(DatagramPacket packet) {

        return () -> {

        };
    }

    public void shutDown() {
        if (isRunning()) {
            new Thread(() -> {

                // send disconnect messages

                running = false;
                executor().shutdown();
                try {
                    if (!executor().awaitTermination(
                            EXC_TIMEOUT, TimeUnit.MILLISECONDS)) {
                        executor().shutdownNow();
                    }
                } catch (InterruptedException e) {
                    executor().shutdownNow(); }
                socket.close();
            }).start();
        }
    }

    public void shutDown2() {
        if (isRunning()) {
            new Thread(() -> {

                // send disconnect messages

                running = false;
                executor().shutdown();

                try {
                    if (!executor().awaitTermination(
                            EXC_TIMEOUT, TimeUnit.MILLISECONDS)) {
                        executor().shutdownNow();
                        if (!executor().awaitTermination(
                                60, TimeUnit.SECONDS)) {
                            // log.("Could not Terminate")
                        }
                    }
                } catch (InterruptedException e) {
                    executor().shutdownNow();
                    Thread.currentThread().interrupt();
                }
                socket.close();
            }).start();
        }
    }

    public boolean isRunning() {
        return running;
    }

    public String getName() {
        return name;
    }

    public int port() {
        return port;
    }
}
