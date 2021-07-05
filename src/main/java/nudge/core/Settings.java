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

    // Temp
    public static final String DEFAULT_PATH = "settings";
    public static final String FILE_EXTENSION = ".bin";

    private boolean useMonitorAspectRatio = false;
    private boolean vsyncEnabled = false;
    private boolean resizable = true;
    private boolean fullScreen = false;
    private boolean muted = false;

    private float target_AspectRatio = 16/9f;
    private int target_ResolutionWidth = 1280;
    private int target_ResolutionHeight = 720;
    private int target_fps = 60;
    private int target_ups = 30;

    private float audio_master = 1f;
    private float audio_effects = 1f;
    private float audio_dialogue = 1f;
    private float audio_ambient = 1f;
    private float audio_music = 1f;


    public Settings() {
        try {
            load();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Could not load settings.bin");
            System.out.println("Default settings enabled");
        }
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


    public void load() throws IOException {

        File settingsFile = new File(DEFAULT_PATH + FILE_EXTENSION);

        if (settingsFile.isFile()) {

            Database database = Database.deserializeFromFile(settingsFile);

            DBObject settings = database.findObject("settings");

            boolean[] boolArr = settings.findArray("bools").boolData();

            useMonitorAspectRatio = boolArr[0];
            vsyncEnabled = boolArr[1];
            resizable = boolArr[2];
            fullScreen = boolArr[3];
            muted = boolArr[4];

            target_AspectRatio = settings.findField("aspectRatio").getFloat();
            target_ResolutionWidth = settings.findField("resolutionWidth").getInt();
            target_ResolutionHeight = settings.findField("resolutionHeight").getInt();
            target_ups = settings.findField("ups").getInt();
            target_fps = settings.findField("fps").getInt();

            audio_master = settings.findField("audioMaster").getFloat();
            audio_master = settings.findField("audioEffects").getFloat();
            audio_master = settings.findField("audioDialogue").getFloat();
            audio_master = settings.findField("audioAmbient").getFloat();
            audio_master = settings.findField("audioMusic").getFloat();
        }
    }

    public void save() {

        Database database = new Database("db");
        DBObject settings = new DBObject("settings");

        DBArray bools = new DBArray("bools",new boolean[] {

                useMonitorAspectRatio,
                vsyncEnabled,
                resizable,
                fullScreen,
                muted
        });

        settings.add(bools);

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

        // todo: again look into exceptions in the serialization packet

        try {
            database.serializeToFile(DEFAULT_PATH + FILE_EXTENSION);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Could not save the application settings");
        }

    }
}
