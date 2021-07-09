package nudge.core;

/**
 * @author Frederik Dahl
 * 08/07/2021
 */


public interface Settings {

    boolean DEFAULT_VSYNC           = false;
    boolean DEFAULT_RESIZABLE       = false;
    boolean DEFAULT_FULL_SCREEN     = false;
    boolean DEFAULT_USE_MONITOR_AR  = true;
    boolean DEFAULT_LIMIT_FPS       = false;

    float DEFAULT_ASPECT_RATIO      = 16/9f;
    int DEFAULT_SCREEN_WIDTH        = 1280;
    int DEFAULT_SCREEN_HEIGHT       = 720;
    int DEFAULT_TARGET_FPS          = 60;
    int DEFAULT_PPU_WORLD           = 1;
    int DEFAULT_PPU_UI              = 1;


    int targetFPS();

    int screenWidth();

    int screenHeight();

    float aspectRatio();

    int pixelPerUnitWorld();

    int pixelPerUnitUI();

    boolean monitorAspectRaEnabled();

    boolean vsyncEnabled();

    boolean resizeEnabled();

    boolean fullScreenEnabled();

    boolean limitFPSEnabled();
}
