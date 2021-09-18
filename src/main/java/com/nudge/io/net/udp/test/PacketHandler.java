package com.nudge.io.net.udp.test;

import java.net.DatagramPacket;

public abstract class PacketHandler {

    public abstract void process(DatagramPacket packet);
}
