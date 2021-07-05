package nudge.core.input;

import static org.lwjgl.glfw.GLFW.*;

/**
 * @author Frederik Dahl
 * 29/06/2021
 */

public class Keyboard {

    private static Keyboard instance;

    private final boolean[] keys = new boolean[GLFW_KEY_LAST];

    private KeyReader reader;

    private Keyboard() {}


    public static Keyboard get() {

        if (instance == null)

            instance = new Keyboard();

        return instance;
    }

    public static void keyCallback(long window, int key, int scancode, int action, int mods) {

        if (key == GLFW_KEY_UNKNOWN) return;

        Keyboard keyboard = get();

        if (action == GLFW_PRESS) {

            keyboard.keys[key] = true;

            KeyReader reader = keyboard.reader;

            if (reader != null) {

                if (!reader.isSleeping()) {

                    if (key < 0x20 || key > 0x7E) {

                        reader.registerControl(key);
                    }
                }
            }
        }
        else if (action == GLFW_RELEASE)

            keyboard.keys[key] = false;
    }

    public static void charCallback(long window, int codepoint) {

        KeyReader reader = get().reader;

        if (reader != null) {

            if (reader.isSleeping()) return;

            if ((codepoint & 0x7F) == codepoint)

                reader.registerChar((byte)codepoint);
        }
    }

    public static boolean isPressed(int keyCode) {

        Keyboard keyboard = Keyboard.get();

        if (keyCode >= keyboard.keys.length) return false;

        return keyboard.keys[keyCode];
    }

    public void setReader(KeyReader reader) {

        boolean thisReaderNull = this.reader == null;

        boolean newReaderNull = reader == null;

        if (thisReaderNull) {

            if (newReaderNull) return;

            this.reader = reader;

            reader.signalActivate();
        }
        else {

            if (newReaderNull) {

                this.reader.signalDeactivate();

                this.reader = null;
            }
            else {

                if (this.reader.equals(reader)) return;

                this.reader.signalDeactivate();

                this.reader = reader;

                reader.signalActivate();
            }
        }
    }
}
