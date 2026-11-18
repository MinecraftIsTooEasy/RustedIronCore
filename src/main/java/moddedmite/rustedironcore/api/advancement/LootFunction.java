package moddedmite.rustedironcore.api.advancement;

import com.google.gson.JsonObject;
import net.minecraft.ItemStack;

public interface LootFunction {

    void validate(JsonObject json);

    void apply(JsonObject json, ItemStack stack, AdvancementContext context);
}
