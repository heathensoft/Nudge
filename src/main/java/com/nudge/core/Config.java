package com.nudge.core;

/**
 * @author Frederik Dahl
 * 08/07/2021
 */


public interface Config {

    // CORE DISPLAY
    boolean DEF_VSYNC           = false;
    boolean DEF_RESIZABLE       = false;
    boolean DEF_FULL_SCREEN     = false;
    boolean DEF_USE_MONITOR_AR  = true;
    float DEF_ASPECT_RATIO      = 16/9f;
    int DEF_SCREEN_WIDTH        = 1280;
    int DEF_SCREEN_HEIGHT       = 720;

    // CORE APPLICATION
    boolean DEF_CPU_SLEEP       = true;
    boolean DEF_CAP_FPS         = false;
    int DEF_TARGET_FPS          = 60;
    int DEF_TARGET_UPS          = 30;


    // CORE DISPLAY

    default int screenWidth() { return 1280; }

    default int screenHeight() { return 720; }

    default float aspectRatio() { return 16/9f; }

    default boolean monitorAspectRaEnabled() { return false; }

    default boolean vsyncEnabled() { return false; }

    default boolean resizeEnabled() { return false; }

    default boolean fullScreenEnabled() { return false; }

    // CORE APP_CYCLE

    default boolean fpsCapEnabled() { return false; }

    default boolean sleepOnSync() { return true; }

    default int targetFPS() { return  60; }

    default int targetUPS() { return  30; }

    // CORE APP_AUDIO

    default float volumeMaster() { return 1f; }

    default float volumeEffects() { return 1f; }

    default float volumeAmbient() { return 1f; }

    default float volumeDialogue() { return 1f; }

    default float volumeMusic() { return 1f; }

    default boolean audioEnabled() { return false; }
}
