package com.nudge.core.settings;

/**
 * @author Frederik Dahl
 * 13/07/2021
 */


public final class SettBool extends Setting {
    
    public SettBool(String label, Boolean defaultValue) {
        super(label, defaultValue);
        type = TYPE_BOOL;
    }
    
    public void set(Boolean newValue) { super.setObj(newValue); }
    
    public boolean get() { return (Boolean) super.getObj(); }
    
    @Override
    protected boolean applyCondition() { return true; }
}
