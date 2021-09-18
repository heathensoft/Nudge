package com.nudge.io.net.udp.test.test;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class Server {


    public static void main(String[] args) {

        Server server = new Server();
    }

    DatagramSocket socket;
    int port = 6666;

    public Server() {
        try {
            socket = new DatagramSocket(port);
        } catch (SocketException e) {
            e.printStackTrace(); }
        System.out.println("Server: started");
        listen();
    }


    private void send(InetAddress address, int port) {

        new Thread(() -> {
            System.out.println("Server: starts listening");
            byte[] stopData = "stop".getBytes();
            byte[] sendData;
            for (int i = 0; i < 1000; i++)
             {
                 if (i == 999) {
                     sendData = stopData;
                 }
                 else sendData = ("message no.: " + i).getBytes();
                 try {
                     Thread.sleep(1);
                 } catch (InterruptedException e) {
                     e.printStackTrace();
                 }
                 DatagramPacket packet = new DatagramPacket(sendData, sendData.length, address, port);
                try {
                    socket.send(packet);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("Server: Stopped");
            System.out.println("Stopping Thread");
            socket.close();
        }).start();
    }

    private void listen() {

        new Thread(() -> {
            while (true) {
                byte[] data = new byte[128];
                DatagramPacket packet = new DatagramPacket(data,data.length);
                try {
                    socket.receive(packet);
                    System.out.println("Server: client port "+ packet.getPort() );
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String message = new String(packet.getData()).trim();
                if (message.equals("stop")) {
                    System.out.println("Server: receives stop signal from client... running = false");
                    break;
                }
                else if (message.equals("start")){
                    System.out.println("Server: Received start signal from client");
                    send(packet.getAddress(),packet.getPort());
                }
                System.out.println(message);
            }
            System.out.println("Stopping Thread");
        }).start();
    }
}
