package nudge.core.coreTest;

import nudge.core.view.VideoMode;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.Callback;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Objects;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.glfwSetWindowMonitor;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * @author Frederik Dahl
 * 17/08/2021
 */


public class Window implements IWindow {
    
    // todo: Create a god-damn logging system
    // todo: Eventually having separate threads for glfw event-polling
    
    private long contextThreadID;
    
    private long window;
    private long monitor;
    
    private int windowWidth;
    private int windowHeight;
    private int windowX;
    private int windowY;
    private int windowWidthBeforeFullScreen;
    private int windowHeightBeforeFullScreen;
    private int frameBufferWidth;
    private int frameBufferHeight;
    private int viewportWidth;
    private int viewportHeight;
    private int viewportX;
    private int viewportY;
    
    private float aspectRatio;
    private float viewportWidthInv;
    private float viewportHeightInv;
    
    private boolean vsync;
    private boolean windowed;
    private boolean minimized;
    private boolean lockAspectRatio;
    private boolean capabilitiesCreated;
    private boolean contextCurrent;
    
    private boolean windowPositionEvent;
    private boolean windowSizeEvent;
    private boolean framebufferSizeEvent;
    private boolean windowIconifyEvent;
    private boolean monitorEvent;
    
    private GLFWVidMode monitorDefaultVidMode;
    private GLFWVidMode vidModeBeforeWindowed;
    
    private final IntBuffer tmpBuffer1;
    private final IntBuffer tmpBuffer2;
    
    // When I start testing, I will experiment with the NudgeGLFW callbacks;
    private final GLFWWindowPosCallback windowPosCallback;
    private final GLFWWindowSizeCallback windowSizeCallback;
    private final GLFWFramebufferSizeCallback framebufferSizeCallback;
    private final GLFWWindowIconifyCallback windowIconifyCallback;
    private final GLFWMonitorCallback monitorCallback;
    
    
    public Window() {
        
        tmpBuffer1 = BufferUtils.createIntBuffer(1);
        tmpBuffer2 = BufferUtils.createIntBuffer(1);
        
        windowPosCallback = new GLFWWindowPosCallback() {
            @Override
            public void invoke(long window, int xpos, int ypos) {
                System.out.println("Window: windowPos callback invoked..");
                if (windowX != xpos || windowY != ypos) {
                    windowX = xpos;
                    windowY = ypos;
                    windowPositionEvent = true;
                }
            }
        };
        
        windowSizeCallback = new GLFWWindowSizeCallback() {
            @Override
            public void invoke(long window, int width, int height) {
                System.out.println("Window: windowSize callback invoked..");
                if (windowWidth != width || windowHeight != height) {
                    windowWidth = width;
                    windowHeight = height;
                    windowSizeEvent = true;
                }
            }
        };
        
        framebufferSizeCallback = new GLFWFramebufferSizeCallback() {
            @Override
            public void invoke(long window, int width, int height) {
                System.out.println("Window: frameBufferSize callback invoked..");
                System.out.println("Window: parameters: " + width + ", " + height);
                System.out.println("Window: old values: " + frameBufferWidth + ", " + frameBufferHeight);
                if (frameBufferWidth != width || frameBufferHeight != height) {
                    System.out.println("Window: frameBuffer values updated..");
                    frameBufferWidth = width;
                    frameBufferHeight = height;
                    framebufferSizeEvent = true;
                    System.out.println("Window: calling method: updateViewport(" + width + ", " + height + ");");
                    updateViewport(width,height);
                }
                else {
                    System.out.println("Window: frameBuffer values not updated..");
                }
            }
        };
        
        windowIconifyCallback = new GLFWWindowIconifyCallback() {
            @Override
            public void invoke(long window, boolean iconified) {
                System.out.println("Window: iconify callback invoked..");
                minimized = iconified;
                if (minimized) {
                    System.out.println("Window: minimized..");
                }
                else System.out.println("Window: restored from minimized..");
                windowIconifyEvent = true;
            }
        };
        
        monitorCallback = new GLFWMonitorCallback() {
            @Override
            public void invoke(long monitor, int event) {
                System.out.println("Window: monitor callback invoked..");
                if (event == GLFW_CONNECTED)
                {
                    System.out.println("Window: monitor was connected");
                }
                else if (event == GLFW_DISCONNECTED)
                {
                    System.out.println("Window: monitor was disconnected");
                }
            }
        };
    }
    
    @Override
    public void initialize(LaunchConfig config) {
    
        if (config == null) throw new IllegalStateException("Config cannot be null");
        
        
    
    }
    
    @Override
    public void controlStep() {
        
        // could check: if anything updated, is it correct?
        
        // also: could clear the screen if updated. if i have the step after swapBuffers.
    
    }
    
    @Override
    public void close() {
        
        if (shouldClose()) {
            // Terminate GLFW and free the error callback
            glfwDestroyWindow(window);
            
            // Might not be necessary to explicitly call these;
            
            // Window
            glfwSetWindowPosCallback(window, null);
            glfwSetWindowSizeCallback(window, null);
            glfwSetFramebufferSizeCallback(window, null);
            glfwSetWindowIconifyCallback(window, null);
            glfwSetMonitorCallback(null);
            // Mouse
            glfwSetMouseButtonCallback(window,null);
            glfwSetCursorPosCallback(window, null);
            glfwSetScrollCallback(window,null);
            // Keyboard
            glfwSetCharCallback(window,null);
            glfwSetKeyCallback(window,null);
            // GamePad
            //glfwSetJoystickCallback(null); // free explicitly
            
            
            monitorCallback.free(); // free explicitly
            glfwFreeCallbacks(window); // does not free all
            glfwTerminate();
            
            // ErrorCallback
            Objects.requireNonNull(glfwSetErrorCallback(null)).free();
        }
    }
    
    @Override
    public void setVisible(boolean visible) {
        if(visible) {
            glfwShowWindow(window);
        } else {
            glfwHideWindow(window);
        }
    }
    
    @Override
    public void makeContextCurrent() { // Any thread
        if (!contextCurrent) {
            glfwMakeContextCurrent(window);
            contextThreadID = Thread.currentThread().getId();
            contextCurrent = true;
        }
    }
    
    @Override
    public void createCapabilities() {
        if (!capabilitiesCreated) {
            if (!contextCurrent)
                throw new IllegalStateException("Context not current (use makeContextCurrent())");
            GL.createCapabilities();
            capabilitiesCreated = true;
        }
    }
    
    @Override
    public void swapBuffers() {
        glfwSwapBuffers(window);
    }
    
    @Override
    public void pollInputEvents() {
        glfwPollEvents();
    }
    
    @Override
    public void signalToClose() {
        System.out.println("Window: signalling to close..");
        glfwSetWindowShouldClose(window,true);
    }
    
    @Override
    public void setTitle(CharSequence title) {
        glfwSetWindowTitle(window, title);
    }
    
    @Override
    public void toggleVsync(boolean on) {
        System.out.println("Window: toggle vsync: " + on);
        glfwSwapInterval(on ? 1 : 0);
    }
    
    @Override
    public void centerWindow() {
    
        if (isWindowed()) {
            
            System.out.println("Window: currently in windowed mode..");
            System.out.println("Window: centering window..");
            GLFWVidMode vidMode = glfwGetVideoMode(monitor);
            
            System.out.println("Window: calling getWindowSize(tmpBuffer1, tmpBuffer2);");
            getWindowSize(tmpBuffer1,tmpBuffer2);
            
            int width = tmpBuffer1.get(0);
            int height = tmpBuffer2.get(0);
            System.out.println("Window: actual windowSize: " + width + ", " + height);
            
            if (vidMode != null) {
                
                int monitorResolutionWidth = vidMode.width();
                int monitorResolutionHeight = vidMode.height();
                System.out.println("Window: GLFWVidMode: primary-monitor's resolution: " + monitorResolutionWidth + ", " + monitorResolutionHeight);
                System.out.println("Window: positioning the window with the aforementioned parameters..");
                
                glfwSetWindowPos(
                        window,
                        (monitorResolutionWidth - width) / 2,
                        (monitorResolutionHeight - height) / 2
                );
            }
        }
        else {
            System.out.println("Window: currently in fullScreen mode..");
            System.out.println("Window: can not center window in fullScreen mode..");
        }
    }
    
    @Override
    public void setCallback(Callback callback) {
        
        // probably not going to use this.
        if (callback instanceof GLFWMonitorCallback) {
            glfwSetMonitorCallback((GLFWMonitorCallbackI) callback);
        }
        else if (callback instanceof  GLFWWindowSizeCallback) {
            glfwSetWindowSizeCallback(window,(GLFWWindowSizeCallbackI) callback);
        }
        else if (callback instanceof  GLFWWindowPosCallback) {
            glfwSetWindowPosCallback(window,(GLFWWindowPosCallbackI) callback);
        }
        else if (callback instanceof  GLFWWindowIconifyCallback) {
            glfwSetWindowIconifyCallback(window,(GLFWWindowIconifyCallbackI) callback);
        }
        else if (callback instanceof GLFWFramebufferSizeCallback) {
            glfwSetFramebufferSizeCallback(window,(GLFWFramebufferSizeCallbackI) callback);
        }
        else if (callback instanceof GLFWMouseButtonCallback) {
            glfwSetMouseButtonCallback(window,(GLFWMouseButtonCallbackI) callback);
        }
        else if (callback instanceof GLFWCursorPosCallback) {
            glfwSetCursorPosCallback(window,(GLFWCursorPosCallbackI) callback);
        }
        else if (callback instanceof GLFWScrollCallback) {
            glfwSetScrollCallback(window,(GLFWScrollCallbackI) callback);
        }
        else if (callback instanceof GLFWKeyCallback) {
            glfwSetKeyCallback(window,(GLFWKeyCallbackI) callback);
        }
        else if (callback instanceof GLFWCharCallback) {
            glfwSetCharCallback(window,(GLFWCharCallbackI) callback);
        }
        else if (callback instanceof GLFWJoystickCallback) {
            glfwSetJoystickCallback((GLFWJoystickCallbackI) callback);
        }
        else System.out.println("setCallback: callBack not supported by Engine");
    }
    
    @Override
    public void windowed(int width, int height) {
        
        System.out.println("Window: called method windowed(int " + width + ", int " + height + ");");
        if (windowed) {
            System.out.println("Window: currently in windowed mode..");
            System.out.println("Window: setting new windowSize: " + width + ", " + height);
            glfwSetWindowSize(window,width,height);
        }
        else {
            System.out.println("Window: currently in fullScreen mode..");
            System.out.println("Window: storing current VidMode for monitor fullScreen mode..");
            vidModeBeforeWindowed = glfwGetVideoMode(monitor);
            System.out.println("Window: Entering windowed mode with parameters: " + width + ", " + height);
            glfwSetWindowMonitor(window,NULL,0,0, width,height,GLFW_DONT_CARE);
            windowed = true;
        }
        System.out.println("Window: currently in windowed mode..");
        System.out.println("Window: calling method: centerWindow();");
        centerWindow();
    }
    
    @Override
    public void fullScreen(int width, int height) {
    
        System.out.println("Window: called method fullScreen(int " + width + ", int " + height + ");");
        
        if (!windowed) {
            System.out.println("Window: currently in fullScreen mode..");
            GLFWVidMode currentVidMode = glfwGetVideoMode(monitor);
            if (currentVidMode == null) throw new IllegalStateException(
                        "Window is not properly initialized. Could not identify a GLFWVidMode for monitor: " + monitor);
            if (width == currentVidMode.width() && height == currentVidMode.height()) {
                System.out.println("Window: window already in the fullScreen mode resolution: " + width + ", " + height);
                System.out.println("Window: returning unaltered..");
                return;
            }
        }
        else System.out.println("Window: currently in windowed mode..");
    
        int resolutionWidth, resolutionHeight;
    
        System.out.println("Window: checking if monitor supports the resolution: " + width + ", " + height);
    
        if (width == monitorDefaultVidMode.width() && height == monitorDefaultVidMode.height()) {
            System.out.println("Window: detected monitor default resolution");
            System.out.println("Window: resolution supported by monitor");
            resolutionWidth = width;
            resolutionHeight = height;
        }
        else {
            boolean monitorSupportsResolution = resolutionSupportedByMonitor(width,height);
            if (monitorSupportsResolution) {
                System.out.println("Window: resolution supported by monitor");
                resolutionWidth = width;
                resolutionHeight = height;
            }
            else {
                System.out.println("Window: resolution NOT supported by monitor");
                System.out.println("Window: using default monitor resolution..");
                resolutionWidth = monitorDefaultVidMode.width();
                resolutionHeight = monitorDefaultVidMode.height();
            }
        }
    
        float newAspectRatio = (float) resolutionWidth / resolutionHeight;
        if (aspectRatio != newAspectRatio) {
            if (!lockAspectRatio) {
                System.out.println("Window: aspectRatio not locked");
                System.out.println("Window: changing aspectRatio from: " + aspectRatio + " to " + newAspectRatio);
                aspectRatio = newAspectRatio;
            }
        }
        
        if (windowed) {
            
            System.out.println("Window: storing the windowSize parameters for windowed mode..");
            getWindowSize(tmpBuffer1,tmpBuffer2);
            windowWidthBeforeFullScreen = tmpBuffer1.get(0);
            windowHeightBeforeFullScreen = tmpBuffer2.get(0);
            
            System.out.println("Window: entering fullScreen mode with parameters: " + resolutionWidth + ", " + resolutionHeight);
            glfwSetWindowMonitor(window,monitor,0,0,resolutionWidth,resolutionHeight,monitorDefaultVidMode.refreshRate());
            windowed = false;
            
        }
        else {
            
            System.out.println("Window: changing resolution to: " + resolutionWidth + ", " + resolutionHeight);
            glfwSetWindowSize(window,resolutionWidth,resolutionHeight);
        
        }
    }
    
    @Override
    public void toggleFullScreen() {
    
        System.out.println("Window: called method: toggleFullScreen();");
        
        long monitor;
        int width;
        int height;
        int refreshRate;
        
        if (windowed) {
            System.out.println("Window: currently in windowed mode..");
            
            GLFWVidMode vidMode = vidModeBeforeWindowed;
            monitor = this.monitor;
            width = vidMode.width();
            height = vidMode.height();
            refreshRate = vidMode.refreshRate();
    
            System.out.println("Window: storing the windowSize parameters for windowed mode..");
            getWindowSize(tmpBuffer1,tmpBuffer2);
            windowWidthBeforeFullScreen = tmpBuffer1.get(0);
            windowHeightBeforeFullScreen = tmpBuffer2.get(0);
    
            System.out.println("Window: entering fullScreen mode with parameters: " + width + ", " + height);
        }
        else {
            System.out.println("Window: currently in fullScreen mode..");
            
            monitor = NULL;
            width = windowWidthBeforeFullScreen;
            height = windowHeightBeforeFullScreen;
            refreshRate = GLFW_DONT_CARE;
    
            System.out.println("Window: storing current VidMode for monitor fullScreen mode..");
            vidModeBeforeWindowed = glfwGetVideoMode(monitor);
            
            System.out.println("Window: entering windowed mode with parameters: " + width + ", " + height);
        }
    
        glfwSetWindowMonitor(window,monitor,0,0,width,height,refreshRate);
    }
    
    @Override
    public long windowHandle() {
        return window;
    }
    
    @Override
    public long monitorHandle() {
        return monitor;
    }
    
    @Override
    public boolean shouldClose() {
        return glfwWindowShouldClose(window);
    }
    
    @Override
    public boolean isWindowed() {
        return windowed;
    }
    
    @Override
    public boolean isMinimized() {
        return minimized;
    }
    
    @Override
    public boolean vsyncEnabled() {
        return vsync;
    }
    
    @Override
    public int windowWidth() {
        return windowWidth;
    }
    
    @Override
    public int windowHeight() {
        return windowHeight;
    }
    
    @Override
    public int windowX() {
        return windowX;
    }
    
    @Override
    public int windowY() {
        return windowY;
    }
    
    @Override
    public int frameBufferWidth() {
        return frameBufferWidth;
    }
    
    @Override
    public int frameBufferHeight() {
        return frameBufferHeight;
    }
    
    @Override
    public int viewportWidth() {
        return viewportWidth;
    }
    
    @Override
    public int viewportHeight() {
        return viewportHeight;
    }
    
    @Override
    public float viewportWidthInv() {
        return viewportWidthInv;
    }
    
    @Override
    public float viewportHeightInv() {
        return viewportHeightInv;
    }
    
    @Override
    public int viewportX() {
        return viewportX;
    }
    
    @Override
    public int viewportY() {
        return viewportY;
    }
    
    @Override
    public float aspectRatio() {
        return aspectRatio;
    }
    
    
    
    private void updateViewport(int fbWidth, int fbHeight) {
        
        if (!capabilitiesCreated)
            throw new IllegalStateException("Window not initialized");
    
        System.out.println("Window: updating viewport..");
        
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
        this.viewportX = viewPortX;
        this.viewportY = viewPortY;
    
        System.out.println("Window: calling method: glViewport(" + viewPortX + ", " + viewPortY +
                                  ", " + aspectWidth + ", " + aspectHeight + ");");
        
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
    
    private void getFrameBufferSize(IntBuffer width, IntBuffer height) {
        glfwGetFramebufferSize(window,width,height);
    }
    
    private void getWindowSize(IntBuffer width, IntBuffer height) {
        glfwGetWindowSize(window,width,height);
    }
    
    private void getWindowPosition(IntBuffer x, IntBuffer y) {
        glfwGetWindowPos(window, x, y);
    }
    
    private boolean notMainThread() {
        return CORE.isMainThread(Thread.currentThread().getId());
    }
    
    private boolean currentThreadIsContextThread() {
        return contextThreadID != Thread.currentThread().getId();
    }
}
