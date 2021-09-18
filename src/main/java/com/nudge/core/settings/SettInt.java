package com.nudge.core.settings;

import java.util.Arrays;

/**
 * @author Frederik Dahl
 * 14/07/2021
 */


public final class SettInt extends SettNum {
    
    private final int min, max;
    private int[] values;
    
    public SettInt(String label, Integer defaultValue, int ... discrete) {
        super(label, defaultValue);
        values = discrete;
        Arrays.sort(values);
        this.min = values[0];
        this.max = values[values.length - 1];
        type = TYPE_INT;
    }
    
    public SettInt(String label, Integer defaultValue, int min, int max) {
        super(label, defaultValue);
        this.min = min;
        this.max = max;
        type = TYPE_INT;
    }
    
    public SettInt(String label, Integer defaultValue) {
        this(label, defaultValue, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }
    
    public void set(Integer newValue) { super.setNumb(newValue); }
    
    public int get() { return (Integer) super.getNumb(); }
    
    @Override
    protected boolean applyCondition() {
        
        if (values == null)
            
            return (Integer)candidate >= min && (Integer)candidate <= max;
        
        int candidate = (Integer) this.candidate;
        
        for (int value : values)
            
            if (value == candidate) return true;
        
        return false;
    }
}

