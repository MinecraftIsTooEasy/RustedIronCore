package moddedmite.rustedironcore.property;

import net.minecraft.Item;

public class ItemProperties {
    // call them at the Handlers#PropertiesRegistry
    public static final IntegerProperty<Item> RockExperience = IntegerProperty.of("RockExperience", 0);
    public static final IntegerProperty<Item> HeatLevel = IntegerProperty.of("HeatLevel", 1);
    public static final IntegerProperty<Item> HeatLevelRequired = IntegerProperty.of("HeatLevelRequired", 1);
    public static final IntegerProperty<Item> BurnTime = IntegerProperty.of("BurnTime", 100);
}
