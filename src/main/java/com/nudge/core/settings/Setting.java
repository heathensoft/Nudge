package com.nudge.core.settings;

/**
 * @author Frederik Dahl
 * 13/07/2021
 */


abstract class Setting {
    
    // For serialization purposes (else use "instanceof" or isBool() etc.)
    public static final byte TYPE_NULL  = 0;
    public static final byte TYPE_BOOL  = 1;
    public static final byte TYPE_INT   = 2;
    public static final byte TYPE_FLOAT = 3;
    
    protected byte type = TYPE_NULL;
    
    protected final String label;
    protected final Object defaultValue;
    protected Object current;
    protected Object candidate;
    
    
    
    public Setting(String label, Object defaultValue) {
        this.defaultValue = defaultValue;
        this.candidate = defaultValue;
        this.current = defaultValue;
        this.label = label;
    }
    
    protected abstract boolean applyCondition();
    
    protected final void setObj(Object newValue) {
        if (!newValue.equals(candidate))
        candidate = newValue;
    }
    
    /**
     * updates the current setting if applicable. cancels if not (sets candidate = current)
     * Useful to check before changing anything like fps, resolution etc.
     * example: if(apply()) changeSomething();
     *
     * @return returns true if altered AND the apply condition met.
     */
    public final boolean apply() {
        if (candidate == current) return false;
        if (!applyCondition()) {
            cancel();
            return false;
        }
        current = candidate;
        return true;
    }
    
    public final String label() { return label; }
    
    public final void cancel() { candidate = current; }
    
    public final void restore() { candidate = current = defaultValue; }
    
    public final boolean altered() { return !candidate.equals(current); }
    
    protected final Object getObj() { return current; }
    
    
    public final boolean isInt() { return current instanceof Integer; }
    
    public final boolean isFloat() { return current instanceof Float; }
    
    public final boolean isNumber() { return current instanceof Number; }
    
    public final boolean isBool() { return current instanceof Boolean; }
    
}
