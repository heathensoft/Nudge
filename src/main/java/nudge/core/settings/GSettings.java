package nudge.core.settings;

/**
 * @author Frederik Dahl
 * 14/07/2021
 */


public abstract class GSettings extends Settings implements Config {
    
    public static final String WINDOW_WIDTH     = "WiWi";
    public static final String WINDOW_HEIGHT    = "WiHe";
    public static final String ASPECT_RATIO     = "AsRa";
    public static final String USE_MONITOR_AR   = "UMAR";
    public static final String VSYNC            = "VSyn";
    public static final String RESIZABLE        = "ReSi";
    public static final String FULL_SCREEN      = "FuSc";
    
    public static final String FPS_CAP          = "FPSC";
    public static final String SLEEP_ON_SYNC    = "SlOS";
    public static final String TARGET_FPS       = "TFPS";
    public static final String TARGET_UPS       = "TUPS";
    
    public static final String VOLUME_MASTER    = "VoMa";
    public static final String VOLUME_EFFECTS   = "VoEf";
    public static final String VOLUME_AMBIENCE  = "VoAm";
    public static final String VOLUME_DIALOGUE  = "VoDi";
    public static final String VOLUME_MUSIC     = "VoMu";
    public static final String AUDIO_ENABLED    = "AuEn";
    
    
    public <T extends Config> GSettings(T config) {
        
        put(new SettInt(WINDOW_WIDTH, config.windowWidth(),0,Integer.MAX_VALUE));
        put(new SettInt(WINDOW_HEIGHT, config.windowHeight(),0,Integer.MAX_VALUE));
        put(new SettFloat(ASPECT_RATIO, config.aspectRatio(),0,Float.MAX_VALUE));
        put(new SettBool(USE_MONITOR_AR,config.monitorAspectRaEnabled()));
        put(new SettBool(VSYNC,config.vsyncEnabled()));
        put(new SettBool(RESIZABLE,config.resizeEnabled()));
        put(new SettBool(FULL_SCREEN,config.fullScreenEnabled()));
    
        put(new SettBool(FPS_CAP,config.fpsCapEnabled()));
        put(new SettBool(SLEEP_ON_SYNC,config.sleepOnSync()));
        put(new SettInt(TARGET_FPS, config.targetFPS(),1,Integer.MAX_VALUE));
        put(new SettInt(TARGET_UPS, config.targetUPS(),1,Integer.MAX_VALUE));
    
        put(new SettFloat(VOLUME_MASTER, config.volumeMaster(),0,1));
        put(new SettFloat(VOLUME_EFFECTS, config.volumeEffects(),0,1));
        put(new SettFloat(VOLUME_AMBIENCE, config.volumeAmbient(),0,1));
        put(new SettFloat(VOLUME_DIALOGUE, config.volumeDialogue(),0,1));
        put(new SettFloat(VOLUME_MUSIC, config.volumeMusic(),0,1));
        put(new SettBool(AUDIO_ENABLED,config.audioEnabled()));
    }
    
    public GSettings() {
        
        put(new SettInt(WINDOW_WIDTH, Config.super.windowWidth(),0,Integer.MAX_VALUE));
        put(new SettInt(WINDOW_HEIGHT, Config.super.windowHeight(),0,Integer.MAX_VALUE));
        put(new SettFloat(ASPECT_RATIO, Config.super.aspectRatio(),0,Float.MAX_VALUE));
        put(new SettBool(USE_MONITOR_AR,Config.super.monitorAspectRaEnabled()));
        put(new SettBool(VSYNC,Config.super.vsyncEnabled()));
        put(new SettBool(RESIZABLE,Config.super.resizeEnabled()));
        put(new SettBool(FULL_SCREEN,Config.super.fullScreenEnabled()));
    
        put(new SettBool(FPS_CAP,Config.super.fpsCapEnabled()));
        put(new SettBool(SLEEP_ON_SYNC,Config.super.sleepOnSync()));
        put(new SettInt(TARGET_FPS, Config.super.targetFPS(),1,Integer.MAX_VALUE));
        put(new SettInt(TARGET_UPS, Config.super.targetUPS(),1,Integer.MAX_VALUE));
    
        put(new SettFloat(VOLUME_MASTER, Config.super.volumeMaster(),0,1));
        put(new SettFloat(VOLUME_EFFECTS, Config.super.volumeEffects(),0,1));
        put(new SettFloat(VOLUME_AMBIENCE, Config.super.volumeAmbient(),0,1));
        put(new SettFloat(VOLUME_DIALOGUE, Config.super.volumeDialogue(),0,1));
        put(new SettFloat(VOLUME_MUSIC, Config.super.volumeMusic(),0,1));
        put(new SettBool(AUDIO_ENABLED,Config.super.audioEnabled()));
    }
    
    
}
