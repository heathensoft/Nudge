package nudge.core.glfwEvents;

/**
 * @author Frederik Dahl
 * 17/08/2021
 */


public interface GLFWCallbackNudge {
    
    // Useful for polymorphism.
    
    // On GLFW-event handling in general:
    // We want to avoid handling glfw-events directly. This MAY cause issues for the glfw window.
    // Instead: We register events, and update the respective functionality once pr. frame
    // in the appropriate place in our main loop, followed by a reset.
    
    // 1.   GLFW event
    // 2.   GLFWCallback
    // 3.   Specific callback already called this frame? Then ignore if values are the same.
    //      Else store values if there is a change. And if so, Queue event.
    // 4.   Any events queued? Then query / use the new values, and reset the eventCallbacks
    
    boolean eventQueued();
    
    void reset();
}
