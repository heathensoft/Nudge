package nudge.core;

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


public class Settings {

    public static final String DEFAULT_DIRECTORY;
    public static final String FILE_NAME;
    public static final String FILE_EXTENSION;

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


    private String directory;

    private boolean useMonitorAspectRatio = false;  // use monitor aspect ratio?
    private boolean vsyncEnabled = false;           // enable glfw internal v-sync?
    private boolean resizable = true;               // resizable window?
    private boolean fullScreen = false;             // fullScreen?
    private boolean muted = false;                  // mute all audio?

    private float target_AspectRatio = 16/9f;       //
    private int target_ResolutionWidth = 1280;      // screenW
    private int target_ResolutionHeight = 720;
    private int target_fps = 60;
    private int target_ups = 30;
    private int ppu_world = 1;
    private int ppu_ui = 1;

    private float audio_master = 1f;
    private float audio_effects = 1f;
    private float audio_dialogue = 1f;
    private float audio_ambient = 1f;
    private float audio_music = 1f;


    public Settings() {

        this(true);
    }

    public Settings(boolean tryLoad) {

        this(tryLoad,DEFAULT_DIRECTORY);

    }

    public Settings(boolean tryLoad, String directory) {

        setDirectory(directory);

        if (tryLoad) {
            try { load();
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Could not load: " + this.directory + FILE_NAME + FILE_EXTENSION);
                System.out.println("Default application settings enabled");
            }
        }


    }

    public void setDirectory(String path) {
        String s = File.separator;
        path = path.replace('/',s.charAt(0));
        if (!path.endsWith(s)) {
            directory = path.concat(s);
        }
        else directory = path;
    }

    public String getDirectory() {
        return directory;
    }

    public void setTarget_fps(int value) {
        target_fps = value;
    }

    public void setTarget_ups(int value) {
        target_ups = value;
    }

    public void setTarget_ResolutionHeight(int h) {
        target_ResolutionHeight = h;
    }

    public void setTarget_ResolutionWidth(int w) {
        target_ResolutionWidth = w;
    }

    public void setTarget_AspectRatio(float w, float h) {
        target_AspectRatio = w/h;
    }

    public float getAudio_master() {
        return muted ? 0 : audio_master;
    }

    public float getAudio_effects() {
        return muted ? 0 : audio_effects;
    }

    public float getAudio_ambient() {
        return muted ? 0 : audio_ambient;
    }

    public float getAudio_dialogue() {
        return muted ? 0 : audio_dialogue;
    }

    public float getAudio_music() {
        return muted ? 0 : audio_music;
    }

    public void setAudioVolumeMaster(float value) {
        audio_master = U.clamp(value,0,1f);
        audio_effects *= audio_master;
        audio_effects *= audio_master;
        audio_ambient *= audio_master;
        audio_music *= audio_master;
    }

    public void setAudioVolumeEffects(float value) {
        audio_effects = U.clamp(value,0,1f);
        audio_effects *= audio_master;
    }

    public void setAudioVolumeDialogue(float value) {
        audio_dialogue = U.clamp(value,0,1f);
        audio_dialogue *= audio_master;
    }

    public void setAudioVolumeAmbient(float value) {
        audio_ambient = U.clamp(value,0,1f);
        audio_ambient *= audio_master;
    }

    public void setAudioVolumeMusic(float value) {
        audio_music = U.clamp(value,0,1f);
        audio_music *= audio_master;
    }

    public boolean onDisk() {
        File settingsFile = new File(directory + FILE_NAME + FILE_EXTENSION);
        return settingsFile.exists();
    }

    private void load() throws IOException {

        File settingsFile = new File(directory + FILE_NAME + FILE_EXTENSION);

        if (settingsFile.exists()) {

            Database database = Database.deserializeFromFile(settingsFile);;

            DBObject settings = database.findObject("settings");

            DBArray bools = settings.findArray("booleans");

            boolean[] booleans = bools.boolData();

            useMonitorAspectRatio = booleans[0];
            vsyncEnabled = booleans[1];
            resizable = booleans[2];
            fullScreen = booleans[3];
            muted = booleans[4];

            target_AspectRatio = settings.findField("aspectRatio").getFloat();
            target_ResolutionWidth = settings.findField("resolutionWidth").getInt();
            target_ResolutionHeight = settings.findField("resolutionHeight").getInt();
            target_ups = settings.findField("ups").getInt();
            target_fps = settings.findField("fps").getInt();

            audio_master = settings.findField("audioMaster").getFloat();
            audio_effects = settings.findField("audioEffects").getFloat();
            audio_dialogue = settings.findField("audioDialogue").getFloat();
            audio_ambient = settings.findField("audioAmbient").getFloat();
            audio_music = settings.findField("audioMusic").getFloat();
        }
    }

    public void save() {

        File directory = new File(this.directory);

        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                System.out.println("Failed to save the application settings. Could not create directory");
                return;
            }
        }

        Database database = new Database("db");
        DBObject settings = new DBObject("settings");

        DBArray booleans = new DBArray("booleans",new boolean[] {

                useMonitorAspectRatio,
                vsyncEnabled,
                resizable,
                fullScreen,
                muted
        });

        settings.add(booleans);

        DBField aspectRatio = new DBField("aspectRatio",target_AspectRatio);
        DBField resolutionWidth = new DBField("resolutionWidth",target_ResolutionWidth);
        DBField resolutionHeight = new DBField("resolutionHeight",target_ResolutionHeight);
        DBField ups = new DBField("ups",target_ups);
        DBField fps = new DBField("fps",target_fps);

        settings.add(aspectRatio);
        settings.add(resolutionWidth);
        settings.add(resolutionHeight);
        settings.add(ups);
        settings.add(fps);

        DBField audioMaster = new DBField("audioMaster",audio_master);
        DBField audioEffects = new DBField("audioEffects",audio_effects);
        DBField audioDialogue = new DBField("audioDialogue",audio_dialogue);
        DBField audioAmbient = new DBField("audioAmbient",audio_ambient);
        DBField audioMusic = new DBField("audioMusic",audio_music);

        settings.add(audioMaster);
        settings.add(audioEffects);
        settings.add(audioDialogue);
        settings.add(audioAmbient);
        settings.add(audioMusic);

        database.add(settings);

        // todo: again, look into exceptions in the serialization packet

        try {
            database.serializeToFile(this.directory + FILE_NAME + FILE_EXTENSION);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to save the application settings. Could not read Database");
        }

    }
}
