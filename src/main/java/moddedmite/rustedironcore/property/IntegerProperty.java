package moddedmite.rustedironcore.property;

public class IntegerProperty<T> extends Property<T, Integer> {
    protected IntegerProperty(String name, int defaultValue) {
        super(name, Integer.class, defaultValue);
    }

    public static <T> IntegerProperty<T> of(String name, int defaultValue) {
        return new IntegerProperty<>(name, defaultValue);
    }
}
