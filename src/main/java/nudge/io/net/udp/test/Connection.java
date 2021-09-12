package nudge.io.net.udp.test;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Connection {

    private final int port;
    private final InetAddress addr;
    private final DatagramSocket socket;

    public Connection(DatagramSocket socket, InetAddress addr, int port) {
        this.addr = addr;
        this.port = port;
        this.socket = socket;
    }

    public void send(byte[] data) {
        DatagramPacket packet = new DatagramPacket(data, data.length, addr, port);

        try {
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public byte[] receive() {

        byte[] buffer = new byte[1024];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

        try {
            socket.receive(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return packet.getData();
    }

    public int getPort() {
        return this.port;
    }


    public InetAddress getAddress() {
        return this.addr;
    }

    public Runnable close() {

        return () -> {
            synchronized(socket) {
                socket.close();
            }
        };

    }

}
