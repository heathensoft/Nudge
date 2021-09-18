package com.nudge.core.glfwEvents;

import org.lwjgl.glfw.GLFWScrollCallback;

/**
 * @author Frederik Dahl
 * 17/08/2021
 */


public class ScrollCallback extends GLFWScrollCallback implements GLFWCallbackNudge {
    
    private double xoffset = 0.0d;
    private double yoffset = 0.0d;
    
    private boolean scrolled;
    
    @Override
    public void invoke(long window, double xoffset, double yoffset) {
        this.xoffset = xoffset;
        this.yoffset = yoffset;
        scrolled = true;
    }
    
    public double getXoffset() { return xoffset; }
    
    public double getYoffset() { return yoffset; }
    
    @Override
    public boolean eventQueued() { return scrolled; }
    
    @Override
    public void reset() { scrolled = false; }
}
