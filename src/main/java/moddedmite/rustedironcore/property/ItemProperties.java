package moddedmite.rustedironcore.property;

import huix.glacier.api.extension.item.IFuelItem;
import huix.glacier.api.extension.item.IFusibleItem;
import huix.glacier.api.extension.item.IRetainableItem;
import huix.glacier.api.extension.item.IRockItem;
import moddedmite.rustedironcore.api.event.Handlers;
import net.minecraft.Block;
import net.minecraft.Item;
import net.minecraft.ItemStack;

import java.util.function.UnaryOperator;

/**
 * Call them at the {@link Handlers#PropertiesRegistry}
 * <br>
 * To register blocks, use {@link Item#getItem(Block)} to cast instances.
 */
public class ItemProperties {
    /**
     * Also use {@link IRockItem}
     */
    public static final IntegerProperty<Item> RockExperience = IntegerProperty.of("RockExperience", 0);
    /**
     * Also use {@link IFuelItem}
     */
    public static final IntegerProperty<Item> HeatLevel = IntegerProperty.of("HeatLevel", 1);
    /**
     * Also use {@link IFusibleItem}
     */
    public static final IntegerProperty<Item> HeatLevelRequired = IntegerProperty.of("HeatLevelRequired", 1);
    /**
     * Also use {@link IFuelItem}
     */
    public static final IntegerProperty<Item> BurnTime = IntegerProperty.of("BurnTime", 100);
    /**
     * Also use {@link IRetainableItem}
     */
    public static final Property<Item, UnaryOperator<ItemStack>> CraftConsumeOverride = new Property<>("CraftConsumeOverride", null, x -> {
        x.splitStack(1);
        return x;
    });
}
