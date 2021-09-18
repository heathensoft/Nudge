package com.nudge.core.glfwEvents;

import org.lwjgl.glfw.GLFWWindowIconifyCallback;

/**
 * @author Frederik Dahl
 * 17/08/2021
 */


public class WindowIconifyCallback extends GLFWWindowIconifyCallback implements GLFWCallbackNudge {
    
    private boolean minimized;
    private boolean event;
    
    @Override
    public void invoke(long window, boolean iconified) {
        
        if (minimized != iconified) {
            minimized = iconified;
            event = true;
        }
    }
    
    public boolean iconified() { return minimized; }
    
    @Override
    public boolean eventQueued() { return event; }
    
    @Override
    public void reset() { event = false; }
    
    
}
