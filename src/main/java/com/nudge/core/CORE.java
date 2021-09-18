package com.nudge.core;

import com.nudge.core.input.Keyboard;
import com.nudge.core.input.Mouse;
import com.nudge.core.view.Display;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;

import java.util.Objects;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * @author Frederik Dahl
 * 05/07/2021
 */


public final class CORE {
    
    private static CORE instance;
    
    public static final long MAIN_THREAD_ID = Thread.currentThread().getId();
    
    public Application app;
    public Display display; // Window rather
    public Keyboard keyboard;
    public Mouse mouse;
    
    private long window;


    private CORE() { }
    

    public void start(final Application app) {
        
        try {
            initialize(app);
            run();
        }
        finally {
            terminate();
        }
        
    }

    private void initialize(final Application app) {
        
        printSystemInfo();
        System.out.println("\nINITIALIZING GLFW WINDOW");
        
        mouse = new Mouse();
        keyboard = new Keyboard();

        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW library
        if (!glfwInit()) throw new IllegalStateException("Failed to Initialize GLFW.");

        // App Configuration
        this.app = app;
        app.configure(); // set additional settings and / or window hints
        Config config = app.settings(); // get the app configuration

        // Hiding window while initializing
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, config.resizeEnabled() ? GL_TRUE : GL_FALSE);

        glfwSwapInterval(config.vsyncEnabled() ? 1 : 0); // V-sync

        // Create window
        String title = app.title() + "(v." + app.versionMajor() + "." + app.versionMinor() + ")";
        long monitor = config.fullScreenEnabled() ?  glfwGetPrimaryMonitor() : NULL;
        window = glfwCreateWindow(config.screenWidth(), config.screenHeight(), title, monitor, NULL);
        if (window == NULL) throw new IllegalStateException("Failed to create GLFW window.");

        // Input callbacks
        glfwSetCursorPosCallback(   window, mouse.cursorPositionCallback());
        glfwSetMouseButtonCallback( window, mouse.mouseButtonCallback());
        glfwSetScrollCallback(      window, mouse.scrollCallback());
        glfwSetKeyCallback(         window, keyboard.getKeyCallback());
        glfwSetCharCallback(        window, keyboard.getCharCallback());

        glfwMakeContextCurrent(window);

        // Aspect ratio.
        float aspectRatio = config.aspectRatio();

        if(config.monitorAspectRaEnabled()) {
            GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
            if (vidMode == null) throw new RuntimeException("Unable to access primary monitor");
            int targetWidth = vidMode.width();
            int targetHeight = vidMode.height();
            aspectRatio = (float) targetWidth / (float) targetHeight;
        }

        // About GL.createCapabilities():
        // (http://forum.lwjgl.org/index.php?topic=6459.0)

        // "In order for LWJGL to actually know about the OpenGL context and initialize itself using that context,
        // we have to call GL.createCapabilities(). It is only after that call that we can call OpenGL functions
        // via methods on those GLxx and extension classes living in the org.lwjgl.opengl package."

        // it's not entirely clear to me. But before using certain functionality like calling ie. glfwShowWindow()
        // below here, or glViewport() (inside Display's constructor, also below) or glGenTextures() inside our Texture class etc.
        // we create an instance of our OPENGL context created above (glfwMakeContextCurrent(window)) through GL.createC..()

        GL.createCapabilities();

        // Setting up our Display, with some window related callbacks
        display = new Display(window,aspectRatio);

        // Default blending. Change at anytime.
        glEnable(GL_BLEND); // Enabling alfa-blend
        // What blending function to use (very typical)
        glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);

        // Finally
        glfwShowWindow(window);
    }

    private void run() {
    
    
    }

    private void terminate() {
        
        System.out.println("\nTERMINATING GLFW WINDOW AND CONTEXT");
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);
        // Terminate GLFW and free the error callback
        glfwTerminate();
        Objects.requireNonNull(glfwSetErrorCallback(null)).free();
        System.out.println("\tTERMINATED");

    }
    
    private void printSystemInfo() {
    
        String platform = System.getProperty("os.name") + ", " + System.getProperty("os.arch") + " Platform.";
        int numProcessors = Runtime.getRuntime().availableProcessors();
        int JREMemoryMb = (int)(Runtime.getRuntime().maxMemory() / 1000000L);
        String jre = System.getProperty("java.version");
    
        System.out.println("\nWelcome!\n");
        System.out.println("SYSTEM INFO\n");
    
        System.out.println("---Running on: " + platform);
        System.out.println("---jre: " + jre);
        System.out.println("---Available processors: " + numProcessors);
        System.out.println("---Reserved memory: " + JREMemoryMb + " Mb");
    
        System.out.println("---LWJGL version: " + Version.getVersion());
        System.out.println("---GLFW version: " + glfwGetVersionString());
        
    }
    
    public static void EXIT() { glfwSetWindowShouldClose(get().window,true); }
    
    public static CORE get() { return instance == null ? instance = new CORE() : instance; }
    
    public static boolean isMainThread(long threadID) {
        return threadID == CORE.MAIN_THREAD_ID;
    }

}
