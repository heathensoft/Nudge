package com.nudge.core.settings;

/**
 * @author Frederik Dahl
 * 15/07/2021
 */


public interface SettingListener {
    
    void onAppliedSetting(Setting newSetting);
    
    // free appropriate listeners from memory on state-change
    boolean clearOnStateChange();
}
