package com.nudge.core.view;

import com.nudge.core.CORE;
import com.nudge.core.settings.Config;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;
import java.util.ArrayList;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.system.MemoryStack.stackPush;

/**
 * @author Frederik Dahl
 * 04/08/2021
 */


public class Window {
    
    // Will eventually support "callback-multithreading".
    // I am using temporary exception-handling to see things explicitly.
    
    private long contextThreadID;
    
    private long window;
    private long monitor;
    
    private int windowWidth;
    private int windowHeight;
    private int windowWidthBeforeFullScreen;
    private int windowHeightBeforeFullScreen;
    private int fullScreenResolutionWidth;
    private int fullScreenResolutionHeight;
    private int monitorRefreshRate;
    private int frameBufferWidth;
    private int frameBufferHeight;
    private int viewportWidth;
    private int viewportHeight;
    private int viewportX0;
    private int viewportY0;
    
    
    private float aspectRatio;
    private float viewportWidthInv;
    private float viewportHeightInv;
    
    private boolean vsync;
    private boolean minimized;
    private boolean fullScreen;
    private boolean initialized;
    private boolean contextCurrent;
    
    
    
    public Window(Config config) {
    
        if (config == null) throw new IllegalStateException("Config cannot be null");
        
        
    
    }
    
    private void setWindowCallbacks() {
    
    }
    
    private void queryFrameBufferSize() {
    
        if (notMainThread())
            throw new IllegalStateException("Method must be called from main-thread.");
        
        try ( MemoryStack stack = stackPush() ) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);
            glfwGetFramebufferSize(window,pWidth,pHeight);
            frameBufferWidth = pWidth.get();
            frameBufferHeight = pHeight.get();
        }
    }
    
    private void queryWindowSize() {
        
        if (notMainThread())
            throw new IllegalStateException("Method must be called from main-thread.");
        
        try ( MemoryStack stack = stackPush() ) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);
            glfwGetWindowSize(window, pWidth, pHeight);
            windowWidth = pWidth.get();
            windowHeight = pHeight.get();
        }
    }
    
    private void updateViewport(int fbWidth, int fbHeight) {
        
        if (!initialized)
            throw new IllegalStateException("Window not initialized");
        
        int aspectWidth = fbWidth;
        int aspectHeight = (int)((float)aspectWidth / aspectRatio);
        if (aspectHeight > fbHeight) {
            aspectHeight = fbHeight;
            aspectWidth = (int)((float)aspectHeight * aspectRatio);
        }
        int viewPortX = (int) (((float)fbWidth / 2f) - ((float)aspectWidth / 2f));
        int viewPortY = (int) (((float)fbHeight / 2f) - ((float)aspectHeight / 2f));
        
        this.viewportWidth = aspectWidth;
        this.viewportHeight = aspectHeight;
        this.viewportWidthInv = 1f / aspectWidth;
        this.viewportHeightInv = 1f / aspectHeight;
        this.viewportX0 = viewPortX;
        this.viewportY0 = viewPortY;
        
        
        glViewport(viewPortX,viewPortY,aspectWidth,aspectHeight);
    }
    
    private boolean resolutionSupportedByMonitor(int resWidth, int resHeight) {
        
        ArrayList<VideoMode> videoModes = getVideoModes();
        
        for (VideoMode mode : videoModes) {
            int width = mode.getWidth();
            int height = mode.getHeight();
            if (width == resWidth && height == resHeight)
                return true;
        }
        return false;
    }
    
    private ArrayList<VideoMode> getVideoModes() {
        
        ArrayList<VideoMode> videoModes = new ArrayList<>();
        GLFWVidMode.Buffer modes = glfwGetVideoModes(monitor);
        if (modes != null) {
            for (int i = 0; i < modes.capacity(); i++) {
                modes.position(i);
                int width = modes.width();
                int height = modes.height();
                int redBits = modes.redBits();
                int greenBits = modes.greenBits();
                int blueBits = modes.blueBits();
                int refreshRate = modes.refreshRate();
                videoModes.add(new VideoMode(width, height, redBits, greenBits, blueBits, refreshRate));
            }
        }
        return videoModes;
    }
    
    private boolean notMainThread() {
        
        return CORE.isMainThread(Thread.currentThread().getId());
    }
    
    private boolean notContextThread() {
        
        if (!contextCurrent)
            throw new IllegalStateException("Context not current (Window not initialized)");
        return contextThreadID != Thread.currentThread().getId();
    }
    
    
    // this function also updates the aspectRatio (FullScreen only)
    public void setResolution(int width, int height) {
        
        if (fullScreen) {
    
            glfwSetWindowSize(window,width,height);
    
            queryFrameBufferSize(); // might not need this. It can get set in callback
    
            int newAspectRatio = frameBufferWidth / frameBufferHeight;
    
            if (aspectRatio != newAspectRatio) {
        
                aspectRatio = newAspectRatio;
        
                updateViewport(frameBufferWidth,frameBufferHeight);
            }
        
        }
        
        if (width != windowWidth && height != windowHeight) { // wrong.
            
            glfwSetWindowSize(window,width,height);
            
            queryFrameBufferSize(); // might not need this. It can get set in callback
            
            int newAspectRatio = frameBufferWidth / frameBufferHeight;
            
            if (aspectRatio != newAspectRatio) {
                
                aspectRatio = newAspectRatio;
                
                updateViewport(frameBufferWidth,frameBufferHeight);
            }
            
        }
        
        
    }
    
    
    public void makeContextCurrent() { // any thread
        
        if (!contextCurrent) {
            glfwMakeContextCurrent(window);
            contextThreadID = Thread.currentThread().getId();
            contextCurrent = true;
        }
    }
    
    public void createCapabilities() {
        
        if (!initialized) {
            if (!contextCurrent)
                throw new IllegalStateException("Context not current (use makeContextCurrent())");
            GL.createCapabilities();
            initialized = true;
        }
    }
    
    public void show() {
        
        if (notMainThread())
            throw new IllegalStateException("Method must be called from main-thread.");
        
        glfwShowWindow(window);
    }
    
    public void toggleVsync(boolean on) {   // How would this affect the application timers?
        
        if (notContextThread())
            throw new IllegalStateException("Method must be called from context-current thread");
        glfwSwapInterval(on ? 1 : 0 );
        vsync = on;
    }
    
}
