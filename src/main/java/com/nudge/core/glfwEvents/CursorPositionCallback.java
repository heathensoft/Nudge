package com.nudge.core.glfwEvents;

import org.lwjgl.glfw.GLFWCursorPosCallback;

/**
 * @author Frederik Dahl
 * 17/08/2021
 */


public class CursorPositionCallback extends GLFWCursorPosCallback implements GLFWCallbackNudge {
    
    private double x = 0.0d;
    private double y = 0.0d;
    private double lastX = 0.0d;
    private double lastY = 0.0d;
    
    private boolean moved;
    
    @Override
    public void invoke(long window, double xpos, double ypos) {
        // could use epsilon here
        if (x != xpos && y != ypos) {
            lastX = x;
            lastY = y;
            x = xpos;
            y = ypos;
            moved = true;
        }
    }
    
    public double getX() { return x; }
    
    public double getY() { return y; }
    
    public double getLastX() { return lastX; }
    
    public double getLastY() { return lastY; }
    
    @Override
    public boolean eventQueued() { return moved; }
    
    @Override
    public void reset() { moved = false; }
}
