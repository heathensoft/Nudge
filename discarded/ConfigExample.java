package discarded;

import nudge.core.Config;
import nudge.io.serialization.database.DBArray;
import nudge.io.serialization.database.DBField;
import nudge.io.serialization.database.DBObject;
import nudge.io.serialization.database.Database;
import nudge.util.U;

import java.io.File;
import java.io.IOException;

/**
 * @author Frederik Dahl
 * 05/07/2021
 */


public class ConfigExample implements Config {


    private final String KEY_BOOL           = "B";
    private final String KEY_OBJ            = "O";

    //private final String KEY_MUTED          = "Mu";
    //private final String KEY_VSYNC          = "VS";
    //private final String KEY_RESIZE         = "Rs";
    //private final String KEY_FULL_SCREEN    = "Fs";
    //private final String KEY_LIMIT_FPS      = "LFPS";
    //private final String KEY_USE_MONITOR_AR = "MAR";

    private final String KEY_ASPECT_RATIO   = "AR";
    private final String KEY_SCREEN_WIDTH   = "SW";
    private final String KEY_SCREEN_HEIGHT  = "SH";
    private final String KEY_TARGET_FPS     = "TFPS";
    private final String KEY_PPU_WORLD      = "PPUW";
    private final String KEY_PPU_UI         = "PPUI";
    private final String KEY_V_MASTER       = "VMa";
    private final String KEY_V_EFFECTS      = "VEf";
    private final String KEY_V_DIALOGUE     = "VDi";
    private final String KEY_V_AMBIENCE     = "VAm";
    private final String KEY_V_MUSIC        = "VMu";

    static {

        StringBuilder sb;
        String s = File.separator;
        String home = System.getProperty("user.home");
        sb = new StringBuilder(home).append(s).append("Documents").append(s);
        sb.append("NudgeGames").append(s).append("Temp").append(s).append("Settings").append(s);

        DEFAULT_DIRECTORY = sb.toString();
        FILE_NAME = "settings";
        FILE_EXTENSION = ".bin";
    }

    public static final String DEFAULT_DIRECTORY;
    public static final String FILE_NAME;
    public static final String FILE_EXTENSION;

    private String directory;

    // DISPLAY
    private boolean vsync           = DEF_VSYNC;
    private boolean resizable       = DEF_RESIZABLE;
    private boolean fullScreen      = DEF_FULL_SCREEN;
    private boolean useMonitorAR    = DEF_USE_MONITOR_AR;
    private boolean limitFPS        = DEF_CAP_FPS;

    private float aspectRatio       = DEF_ASPECT_RATIO;
    private int screenWidth         = DEF_SCREEN_WIDTH;
    private int screenHeight        = DEF_SCREEN_HEIGHT;
    private int targetFPS           = DEF_TARGET_FPS;

    // AUDIO
    private boolean muted           = true;

    private float volumeMaster      = 1f;
    private float volumeEffects     = 1f;
    private float volumeDialogue    = 1f;
    private float volumeAmbience    = 1f;
    private float volumeMusic       = 1f;


    public ConfigExample() {this(DEFAULT_DIRECTORY); }

    public ConfigExample(String directory) { setDirectory(directory); }


    // todo: check if path is "valid" (legal chars)
    public void setDirectory(String path) {
        if (path != null) {
            String s = File.separator;
            path = path.replace('/',s.charAt(0));
            if (!path.endsWith(s)) {
                directory = path.concat(s);
            }
            else directory = path;
        }
    }

    public String getDirectory() { return directory; }

    @Override
    public int targetFPS() { return targetFPS; }

    @Override
    public int screenWidth() { return screenWidth; }

    @Override
    public int screenHeight() { return screenHeight; }

    @Override
    public float aspectRatio() { return aspectRatio; }

    @Override
    public boolean monitorAspectRaEnabled() { return useMonitorAR; }

    @Override
    public boolean vsyncEnabled() { return vsync; }

    @Override
    public boolean resizeEnabled() { return resizable; }

    @Override
    public boolean fullScreenEnabled() { return fullScreen; }

    @Override
    public boolean fpsCapEnabled() { return limitFPS; }
    

    public void setTargetFPS(int value) { targetFPS = value; }

    public void setScreenHeight(int h) { screenHeight = h; }

    public void setScreenWidth(int w) { screenWidth = w; }

    public void setAspectRatio(float w, float h) { aspectRatio = w/h; }

    public void enableMonitorAspectRa(boolean b) { useMonitorAR = b; }

    public void enableVsync(boolean b) { vsync = b; }

    public void enableResize(boolean b) { resizable = b; }

    public void enableFullScreen(boolean b) { fullScreen = b; }

    public void enableFPSLimit(boolean b) { limitFPS = b; }


    public float volumeMaster() { return volumeMaster; }

    public float volumeEffects() { return volumeEffects; }

    public float volumeAmbient() { return volumeAmbience; }

    public float volumeDialogue() { return volumeDialogue; }

    public float volumeMusic() { return volumeMusic; }

    public boolean audioEnabled() { return muted; }


    public void setVolumeMaster(float value) { volumeMaster = U.clamp(value,0,1f); }

    public void setVolumeEffects(float value) { volumeEffects = U.clamp(value,0,1f); }

    public void setVolumeDialogue(float value) { volumeDialogue = U.clamp(value,0,1f); }

    public void setVolumeAmbience(float value) { volumeAmbience = U.clamp(value,0,1f); }

    public void setVolumeMusic(float value) { volumeMusic = U.clamp(value,0,1f); }

    public void enableAudio(boolean b) { muted = b; }


    public boolean onDisk() { return new File(directory + FILE_NAME + FILE_EXTENSION).exists(); }


    // todo: rethink exception handling
    public void load() throws IOException {

        File settingsFile = new File(directory + FILE_NAME + FILE_EXTENSION);

        if (settingsFile.exists()) {

            Database database = Database.deserializeFromFile(settingsFile);
            DBObject settings = database.findObject(KEY_OBJ);

            DBArray dbools = settings.findArray(KEY_BOOL);
            boolean[] bools = dbools.boolData();

            useMonitorAR    = bools[0];
            vsync           = bools[1];
            resizable       = bools[2];
            fullScreen      = bools[3];
            muted           = bools[4];
            limitFPS        = bools[5];

            aspectRatio     = settings.findField(KEY_ASPECT_RATIO).getFloat();
            screenWidth     = settings.findField(KEY_SCREEN_WIDTH).getInt();
            screenHeight    = settings.findField(KEY_SCREEN_HEIGHT).getInt();
            targetFPS       = settings.findField(KEY_TARGET_FPS).getInt();

            volumeMaster    = settings.findField(KEY_V_MASTER).getFloat();
            volumeEffects   = settings.findField(KEY_V_EFFECTS).getFloat();
            volumeDialogue  = settings.findField(KEY_V_DIALOGUE).getFloat();
            volumeAmbience  = settings.findField(KEY_V_AMBIENCE).getFloat();
            volumeMusic     = settings.findField(KEY_V_MUSIC).getFloat();

        }
        System.out.println("Could not load from: " + this.directory + FILE_NAME + FILE_EXTENSION);
        System.out.println("\tNo such file..\n\tDefault Application Settings Enabled.");
    }

    public void save() {

        File directory = new File(this.directory);

        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                System.out.println("Failed to save the application settings. Could not create directory");
                return;
            }
        }

        Database database = new Database("DB");
        DBObject settings = new DBObject(KEY_OBJ);

        DBArray booleans = new DBArray(KEY_BOOL,new boolean[] {

                useMonitorAR,
                vsync,
                resizable,
                fullScreen,
                muted,
                limitFPS
        });

        settings.add(booleans);

        DBField aspectR          = new DBField(KEY_ASPECT_RATIO, aspectRatio);
        DBField width            = new DBField(KEY_SCREEN_WIDTH, screenWidth);
        DBField height           = new DBField(KEY_SCREEN_HEIGHT, screenHeight);
        DBField fps              = new DBField(KEY_TARGET_FPS, targetFPS);

        DBField audioMaster      = new DBField(KEY_V_MASTER, volumeMaster);
        DBField audioEffects     = new DBField(KEY_V_EFFECTS, volumeEffects);
        DBField audioDialogue    = new DBField(KEY_V_DIALOGUE, volumeDialogue);
        DBField audioAmbient     = new DBField(KEY_V_AMBIENCE, volumeAmbience);
        DBField audioMusic       = new DBField(KEY_V_MUSIC, volumeMusic);

        settings.add(aspectR);
        settings.add(width);
        settings.add(height);
        settings.add(fps);

        settings.add(audioMaster);
        settings.add(audioEffects);
        settings.add(audioDialogue);
        settings.add(audioAmbient);
        settings.add(audioMusic);

        database.add(settings);

        try {
            database.serializeToFile(this.directory + FILE_NAME + FILE_EXTENSION);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to save the application settings. Could not read Database");
        }
    }
}
