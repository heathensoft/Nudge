package nudge.io.net.udp.test.test;

import java.io.IOException;
import java.net.*;


public class Client {

    public static void main(String[] args) {

        Client client = new Client();
    }

    private InetAddress address;
    private DatagramSocket socket;
    int serverPort = 6666;

    public Client() {

        try {
            address = InetAddress.getByName("localHost");
            socket = new DatagramSocket();
            socket.connect(address,serverPort);
        } catch (SocketException | UnknownHostException e) {
            e.printStackTrace();
        }
        System.out.println(socket.isConnected());
        System.out.println("Client: Started");
        byte[] start = "start".getBytes();

        DatagramPacket packet = new DatagramPacket(start,start.length,address,serverPort);
        System.out.println("Client: serverPort "+ socket.getPort() );
        try {
            socket.send(packet);
            System.out.println("Client: port "+ socket.getPort() );
            System.out.println("Client: Start sent from client");
        } catch (IOException e) {
            e.printStackTrace();
        }
        listen();
    }

    private void listen() {

        new Thread(() -> {
            while (true) {
                byte[] data = new byte[128];
                DatagramPacket packet = new DatagramPacket(data,data.length);
                try {
                    socket.receive(packet);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String message = new String(packet.getData()).trim();
                System.out.println(message);
                if (message.equals("stop")) {
                    System.out.println("Client: receives Stop... running = false");
                    try {
                        socket.send(packet);
                        System.out.println("Client: Stop sent from client");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
            socket.close();
            System.out.println("Client: Stopped");
            System.out.println("Stopping Thread");
        }).start();
    }

}
