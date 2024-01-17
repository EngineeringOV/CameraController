package ventures.of.util;

import java.util.TreeMap;

public abstract class NotationMap<K,V> extends TreeMap<K,V> {
    public NotationMap() {
        super();
        setupMap();
    }

    public abstract void setupMap();
}
