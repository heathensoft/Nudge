package nudge.core.coreTest;

import org.lwjgl.system.Callback;

/**
 * @author Frederik Dahl
 * 17/08/2021
 */


public interface IWindow {

    // lifeCycle
    void initialize(LaunchConfig config);
    void controlStep();
    void close();
    
    // setters
    void setVisible(boolean visible);
    void makeContextCurrent();
    void createCapabilities();
    void swapBuffers();
    void pollInputEvents();
    void signalToClose();
    void setTitle(CharSequence title);
    void toggleVsync(boolean on);
    void centerWindow();
    void setCallback(Callback callback);
    void windowed(int width, int height);
    void fullScreen(int width, int height);
    void toggleFullScreen();
    
    // getters
    long windowHandle();
    long monitorHandle();
    boolean shouldClose();
    boolean isWindowed();
    boolean isMinimized();
    boolean vsyncEnabled();
    int windowWidth();
    int windowHeight();
    int windowX();
    int windowY();
    int frameBufferWidth();
    int frameBufferHeight();
    int viewportWidth();
    int viewportHeight();
    float viewportWidthInv();
    float viewportHeightInv();
    int viewportX();
    int viewportY();
    float aspectRatio();
}
