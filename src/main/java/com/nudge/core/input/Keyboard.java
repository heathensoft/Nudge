package com.nudge.core.input;

import com.nudge.core.glfwEvents.CharCallback;
import com.nudge.core.glfwEvents.KeyCallback;

import static org.lwjgl.glfw.GLFW.*;

/**
 * @author Frederik Dahl
 * 29/06/2021
 */

public class Keyboard {
    
    private final boolean[] keys;
    private final boolean[] pkeys;
    private final CharCallback charCallback;
    private final KeyCallback keyCallback;
    private KeyReader reader;
    private int count;
    
    
    public Keyboard() {
        keys = new boolean[GLFW_KEY_LAST];
        pkeys = new boolean[GLFW_KEY_LAST];
        charCallback = new CharCallback();
        keyCallback = new KeyCallback();
        count = 0;
    }
    
    public void step() {
    
        if (count > 0) {
        
            System.arraycopy(keys, 0, pkeys, 0, GLFW_KEY_LAST);
        
            count--;
        }
        
        if (charCallback.eventQueued()) {
    
            if (reader != null) {
        
                if (!reader.isSleeping()) {
    
                    int codepoint = charCallback.getCodepoint();
    
                    if ((codepoint & 0x7F) == codepoint) // should there be a 'not' here?
        
                        reader.registerChar((byte)codepoint);
                }
            }
            charCallback.reset();
        }
        
        if (keyCallback.eventQueued()) {
            
            int key = keyCallback.getKey();
    
            if (key != GLFW_KEY_UNKNOWN) {
    
                int action = keyCallback.getAction();
    
                if (action == GLFW_PRESS) {
        
                    keys[key] = true;
        
                    if (reader != null) {
            
                        if (!reader.isSleeping()) {
                
                            if (key < 0x20 || key > 0x7E) {
                    
                                reader.registerControl(key);
                            }
                        }
                    }
                }
                else if (action == GLFW_RELEASE)
        
                    keys[key] = false;
    
                count = 1;
            }
        }
    }

    public boolean hold(int keyCode) {
        
        if (keyCode >= keys.length) return false;

        return keys[keyCode];
    }

    public boolean pressed(int keyCode) {
        
        if (keyCode >= keys.length) return false;
        
        return keys[keyCode] && !pkeys[keyCode];
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
    
    public CharCallback getCharCallback() { return charCallback; }
    
    public KeyCallback getKeyCallback() { return keyCallback; }
    
}
