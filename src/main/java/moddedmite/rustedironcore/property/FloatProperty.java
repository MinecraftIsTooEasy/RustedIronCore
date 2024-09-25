package moddedmite.rustedironcore.property;

public class FloatProperty<T> extends Property<T, Float> {

    protected FloatProperty(String name, float defaultValue) {
        super(name, Float.class, defaultValue);
    }

    public static <T> FloatProperty<T> of(String name, float defaultValue) {
        return new FloatProperty<>(name, defaultValue);
    }
}
