package moddedmite.rustedironcore.property;

import net.minecraft.Item;

public class ItemProperty<T> extends Property<T, Item> {

    protected ItemProperty(String name, Item defaultValue) {
        super(name, Item.class, defaultValue);
    }

    public static <T> ItemProperty<T> of(String name, Item defaultValue) {
        return new ItemProperty<>(name, defaultValue);
    }
}
