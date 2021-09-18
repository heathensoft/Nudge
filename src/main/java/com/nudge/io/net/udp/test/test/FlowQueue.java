package com.nudge.io.net.udp.test.test;

import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class FlowQueue {

    private static final byte CRI = 3;
    private static final byte IMP = 2;
    private static final byte DEF = 1;
    private static final byte LOW = 0;

    private static final int[] getOrder = {IMP,DEF,IMP,LOW,IMP,DEF,IMP};

    private final ArrayList<BlockingQueue<DatagramPacket>> queues;

    private volatile int size;
    private int getCounter;

    private AtomicInteger size2;
    private AtomicInteger getCounter2;



    public FlowQueue() {

        queues = new ArrayList<>(4);

        queues.add(LOW,new LinkedBlockingQueue<>());
        queues.add(DEF,new LinkedBlockingQueue<>());
        queues.add(IMP,new LinkedBlockingQueue<>());
        queues.add(CRI,new LinkedBlockingQueue<>());

        size = 0;
        getCounter = -1;
    }

    public DatagramPacket pickUp() {

        int get;

        synchronized (this) {

            if (!queues.get(CRI).isEmpty()) {
                size--;
                size2.decrementAndGet();
                return queues.get(CRI).poll();
            }
            //getCounter2.get()
            getCounter = getCounter == getOrder.length ? 0 : ++getCounter;
            get = getCounter;
        }

        BlockingQueue<DatagramPacket> queue = queues.get(getOrder[get]);

        if (queue.isEmpty()) {
            for (int i = 0; i < 2; i++) {
                queue = queues.get(getOrder[++get%3]);
                if (!queue.isEmpty()) {
                    size--;
                    break;
                }
            }
        }

        return queue.poll();
    }

    public DatagramPacket pickUp2() {

        //LinkedList
        int sizeMod = 0;
        DatagramPacket result;
        BlockingQueue<DatagramPacket> selectedQueue = queues.get(CRI);

        if (selectedQueue.isEmpty()) {


        }
        else {
            result = selectedQueue.poll();
            sizeMod--;
        }

        return null;
    }


    public int sizeOfCritical() {
        return queues.get(CRI).size();
    }

    public int sizeOfImportant() {
        return queues.get(IMP).size();
    }

    public int sizeOfDefault() {
        return queues.get(DEF).size();
    }

    public int sizeOfLow() {
        return queues.get(LOW).size();
    }

    public int size() {
        return size;
    }
}
