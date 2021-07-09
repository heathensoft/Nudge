package nudge.core;

import nudge.core.input.KEYBOARD;
import nudge.core.input.MOUSE;
import nudge.core.view.Display;
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

    // Single instance classes:

    // Capital letter names for classes and methods in the context of the Nudge framework signify
    // some relation to a global context: mainly single instance global classes (like CORE, MOUSE and KEYBOARD)
    // but also for some critical static functions like CORE.EXIT();

    private static CORE instance;
    private Application app;
    private Display display;
    private long window;


    private CORE() {}


    public static CORE get() {
        if (instance == null)
            instance = new CORE();
        return instance;
    }

    public void run(final Application app) {
        initialize(app);
        mainLoop();
        terminate();
    }

    private void initialize(final Application app) {

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

        System.out.println("\nINITIALIZING GLFW WINDOW");

        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW library
        if (!glfwInit()) throw new IllegalStateException("Failed to Initialize GLFW.");

        // App Configuration
        this.app = app;
        app.configure(); // set additional settings and / or window hints
        WinConfig config = app.settings(); // get the app configuration

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
        glfwSetCursorPosCallback(   window, MOUSE::mousePosCallback);
        glfwSetMouseButtonCallback( window, MOUSE::mouseButtonCallback);
        glfwSetScrollCallback(      window, MOUSE::mouseScrollCallback);
        glfwSetKeyCallback(         window, KEYBOARD::keyCallback);
        glfwSetCharCallback(        window, KEYBOARD::charCallback);

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

    private void mainLoop() {



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


    // todo: How does this relate to our fps / ups timing system?
    public void enableVsync(boolean b) { glfwSwapInterval(b ? 1 : 0); }

    public Application app() { return app; }

    public Display display() { return display; }

    public MOUSE MOUSE() { return MOUSE.get(); }

    public KEYBOARD KEYBOARD() { return KEYBOARD.get(); }

    public static void EXIT() { glfwSetWindowShouldClose(get().window,true); }



}
