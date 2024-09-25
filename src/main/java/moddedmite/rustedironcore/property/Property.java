package moddedmite.rustedironcore.property;

import java.util.*;

public class Property<T, VALUE> {
    private final Class<VALUE> type;
    private final String name;
    private final VALUE defaultValue;
    private final Map<T, VALUE> map = new HashMap<>();

    protected Property(String name, Class<VALUE> type, VALUE defaultValue) {
        this.name = name;
        this.type = type;
        this.defaultValue = defaultValue;
    }

    public void register(T T, VALUE value) {
        Objects.requireNonNull(value);
        this.map.put(T, value);
    }

    public boolean has(T T) {
        return this.map.containsKey(T);
    }

    public VALUE get(T T) {
        return this.map.get(T);
    }

    public VALUE getOrDefault(T T) {
        return this.map.getOrDefault(T, this.defaultValue);
    }

    public Optional<VALUE> getOptional(T T) {
        return Optional.ofNullable(this.map.get(T));
    }

    public Set<T> keySet() {
        return this.map.keySet();
    }

    public String getName() {
        return this.name;
    }

    public Class<VALUE> getType() {
        return this.type;
    }
}
