package nudge.core.settings;

/**
 * @author Frederik Dahl
 * 14/07/2021
 */


public final class SettFloat extends SettNum {
    
    private final float min, max;
    
    public SettFloat(String label, Float defaultValue, float min, float max) {
        super(label, defaultValue);
        this.min = min;
        this.max = max;
        type = TYPE_FLOAT;
    }
    
    public SettFloat(String label, Float defaultValue) {
        this(label, defaultValue, Float.MIN_VALUE, Float.MAX_VALUE);
    }
    
    public void set(Float newValue) { super.setNumb(newValue); }
    
    public float get() { return (Float) super.getNumb(); }
    
    @Override
    protected boolean applyCondition() { return (Float)candidate >= min && (Float)candidate <= max; }
}
