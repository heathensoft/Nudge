package com.nudge.core.glfwEvents;

import org.lwjgl.glfw.GLFWMouseButtonCallback;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_UNKNOWN;

/**
 * @author Frederik Dahl
 * 17/08/2021
 */


public class MouseButtonCallback extends GLFWMouseButtonCallback implements GLFWCallbackNudge {
    
    private int button =    GLFW_KEY_UNKNOWN;
    private int action =    GLFW_KEY_UNKNOWN;
    private int mods =      GLFW_KEY_UNKNOWN;
    
    private boolean event;
    
    
    @Override
    public void invoke(long window, int button, int action, int mods) {
        
        this.button = button;
        this.action = action;
        this.mods = mods;
        
        event = true;
    }
    
    public int getButton() { return button; }
    
    public int getAction() { return action; }
    
    public int getMods() { return mods; }
    
    @Override
    public boolean eventQueued() { return event; }
    
    @Override
    public void reset() { event = false; }
}
