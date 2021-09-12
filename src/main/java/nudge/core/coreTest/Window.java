package nudge.core.coreTest;

import nudge.core.view.VideoMode;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.Callback;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Objects;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.glfwSetWindowMonitor;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * @author Frederik Dahl
 * 17/08/2021
 */


public class Window implements IWindow {
    
    // todo: Create a logger
    
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
    
    private GLFWVidMode monitorDefaultVidMode;
    private GLFWVidMode vidModeBeforeWindowed;
    
    private final IntBuffer tmpBuffer1;
    private final IntBuffer tmpBuffer2;
    
    // When I start testing, I will experiment with the NudgeGLFW callbacks;
    // Apparently, the display related callbacks are executed independently from glfwPollEvents.
    // as opposed to the input based events.
    
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
                    updateViewportFields();
                    glViewport();
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
        
        // Set error callback
        GLFWErrorCallback.createPrint(System.err).set();
        
        // Initialize the GLFW library
        if (!glfwInit()) throw new IllegalStateException("Failed to Initialize GLFW.");
        
        // Application launch configuration
        boolean resizable = config.resizableWindow();
        int desiredWidth = config.desiredResolutionWidth();
        int desiredHeight = config.desiredResolutionHeight();
        aspectRatio = (float) desiredWidth / desiredHeight;
        lockAspectRatio = config.lockAspectRatio();
        vsync = config.verticalSynchronization();
        windowed = config.windowedMode();
        
        // GLFW Window hints. Subject to change.
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, resizable ? GLFW_TRUE : GLFW_FALSE);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        
        // GLFW Monitor
        
        System.out.println("Window: detecting primary monitor..");
        
        monitor = glfwGetPrimaryMonitor();
        GLFWVidMode vidMode = getVidMode();
    
        System.out.println("Window: monitor vidMode default resolution: " + vidMode.width() + ", " + vidMode.height());
        System.out.println("Window: monitor vidMode default refresh rate: " + vidMode.refreshRate() + " Hz");
        
        monitorDefaultVidMode = vidModeBeforeWindowed = vidMode;
        
        
        
        
        // Window creation
        
        System.out.println("Window: creating the window.. ");
        
        if (windowed) {
            System.out.println("Window: creating windowed mode window with desired resolution: " + desiredWidth + ", " + desiredHeight);
            window = glfwCreateWindow(desiredWidth,desiredHeight,"Application",NULL,NULL);
            if ( window == NULL ) throw new RuntimeException("Failed to create the GLFW window");
            System.out.println("Window: windowed mode window created");
        }
        else {
            
            // We stick with the "go-to" resolution of the primary monitor if the monitor don't have support
            // for the desired resolution.
            //
            // If so, depending on whether the aspect ratio is locked by the launch configuration, we readjust / don't readjust
            // the aspect ratio to MATCH that resolution. Locked: The viewport will reflect the locked ratio independent of resolution
            // with either horizontal or vertical border-boxes.
            //
            // Conversely. On support for the desired resolution, the window should display the view in
            // proper full-screen without border-box.
            
            int resolutionWidth, resolutionHeight;
            
            if (resolutionSupportedByMonitor(desiredWidth,desiredHeight)) {
                System.out.println("Window: resolution supported by monitor");
                resolutionWidth = desiredWidth;
                resolutionHeight = desiredHeight;
            }
            else {
                System.out.println("Window: resolution NOT supported by monitor");
                System.out.println("Window: using default monitor resolution..");
                resolutionWidth = monitorDefaultVidMode.width();
                resolutionHeight = monitorDefaultVidMode.height();
            }
    
            System.out.println("Window: creating fullScreen window with resolution: " + resolutionWidth + ", " + resolutionHeight);
            window = glfwCreateWindow(resolutionWidth,resolutionHeight,"Application",monitor,NULL);
            
            vidMode = getVidMode();
            
            System.out.println("Window: fullScreen window created");
            System.out.println("Window: monitor vidMode resolution: " + vidMode.width() + ", " + vidMode.height());
            System.out.println("Window: monitor vidMode refresh rate: " + vidMode.refreshRate() + " Hz");
        }
        
        getWindowSize(tmpBuffer1,tmpBuffer2);
        windowWidth = tmpBuffer1.get(0);
        windowHeight = tmpBuffer2.get(0);
    
        System.out.println("Window: window size: " + windowWidth + ", " + windowHeight);
    
        getFrameBufferSize(tmpBuffer1,tmpBuffer2);
        frameBufferWidth = tmpBuffer1.get(0);
        frameBufferHeight = tmpBuffer2.get(0);
    
        System.out.println("Window: framebuffer size: " + frameBufferWidth + ", " + frameBufferHeight);
    
        windowWidthBeforeFullScreen = windowWidth;
        windowHeightBeforeFullScreen = windowHeight;
        
        if (windowed) {
            
            centerWindow();
        }
        else {
            vidModeBeforeWindowed = vidMode;
        }
    
        setAspectRatio(frameBufferWidth, frameBufferHeight, false);
        
        getWindowPosition(tmpBuffer1, tmpBuffer2);
        windowX = tmpBuffer1.get(0);
        windowY = tmpBuffer2.get(0);
        
        updateViewportFields(); // glViewport() is called by CORE after GL.createCapabilities():
    
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
    
        // About GL.createCapabilities():
        // (http://forum.lwjgl.org/index.php?topic=6459.0)
    
        // "In order for LWJGL to actually know about the OpenGL context and initialize itself using that context,
        // we have to call GL.createCapabilities(). It is only after that call that we can call OpenGL functions
        // via methods on those GLxx and extension classes living in the org.lwjgl.opengl package."
    
        // it's not entirely clear to me. But before using certain functionality like calling ie. glfwShowWindow()
        // below here, or glViewport() (inside Display's constructor, also below) or glGenTextures() inside our Texture class etc.
        // we create an instance of our OPENGL context (glfwMakeContextCurrent(window)) through GL.createC..()
        
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
    public void glViewport() {
        if (!capabilitiesCreated) throw new IllegalStateException("Window not initialized");
        System.out.println("Window: calling method: glViewport(" + viewportX + ", " + viewportY +
                                   ", " + viewportWidth + ", " + viewportHeight + ");");
        GL11.glViewport(viewportX,viewportY,viewportWidth,viewportHeight);
    }
    
    @Override
    public void setWindowTitle(CharSequence title) {
        glfwSetWindowTitle(window, title);
    }
    
    @Override
    public void toggleVsync(boolean on) {
        vsync = on;
        System.out.println("Window: toggle vsync: " + on);
        glfwSwapInterval(on ? 1 : 0);
    }
    
    @Override
    public void lockAspectRatio(boolean lock) {
        lockAspectRatio = lock;
    }
    
    @Override
    public void centerWindow() {
    
        System.out.println("Window: centering window..");
        
        if (isWindowed()) {
            
            System.out.println("Window: currently in windowed mode..");
            GLFWVidMode vidMode = getVidMode();
            
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
            vidModeBeforeWindowed = getVidMode();
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
            GLFWVidMode currentVidMode = getVidMode();
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
        setAspectRatio(newAspectRatio,false);
        
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
    public void minimizeWindow() {
        System.out.println("Window: minimize window.. ");
        glfwIconifyWindow(window);
    }
    
    @Override
    public void restoreWindow() {
        System.out.println("Window: restore window.. ");
        glfwRestoreWindow(window);
    }
    
    @Override
    public void maximizeWindow() {
        System.out.println("Window: maximize window.. ");
        glfwMaximizeWindow(window);
    }
    
    @Override
    public void focusWindow() {
        // window should be visible and not iconified
        System.out.println("Window: focus window.. ");
        glfwFocusWindow(window);
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
    
    // follow up with Window.glViewport() to notify openGL
    private void updateViewportFields() {
        
        System.out.println("Window: updating viewport fields..");
        
        int aspectWidth = frameBufferWidth;
        int aspectHeight = (int)((float)aspectWidth / aspectRatio);
        if (aspectHeight > frameBufferHeight) {
            aspectHeight = frameBufferHeight;
            aspectWidth = (int)((float)aspectHeight * aspectRatio);
        }
        int viewPortX = (int) (((float)frameBufferWidth / 2f) - ((float)aspectWidth / 2f));
        int viewPortY = (int) (((float)frameBufferHeight / 2f) - ((float)aspectHeight / 2f));
        
        this.viewportWidth = aspectWidth;
        this.viewportHeight = aspectHeight;
        this.viewportWidthInv = 1f / aspectWidth;
        this.viewportHeightInv = 1f / aspectHeight;
        this.viewportX = viewPortX;
        this.viewportY = viewPortY;
        
    }
    // follow up with Window.glViewport() to notify openGL
    private void updateViewportFields(int fbWidth, int fbHeight) {
        
        System.out.println("Window: updating viewport fields..");
        
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
    
    private GLFWVidMode getVidMode() {
        return glfwGetVideoMode(monitor);
    }
    
    private void setAspectRatio(float newAspectRatio, boolean updateViewport) {
        System.out.println("Window: changing aspectRatio from: " + aspectRatio + " to " + newAspectRatio);
        if (aspectRatio != newAspectRatio) {
            if (!lockAspectRatio) {
                System.out.println("Window: changing aspectRatio from: " + aspectRatio + " to " + newAspectRatio);
                aspectRatio = newAspectRatio;
                if (updateViewport) {
                    getFrameBufferSize(tmpBuffer1,tmpBuffer2);
                    int width = tmpBuffer1.get(0);
                    int height = tmpBuffer1.get(0);
                    updateViewportFields(width,height);
                    glViewport();
                }
                else System.out.println("Window: ..without updating viewport");
            }
            else System.out.println("Window: no change to aspectRatio: locked");
        }
    }
    
    private void setAspectRatio(int width, int height, boolean updateViewport) {
        float newAspectRatio = (float) width / height;
        System.out.println("Window: changing aspectRatio from: " + aspectRatio + " to " + newAspectRatio);
        if (aspectRatio != newAspectRatio) {
            if (!lockAspectRatio) {
                System.out.println("Window: changing aspectRatio from: " + aspectRatio + " to " + newAspectRatio);
                aspectRatio = newAspectRatio;
                if (updateViewport) {
                    updateViewportFields(width,height);
                    glViewport();
                }
                else System.out.println("Window: ..without updating viewport");
            }
            else System.out.println("Window: no change to aspectRatio: locked");
        }
        
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
