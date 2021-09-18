package com.nudge.core.glfwEvents;

import org.lwjgl.glfw.GLFWKeyCallback;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_UNKNOWN;

/**
 * @author Frederik Dahl
 * 17/08/2021
 */


public class KeyCallback extends GLFWKeyCallback implements GLFWCallbackNudge {
    
    private int key =       GLFW_KEY_UNKNOWN;
    private int scancode =  GLFW_KEY_UNKNOWN;
    private int action =    GLFW_KEY_UNKNOWN;
    private int mods =      GLFW_KEY_UNKNOWN;
    
    private boolean event;
    
    @Override
    public void invoke(long window, int key, int scancode, int action, int mods) {
        
        if (this.key != key && this.action != action && this.mods != mods) {
            
            this.key = key;
            this.scancode = scancode;
            this.action = action;
            this.mods = mods;
            
            event = true;
        }
    }
    
    public int getKey() { return key; }
    
    public int getScancode() { return scancode; }
    
    public int getAction() { return action; }
    
    public int getMods() { return mods; }
    
    @Override
    public boolean eventQueued() { return event; }
    
    @Override
    public void reset() { event = false; }
}
