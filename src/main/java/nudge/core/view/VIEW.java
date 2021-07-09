package nudge.core.view;

import nudge.core.Settings;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.system.MemoryStack.stackPush;

/**
 * @author Frederik Dahl
 * 08/07/2021
 */


public final class VIEW {

    private static VIEW instance;

    private Camera camera;

    private int screenWidth;
    private int screenHeight;
    private int viewportWidth;
    private int viewportHeight;
    private int viewportX;
    private int viewportY;
    private float aspectRatio;
    private float viewW_normalized;
    private float viewH_normalized;

    private boolean minimized;



    private VIEW() {}

    public static VIEW get() {
        if (instance == null) {
            instance = new VIEW();
        }
        return instance;
    }

    public void initialize(long window, float aspectRatio) {

        this.aspectRatio = aspectRatio;

        try ( MemoryStack stack = stackPush() ) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*
            glfwGetWindowSize(window, pWidth, pHeight);
            screenWidth = pWidth.get();
            screenHeight = pHeight.get();
        }

        resizeCallback(window,screenWidth,screenHeight);

        glfwSetWindowSizeCallback(window,this::resizeCallback);
        glfwSetWindowIconifyCallback(window,this::minimizeCallback);

        // get app camera
    }

    private void resizeCallback(long glfwWindow, int screenWidth, int screenHeight) {

        glfwSetWindowSize(glfwWindow, screenWidth, screenHeight);

        // Figure out the largest area that fits this target aspect ratio
        int aspectWidth = screenWidth;
        int aspectHeight = (int)((float)aspectWidth / aspectRatio);

        if (aspectHeight > screenHeight) {
            // it doesn't fit so we mush change to pillarBox
            aspectHeight = screenHeight;
            aspectWidth = (int)((float)aspectHeight * aspectRatio);
        }
        // Center rectangle
        int viewPortX = (int) (((float)screenWidth / 2f) - ((float)aspectWidth / 2f));
        int viewPortY = (int) (((float)screenHeight / 2f) - ((float)aspectHeight / 2f));

        this.viewportWidth = aspectWidth;
        this.viewportHeight = aspectHeight;
        this.viewW_normalized = 1f / aspectWidth;
        this.viewH_normalized = 1f / aspectHeight;
        this.viewportX = viewPortX;
        this.viewportY = viewPortY;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        // check if camera != null
        //camera.adjustTest(aspectWidth,aspectHeight);
        glViewport(viewPortX,viewPortY,aspectWidth,aspectHeight);
    }

    private void minimizeCallback(long glfwWindow, boolean iconified) {
        this.minimized = iconified;
    }

    private static void setCamera(Camera camera) {


    }

    public static int screenWidth() {
        return instance.screenWidth;
    }

    public static int screenHeight() {
        return instance.screenHeight;
    }

    public static int viewportWidth() {
        return instance.viewportWidth;
    }

    public static int viewportHeight() {
        return instance.viewportHeight;
    }

    public static int viewportX() {
        return instance.viewportX;
    }

    public static int viewportY() {
        return instance.viewportY;
    }

    public static float aspectRatio() { return instance.aspectRatio; }

    public static float viewW_normalized() { return instance.viewW_normalized; }

    public static float viewH_normalized() { return instance.viewH_normalized; }
}
