package huix.glacier.registry.util;

import com.google.common.collect.Maps;
import huix.glacier.util.GlacierLog;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class MutableRegistry implements Registry {
    protected final Map map = this.createMap();

    public MutableRegistry() {
    }

    protected Map createMap() {
        return Maps.newHashMap();
    }

    public Object get(Object key) {
        return this.map.get(key);
    }

    public void put(Object key, Object value) {
        if (this.map.containsKey(key)) {
            GlacierLog.debug("Adding duplicate key '" + key + "' to registry");
        }

        this.map.put(key, value);
    }

    public Set keySet() {
        return Collections.unmodifiableSet(this.map.keySet());
    }

    public boolean containsKey(Object key) {
        return this.map.containsKey(key);
    }
}