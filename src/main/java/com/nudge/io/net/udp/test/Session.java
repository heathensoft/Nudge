package com.nudge.io.net.udp.test;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

public abstract class Session {

    private UDPServer monitor;
    private DatagramSocket socket;
    private int port;
    private int bufferSize;
    private final String name;
    private volatile boolean running;

    private static final int SO_TIMEOUT         = 2000;


    public Session(int port, String name) {
        this.port = validatePort(port);
        this.name = name;
    }

    protected void start(UDPServer monitor, int bufferSize) throws SocketException, SecurityException{
        this.bufferSize = validateBufferSize(bufferSize);
        this.socket = new DatagramSocket(port);
        this.port = socket.getLocalPort();
        this.socket.setSoTimeout(SO_TIMEOUT);
        this.monitor = monitor;
        this.monitor.taskManager().execute(receive());
    }

    protected void start(UDPServer server) throws SocketException, SecurityException{
        start(server,1024);
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
                        monitor.taskManager().execute(process(incoming));
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

    private Runnable process(DatagramPacket packet) {

        return () -> {

        };
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

    public String name() {
        return name;
    }

    public int port() {
        return port;
    }

    public boolean isRunning() {
        return running;
    }
}
