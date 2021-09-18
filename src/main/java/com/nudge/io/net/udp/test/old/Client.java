package com.nudge.io.net.udp.test.old;



import java.io.IOException;
import java.net.*;

public class Client {

    private Thread send;
    private String userName;
    private DatagramSocket socket;
    private InetAddress ip;
    private int port;

    public Client(String username, String address, int port) {
        this.userName = username;
        this.port = port;
        connect(address);

        String connectionMessage = username + " connected from " + address + ":" + port;

        //send(TextMessage.create(connectionMessage,StandardCharsets.UTF_8).getBytes());
    }

    private void connect(String address) {
        try {
            socket = new DatagramSocket();
            ip = InetAddress.getByName(address);
        } catch (UnknownHostException | SocketException e) {
            e.printStackTrace();
        }
    }

    private byte[] receive() {
        byte[] data = new byte[1024];
        DatagramPacket packet = new DatagramPacket(data,data.length);
        try {
            socket.receive(packet);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return packet.getData();
    }

    public void send(final byte[] data) {
        send = new Thread("Send") {
            @Override
            public void run() {
                DatagramPacket packet = new DatagramPacket(data, data.length, ip, port);
                try {
                    socket.send(packet);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        send.start();
    }
}
