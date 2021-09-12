package nudge.core.settings;

/**
 * @author Frederik Dahl
 * 13/07/2021
 */


public abstract class SettNum extends Setting {
    
    public SettNum(String label, Number defaultValue) { super(label, defaultValue); }
    
    protected final void setNumb(Number newValue) { super.setObj(newValue); }
    
    protected final Number getNumb() { return (Number) super.getObj(); }
    
}
