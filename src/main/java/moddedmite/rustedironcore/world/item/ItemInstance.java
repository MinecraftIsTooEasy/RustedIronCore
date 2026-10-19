package moddedmite.rustedironcore.world.item;

import moddedmite.rustedironcore.core.TypedInstance;
import net.minecraft.Item;

public interface ItemInstance extends TypedInstance<Item> {
    String FIELD_ID = "id";
    String FIELD_COUNT = "count";
    String FIELD_COMPONENTS = "components";

    int count();

    int getMaxStackSize();
}
