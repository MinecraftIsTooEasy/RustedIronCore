package huix.glacier.registry.util;

public class BiDefaultedRegistry extends SimpleRegistry {
    private final String field_8395;
    private Object defaultValue;

    public BiDefaultedRegistry(String string) {
        this.field_8395 = string;
    }

    public void add(int rawId, String id, Object object) {
        if (this.field_8395.equals(id)) {
            this.defaultValue = object;
        }

        super.add(rawId, id, object);
    }

    public Object get(String string) {
        Object var2 = super.get(string);
        return var2 == null ? this.defaultValue : var2;
    }

    public Object getByRawId(int index) {
        Object var2 = super.getByRawId(index);
        return var2 == null ? this.defaultValue : var2;
    }
}
