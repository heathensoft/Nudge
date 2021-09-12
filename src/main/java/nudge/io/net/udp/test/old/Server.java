package nudge.io.net.udp.test.old;

import io.serialization.transmission.TextMessage;
import io.serialization.transmission.old.DatabaseTransmission;
import io.serialization.transmission.old.PacketData;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class Server implements Runnable{

    private DatagramSocket socket;
    private int port;
    private boolean running = false;

    private Thread run;
    private Thread manage;
    private Thread receive;
    private Thread send;

    public Server(int port) {
        this.port = port;
        try {
            socket = new DatagramSocket(port);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        run = new Thread(this, "Run");
        run.start();
    }

    @Override
    public void run() {
        running = true;
        System.out.println("Server started on port: " + port);
        //manageClients();
        receive();
    }

    private void manageClients() {
        manage = new Thread("ManageClients") {
            @Override
            public void run() {
                while (running) { // volatile

                }
            }
        };
        manage.start();
    }

    private void receive() {
        receive = new Thread("Receive") {
            @Override
            public void run() {
                while (running) { // volatile
                    byte[] data = new byte[1024];
                    DatagramPacket packet = new DatagramPacket(data,data.length);
                    try {
                        socket.receive(packet);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (PacketData.validateHeader(data)) {

                        byte dataType = PacketData.dataType(data);

                        if (dataType != PacketData.TYPE_INVALID) {

                            if (dataType == PacketData.TYPE_TEXT_MESSAGE) {

                                TextMessage packetData = new TextMessage().recreate(data);

                                System.out.println(packetData.getString());
                            }
                            else if (dataType == PacketData.TYPE_DATABASE_TRANSMISSION) {
                                System.out.println("DataBase received");

                                DatabaseTransmission _databaseTransmission = new DatabaseTransmission().recreate(data);
                                System.out.println(_databaseTransmission.size());
                            }
                        }
                        else {
                            System.out.println("invalid datatype");
                        }
                    }
                    else {
                        System.out.println("invalid header");
                    }
                }
            }
        };
        receive.start();

    }
}
