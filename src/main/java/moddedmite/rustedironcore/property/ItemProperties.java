package moddedmite.rustedironcore.property;

import net.minecraft.Item;
import net.minecraft.ItemStack;

import java.util.function.UnaryOperator;

public class ItemProperties {
    // call them at the Handlers#PropertiesRegistry
    public static final IntegerProperty<Item> RockExperience = IntegerProperty.of("RockExperience", 0);
    public static final IntegerProperty<Item> HeatLevel = IntegerProperty.of("HeatLevel", 1);
    @Deprecated(since = "1.3.5")
    public static final IntegerProperty<Item> HeatLevelRequired = IntegerProperty.of("HeatLevelRequired", 1);
    public static final IntegerProperty<Item> BurnTime = IntegerProperty.of("BurnTime", 100);
    public static final Property<Item, UnaryOperator<ItemStack>> CraftConsumeOverride = new Property<>("CraftConsumeOverride", null, x -> {
        x.splitStack(1);
        return x;
    });
}
