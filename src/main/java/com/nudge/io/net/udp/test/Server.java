package com.nudge.io.net.udp.test;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class Server {

    private final int port;
    private DatagramSocket socket;

    private Thread listenThread;
    private boolean listening = false;

    private static final int MAX_PACKET_SIZE = 1024;
    byte[] receiveDataBuffer = new byte[MAX_PACKET_SIZE * 10];

    public Server(int port) {
        this.port = port;
    }

    public void start() {
        try {
            socket = new DatagramSocket(port);
        } catch (SocketException e) {
            e.printStackTrace();
            return;
        }
        listening = true;
        listenThread = new Thread(this::listen);
        listenThread.setName("Server thread: listen");
        listenThread.start();
    }

    private void listen() {
        while (listening) {
            DatagramPacket packet = new DatagramPacket(receiveDataBuffer, MAX_PACKET_SIZE);
            try {
                socket.receive(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
            process(packet);
        }
    }

    private void process(DatagramPacket packet) {

        InetAddress sendersAddress = packet.getAddress();
        int sendersPort = packet.getPort();

    }

    /*
    private <T extends Response<T>> void respond(T response, InetAddress address, int port ) {

    }
    
     */

    public void send(byte[] data, InetAddress address, int port) {
        DatagramPacket packet = new DatagramPacket(data,data.length,address,port);
        try {
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getPort() {
        return port;
    }
}
