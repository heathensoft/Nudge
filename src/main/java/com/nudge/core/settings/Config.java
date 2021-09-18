package com.nudge.core.settings;

/**
 * @author Frederik Dahl
 * 15/07/2021
 */


public interface Config {
    
    // CORE DISPLAY
    
    default int windowWidth() { return 1280; }
    
    default int windowHeight() { return 720; }
    
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
