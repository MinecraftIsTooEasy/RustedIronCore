package huix.glacier.registry.util;

public interface Registry {
    Object get(Object key);

    void put(Object key, Object value);
}
