package com.nudge.core.glfwEvents;

import org.lwjgl.glfw.GLFWWindowSizeCallback;

/**
 * @author Frederik Dahl
 * 17/08/2021
 */

public class WindowSizeCallback extends GLFWWindowSizeCallback implements GLFWCallbackNudge {
    
    private int width = 0;
    private int height = 0;
    private boolean event;
    
    
    @Override
    public void invoke(long window, int width, int height) {
        
        if (this.width != width || this.height != height) {
            
            this.width = width;
            this.height = height;
            
            event = true;
        }
    }
    
    public int getWidth() { return width; }
    
    public int getHeight() { return height; }
    
    @Override
    public boolean eventQueued() { return event; }
    
    @Override
    public void reset() { event = false; }
}
