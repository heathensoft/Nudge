package nudge.core.glfwEvents;

import org.lwjgl.glfw.GLFWWindowPosCallback;

/**
 * @author Frederik Dahl
 * 18/08/2021
 */


public class WindowPosCallback extends GLFWWindowPosCallback implements GLFWCallbackNudge {
    
    private int x, y;
    
    private boolean event;
    
    @Override
    public void invoke(long window, int xpos, int ypos) {
        
        if (x != xpos || y != ypos) {
            x = xpos; y = ypos;
            event = true;
        }
    }
    
    @Override
    public boolean eventQueued() { return event; }
    
    @Override
    public void reset() { event = false; }
    
    public int getX() { return x; }
    
    public int getY() { return y; }
}
