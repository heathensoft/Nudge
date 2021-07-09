package nudge.core;

import nudge.core.input.KEYBOARD;
import nudge.core.input.MOUSE;
import nudge.core.view.VIEW;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * @author Frederik Dahl
 * 05/07/2021
 */


public final class CORE {

    private static CORE instance;

    private Application app;

    private long window;

    boolean initialized;

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

        System.out.println("LWJGL version:" + Version.getVersion());
        System.out.println("GLFW version:" + glfwGetVersionString());
        System.out.println("INITIALIZING GLFW WINDOW");

        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit()) throw new IllegalStateException("Unable to Initialize GLFW.");

        this.app = app;

        app.configure(); // set additional settings and window hints

        Settings config = app.settings(); // getting the apps configuration

        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, config.resizeEnabled() ? GL_TRUE : GL_FALSE);

        int screenW = config.screenWidth();
        int screenH = config.screenHeight();
        String title = app.title() + "(v." + app.versionMajor() + "." + app.versionMinor() + ")";

        long monitor = config.fullScreenEnabled() ?  glfwGetPrimaryMonitor() : NULL;
        window = glfwCreateWindow(screenW, screenH, title, monitor, NULL);

        if (window == NULL) throw new IllegalStateException("Failed to create the GLFW window.");

        glfwSetCursorPosCallback(   window, MOUSE::mousePosCallback);
        glfwSetMouseButtonCallback( window, MOUSE::mouseButtonCallback);
        glfwSetScrollCallback(      window, MOUSE::mouseScrollCallback);
        glfwSetKeyCallback(         window, KEYBOARD::keyCallback);
        glfwSetCharCallback(        window, KEYBOARD::charCallback);

        glfwMakeContextCurrent(window);
        glfwSwapInterval(config.vsyncEnabled() ? 1 : 0);
        glfwShowWindow(window);

        GL.createCapabilities();

        float aspectRatio = config.aspectRatio();

        if(config.monitorAspectRaEnabled()) {
            GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
            if (vidMode == null) throw new RuntimeException("Monitor Error");
            int targetWidth = vidMode.width();
            int targetHeight = vidMode.height();
            aspectRatio = (float) targetWidth / (float) targetHeight;
        }

        view().initialize(window,aspectRatio);



        initialized = true;
    }

    private void mainLoop() {



    }

    private void terminate() {

        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();


    }


    public Application app() {return app; }

    public VIEW view() { return VIEW.get(); }

    public MOUSE mouse() { return MOUSE.get(); }

    public KEYBOARD keyboard() { return KEYBOARD.get(); }




}
