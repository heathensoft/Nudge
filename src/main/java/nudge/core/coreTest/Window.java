package nudge.core.coreTest;

import org.lwjgl.system.Callback;

/**
 * @author Frederik Dahl
 * 17/08/2021
 */


public interface Window {

    // lifeCycle
    void initialize(LaunchConfig config);
    void step();
    void close();
    
    // setters
    void show();
    void makeContextCurrent();
    void createCapabilities();
    void swapBuffers();
    void pollInputEvents();
    void signalToClose();
    void setTitle(String title);
    void toggleVsync(boolean on);
    void centerWindow();
    void setCallback(Callback callback);
    boolean windowed(int width, int height);
    boolean fullScreen(int width, int height);
    boolean setWindowedSize(int width, int height);
    boolean setResolution(int width, int height);
    
    // getters
    long windowHandle();
    long monitorHandle();
    boolean shouldClose();
    boolean isWindowed();
    boolean isMinimized();
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
