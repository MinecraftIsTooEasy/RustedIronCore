package moddedmite.rustedironcore.property;

import net.minecraft.Item;

public class ItemProperties {
    // call them at the Handlers#PropertiesRegistry
    public static final IntegerProperty<Item> RockExperience = IntegerProperty.of("RockExperience", 0);
}
