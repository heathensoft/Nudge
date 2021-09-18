package com.nudge.io.net.udp.test;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class UDPServer {

    private static final int EXC_TIMEOUT        = 3000;
    private static final int CORE_POOL_SIZE     = 8;
    private static final int MAX_POOL_SIZE      = 16;
    private static final int KEEP_ALIVE_TIME    = 3000;
    private static final int QUEUE_CAP          = 128;

    private final ExecutorService taskManager =

            new ThreadPoolExecutor(
                    CORE_POOL_SIZE,
                    MAX_POOL_SIZE,
                    KEEP_ALIVE_TIME,
                    TimeUnit.MILLISECONDS,
                    new ArrayBlockingQueue<>(QUEUE_CAP)
            );

    private final List<Session> sessions = new ArrayList<>();


    public UDPServer() {

    }

    public void startNew(Session session) throws SocketException, SecurityException{
        sessions.add(session);
        session.start(this);
    }

    protected synchronized ExecutorService taskManager() {
        return taskManager;
    }

    public void shutDown() {

    }
}
