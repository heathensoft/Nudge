package discarded;

import nudge.util.U;

/**
 * @author Frederik Dahl
 * 13/07/2021
 */


public class GSettings implements Default_1 {
    
    // FLAGS / CHANGE LOG
    
    private long changeLog = 0x0000_0000_0000_0000L;
    
    byte shiftDisplay   = 0x00;
    byte shiftApp       = 0x04;
    byte shiftAudio     = 0x08;
    
    private static final short cVsync           = 0x0001;
    private static final short cResizable       = 0x0002;
    private static final short cFullScreen      = 0x0004;
    private static final short cUseMonitorAR    = 0x0008;
    private static final short cAspectRatio     = 0x0010;
    private static final short cWindowWidth     = 0x0020;
    private static final short cWindowHeight    = 0x0040;
    
    private static final short cSleepOnSync     = 0x0001;
    private static final short cFpsCapEnabled   = 0x0002;
    private static final short cTargetFPS       = 0x0004;
    private static final short cTargetUPS       = 0x0008;
    
    private static final short cAudioEnabled    = 0x0001;
    private static final short cVolumeMaster    = 0x0002;
    private static final short cVolumeEffects   = 0x0004;
    private static final short cVolumeDialogue  = 0x0008;
    private static final short cVolumeMusic     = 0x0010;
    
    public boolean log = false;
    
    
    // CORE DISPLAY
    
    private boolean vsync = Default_1.super.vsyncEnabled();
    private boolean resizable;
    private boolean fullScreen;
    private boolean useMonitorAR;
    private float aspectRatio;
    private int windowWidth;
    private int windowHeight;
    
    private boolean vsync_tmp;
    private boolean resizable_tmp;
    private boolean fullScreen_tmp;
    private boolean useMonitorAR_tmp;
    private float aspectRatio_tmp;
    private int windowWidth_tmp;
    private int windowHeight_tmp;
    
    // CORE APP
    
    private boolean sleepOnSync;
    private boolean fpsCapEnabled;
    private int targetFPS;
    private int targetUPS;
    
    private boolean sleepOnSync_tmp;
    private boolean fpsCapEnabled_tmp;
    private int targetFPS_tmp;
    private int targetUPS_tmp;
    
    // CORE AUDIO
    
    private boolean audioEnabled;
    private float volumeMaster;
    private float volumeEffects;
    private float volumeDialogue;
    private float volumeAmbience;
    private float volumeMusic;
    
    private boolean audioEnabled_tmp;
    private float volumeMaster_tmp;
    private float volumeEffects_tmp;
    private float volumeDialogue_tmp;
    private float volumeAmbience_tmp;
    private float volumeMusic_tmp;
    
    
    
    
    public void setWindowWidth(int w) {
        windowHeight_tmp = Math.abs(w);
        if (w == windowHeight) return;
        windowWidth = Math.abs(w);
        logChange(cWindowWidth,shiftDisplay);
    }
    
    public void setWindowHeight(int h) {
        if (h == windowHeight) return;
        windowHeight = Math.abs(h);
        logChange(cWindowHeight,shiftDisplay);
    }
    
    public void setAspectRatio(float w, float h) {
        setAspectRatio(w/h);
    }
    
    public void setAspectRatio(float aspectRatio) {
        aspectRatio = Math.abs(aspectRatio);
        if (aspectRatio != this.aspectRatio)
            this.aspectRatio = aspectRatio;
            logChange(cAspectRatio,shiftDisplay);
    }
    
    public void enableMonitorAspectRa(boolean b) {
        if (b != useMonitorAR) {
            useMonitorAR = b;
            logChange(cUseMonitorAR,shiftDisplay);
        }
    }
    
    public void enableResize(boolean b) {
        if (b != resizable) {
            resizable = b;
            logChange(cResizable,shiftDisplay);
        }
    }
    
    public void enableFullScreen(boolean b) {
        if (b != fullScreen) {
            fullScreen = b;
            logChange(cFullScreen,shiftDisplay);
        }
    }
    
    public void enableVsync(boolean b) {
        if (b != vsync) {
            vsync = b;
            logChange(cVsync,shiftDisplay);
        }
    }
    
    
    public int windowWidth() { return windowWidth; }
    
    public int windowHeight() { return windowHeight; }
    
    public float aspectRatio() { return aspectRatio; }
    
    public boolean monitorAspectRaEnabled() { return useMonitorAR; }
    
    public boolean vsyncEnabled() { return vsync; }
    
    public boolean resizeEnabled() { return resizable; }
    
    public boolean fullScreenEnabled() { return fullScreen; }
    
    
    
    
    public void enableFpsCap(boolean b) {
        if (b != fpsCapEnabled) {
            fpsCapEnabled = b;
            logChange(cFpsCapEnabled,shiftApp);
        }
    }
    
    public void enableSleepOnSync(boolean b) {
        if (b != sleepOnSync) {
            sleepOnSync = b;
            logChange(cSleepOnSync,shiftApp);
        }
    }
    
    public void setTargetFPS(int value) {
        if (value == targetFPS) return;
        targetFPS = Math.max(1,value);
        logChange(cTargetFPS,shiftApp);
    }
    
    public void setTargetUPS(int value) {
        if (value == targetUPS) return;
        targetUPS = Math.max(1,value);
        logChange(cTargetUPS,shiftApp);
    }
    
    
    public boolean fpsCapEnabled() { return fpsCapEnabled; }
    
    public boolean sleepOnSync() { return sleepOnSync; }
    
    public int targetFPS() { return  targetFPS; }
    
    public int targetUPS() { return  targetUPS; }
    
    
    
    
    
    public float volumeMaster() { return volumeMaster; }
    
    public float volumeEffects() { return volumeEffects; }
    
    public float volumeAmbient() { return volumeAmbience; }
    
    public float volumeDialogue() { return volumeDialogue; }
    
    public float volumeMusic() { return volumeMusic; }
    
    public boolean audioEnabled() { return audioEnabled; }
    
    
    public void setVolumeMaster(float value) { volumeMaster = U.clamp(value,0,1f); }
    
    public void setVolumeEffects(float value) { volumeEffects = U.clamp(value,0,1f); }
    
    public void setVolumeDialogue(float value) { volumeDialogue = U.clamp(value,0,1f); }
    
    public void setVolumeAmbience(float value) { volumeAmbience = U.clamp(value,0,1f); }
    
    public void setVolumeMusic(float value) { volumeMusic = U.clamp(value,0,1f); }
    
    public void enableAudio(boolean b) { audioEnabled = b; }
    
    
    
    public void apply() { // Cancel is managed in a settings menu (maybe not)
        
        if (altered()) {
            
            if (settingsAlteredDisplay()) {
            
            
            }
    
            if (settingsAlteredApp()) {
            
            
            }
            
            if (settingsAlteredAudio()) {
                
                // I have to make audio first
            }
            
            resetChangeLog();
        }
    }
    
    public void startLogging() { log = true; }
    
    public boolean altered() { return changeLog != 0L; }
    
    public void resetChangeLog() { changeLog = 0x0000_0000_0000_0000L; }
    
    public boolean settingsAlteredDisplay() { return categoryAltered(shiftDisplay); }
    
    public boolean settingsAlteredApp() { return categoryAltered(shiftApp); }
    
    public boolean settingsAlteredAudio() { return categoryAltered(shiftAudio); }
    
    private void logChange(short setting, byte shift) {
        if (log) changeLog = U.setFlag((long) setting << shift, changeLog);
    }
    
    private boolean specificAltered(short setting, byte shift) {
        return U.flagIsSet((long) setting << shift, changeLog);
    }
    
    private boolean categoryAltered(byte shift) {
        long mask = (long) 0x0F << shift;
        return (changeLog & mask) != 0;
    }
    
}
