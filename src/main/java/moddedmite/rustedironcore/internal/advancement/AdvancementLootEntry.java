package moddedmite.rustedironcore.internal.advancement;

import com.google.gson.JsonObject;
import net.minecraft.ItemStack;

record AdvancementLootEntry(JsonObject definition, ItemStack item) {

}
