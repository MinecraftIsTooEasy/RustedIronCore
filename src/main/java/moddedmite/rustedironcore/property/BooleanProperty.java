package moddedmite.rustedironcore.property;

public class BooleanProperty<T> extends Property<T, Boolean> {
    protected BooleanProperty(String name, boolean defaultValue) {
        super(name, Boolean.class, defaultValue);
    }

    public static <T> BooleanProperty<T> of(String name, boolean defaultValue) {
        return new BooleanProperty<>(name, defaultValue);
    }
}
