package nudge.io.net.udp.test;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.concurrent.*;

public class UDPServer2 {

    private DatagramSocket socket;
    private int port;
    private int bufferSize;
    private volatile boolean running            = false;
    private volatile boolean processing         = false;

    private static final int SO_TIMEOUT         = 2000;
    private static final int EXC_TIMEOUT        = 3000;
    private static final int CORE_POOL_SIZE     = 8;
    private static final int MAX_POOL_SIZE      = 16;
    private static final int KEEP_ALIVE_TIME    = 3000;
    private static final int MAX_TASKS          = 128;

    private final ExecutorService taskManager =

            new ThreadPoolExecutor(
                    CORE_POOL_SIZE,
                    MAX_POOL_SIZE,
                    KEEP_ALIVE_TIME,
                    TimeUnit.MILLISECONDS,
                    new ArrayBlockingQueue<>(MAX_TASKS)
            );


    private final BlockingQueue<DatagramPacket> toProcess = new LinkedBlockingQueue<>();

    public UDPServer2(int port) {
        // 0 might not be equivalent to wildcard
        this.port = validatePort(port);
    }

    public void start(int bufferSize) throws SocketException, SecurityException{
        this.bufferSize = validateBufferSize(bufferSize);
        this.socket = new DatagramSocket(port);
        this.port = socket.getLocalPort();
        this.socket.setSoTimeout(SO_TIMEOUT);
    }

    public void start() throws SocketException, SecurityException{
        start(1024);
    }

    private Runnable receive() {

        return new Runnable() {

            @Override
            public void run() {
                boolean timedOut = false;
                DatagramPacket incoming = null;
                while (isRunning()) {
                    try {
                        if (!timedOut)
                            incoming = createBuffer();

                        socket.receive(incoming);

                        toProcess.offer(incoming);

                        timedOut = false;

                        if (!isProcessing())
                            taskManager().execute(process());

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
                        new byte[bufferSize],
                        bufferSize);
            }
        };
    }

    private synchronized Runnable process() {

        return () -> {

        };
    }

    public void shutDown() {
        if (isRunning()) {
            new Thread(() -> {

                // send disconnect messages

                running = false;
                taskManager().shutdown();
                try {
                    if (!taskManager().awaitTermination(
                            EXC_TIMEOUT, TimeUnit.MILLISECONDS)) {
                        taskManager().shutdownNow();
                        if (!taskManager().awaitTermination(
                                60, TimeUnit.SECONDS)) {
                            System.out.println("Could not terminate");
                            // log.("Could not Terminate")
                        }
                    }
                } catch (InterruptedException e) {
                    taskManager().shutdownNow();
                    Thread.currentThread().interrupt();
                }
                socket.close();
            }).start();
        }
    }

    private synchronized ExecutorService taskManager() {
        return taskManager;
    }

    private int validateBufferSize(int size) {
        return Math.max(8*Byte.SIZE, Math.min(size,64_000*Byte.SIZE));
    }

    private int validatePort(int port) {
        return (port < 0 || port > 0xFFFF) ? 0 : port;
    }

    public int bufferSize() {
        return bufferSize;
    }

    public boolean isRunning() {
        return running;
    }

    public boolean isProcessing() {
        return processing;
    }

    public int port() {
        return port;
    }

}
