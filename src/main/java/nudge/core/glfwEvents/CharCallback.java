package nudge.core.glfwEvents;

import org.lwjgl.glfw.GLFWCharCallback;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_UNKNOWN;

/**
 * @author Frederik Dahl
 * 17/08/2021
 */


public class CharCallback extends GLFWCharCallback implements GLFWCallbackNudge {
    
    private int codepoint = GLFW_KEY_UNKNOWN;
    private boolean event;
    
    @Override
    public void invoke(long window, int codepoint) {
        this.codepoint = codepoint;
        event = true;
    }
    
    public int getCodepoint() { return codepoint; }
    
    @Override
    public boolean eventQueued() { return event; }
    
    @Override
    public void reset() { event = false; }
}
