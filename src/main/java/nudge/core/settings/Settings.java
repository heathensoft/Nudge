package nudge.core.settings;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.function.Consumer;

/**
 * @author Frederik Dahl
 * 15/07/2021
 */


public abstract class Settings implements Iterable<Setting>{
    
    protected final HashMap<String,Setting> settings = new HashMap<>();
    
    public void put(Setting setting) { settings.put(setting.label(),setting); }
    
    public void put(Collection<Setting> settings) { for (Setting setting : settings) put(setting); }
    
    public Setting find(String label) { return settings.get(label);}
    
    @Override
    public Iterator<Setting> iterator() { return settings.values().iterator(); }
    
    @Override
    public void forEach(Consumer<? super Setting> action) { Iterable.super.forEach(action); }
}
