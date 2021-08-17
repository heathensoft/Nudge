package nudge.core.view;

import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.glfwSetWindowIconifyCallback;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.system.MemoryStack.stackPush;

/**
 * @author Frederik Dahl
 * 09/07/2021
 */


public class Display {

    private final long window;

    private int screenWidth;
    private int screenHeight;
    private int viewportWidth;
    private int viewportHeight;
    private int viewportX0;
    private int viewportY0;
    private float viewportWidthInv;
    private float viewportHeightInv;
    private final float aspectRatio;


    private boolean minimized;

    public Display(long window, float aspectRatio) {

        this.window = window;
        this.aspectRatio = aspectRatio;

        try ( MemoryStack stack = stackPush() ) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);
            glfwGetWindowSize(window, pWidth, pHeight);
            screenWidth = pWidth.get();
            screenHeight = pHeight.get();
        }
        resizeCallback(window,screenWidth,screenHeight);

        GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        if (vidMode == null) throw new RuntimeException("Failed to get primary monitor");

        glfwSetWindowPos(
                window, // Center the window
                (vidMode.width() - screenWidth) / 2,
                (vidMode.height() - screenHeight) / 2
        );
        glfwSetWindowSizeCallback(window,this::resizeCallback);
        glfwSetWindowIconifyCallback(window,this::minimizeCallback);
    }

    private void resizeCallback(long glfwWindow, int screenWidth, int screenHeight) {
        
        // Figure out the largest area that fits this target aspect ratio
        int aspectWidth = screenWidth;
        int aspectHeight = (int)((float)aspectWidth / aspectRatio);
        // If it doesn't fit so we change to pillarBox
        if (aspectHeight > screenHeight) {
            aspectHeight = screenHeight;
            aspectWidth = (int)((float)aspectHeight * aspectRatio);
        }
        int viewPortX = (int) (((float)screenWidth / 2f) - ((float)aspectWidth / 2f));
        int viewPortY = (int) (((float)screenHeight / 2f) - ((float)aspectHeight / 2f));

        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.viewportWidth = aspectWidth;
        this.viewportHeight = aspectHeight;
        this.viewportWidthInv = 1f / aspectWidth;
        this.viewportHeightInv = 1f / aspectHeight;
        this.viewportX0 = viewPortX;
        this.viewportY0 = viewPortY;

        glViewport(viewPortX,viewPortY,aspectWidth,aspectHeight);
    }

    private void minimizeCallback(long glfwWindow, boolean iconified) {
        this.minimized = iconified;
    }

    public long window() { return window; }

    public int screenWidth() { return screenWidth; }

    public int screenHeight() { return screenHeight; }

    public int viewportWidth() { return viewportWidth; }

    public int viewportHeight() { return viewportHeight; }

    public int viewportX0() { return viewportX0; }

    public int viewportY0() { return viewportY0; }

    public float aspectRatio() { return aspectRatio; }

    public float viewportWidthInv() { return viewportWidthInv; }

    public float viewportHeightInv() { return viewportHeightInv; }

    public boolean isMinimized() { return minimized; }
    
    
}
