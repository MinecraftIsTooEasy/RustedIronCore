package moddedmite.rustedironcore.api.advancement;

import com.google.gson.JsonElement;
import net.minecraft.ItemStack;

/** A component adapter usable both as an exact predicate and when creating display/reward stacks. */
public interface ItemComponent extends JsonPredicate<ItemStack> {

    void apply(JsonElement value, ItemStack stack);
}
