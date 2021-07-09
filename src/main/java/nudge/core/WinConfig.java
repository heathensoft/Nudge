package nudge.core;

/**
 * @author Frederik Dahl
 * 08/07/2021
 */


public interface WinConfig {

    boolean DEFAULT_VSYNC           = false;
    boolean DEFAULT_RESIZABLE       = false;
    boolean DEFAULT_FULL_SCREEN     = false;
    boolean DEFAULT_USE_MONITOR_AR  = true;
    boolean DEFAULT_LIMIT_FPS       = false;

    float DEFAULT_ASPECT_RATIO      = 16/9f;
    int DEFAULT_SCREEN_WIDTH        = 1280;
    int DEFAULT_SCREEN_HEIGHT       = 720;
    int DEFAULT_TARGET_FPS          = 60;

    default int targetFPS() { return  DEFAULT_TARGET_FPS; }

    default int screenWidth() { return DEFAULT_SCREEN_WIDTH; }

    default int screenHeight() { return DEFAULT_SCREEN_HEIGHT; }

    default float aspectRatio() { return DEFAULT_ASPECT_RATIO; }

    default boolean monitorAspectRaEnabled() { return DEFAULT_USE_MONITOR_AR; }

    default boolean vsyncEnabled() { return DEFAULT_VSYNC; }

    default boolean resizeEnabled() { return DEFAULT_RESIZABLE; }

    default boolean fullScreenEnabled() { return DEFAULT_FULL_SCREEN; }

    default boolean limitFPSEnabled() { return DEFAULT_LIMIT_FPS; }

}
