package com.nudge.core.glfwEvents;

import org.lwjgl.glfw.GLFWMonitorCallback;

/**
 * @author Frederik Dahl
 * 17/08/2021
 */


public class MonitorCallback extends GLFWMonitorCallback implements GLFWCallbackNudge {
    
    
    private long monitor;
    private int event;
    
    private boolean connectionEvent;
    
    @Override
    public void invoke(long monitor, int event) {
        
        this.monitor = monitor;
        this.event = event;
        
        connectionEvent = true;
    }
    
    public long getMonitor() { return monitor; }
    
    public int getEvent() { return event; }
    
    @Override
    public boolean eventQueued() { return connectionEvent; }
    
    @Override
    public void reset() { connectionEvent = false; }
    
}
