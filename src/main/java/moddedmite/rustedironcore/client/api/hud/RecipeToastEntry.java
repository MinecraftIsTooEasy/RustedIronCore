package moddedmite.rustedironcore.client.api.hud;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ItemStack;

@Environment(EnvType.CLIENT)
public record RecipeToastEntry(ItemStack symbol, ItemStack result) {

    public RecipeToastEntry {
        symbol = symbol.copy();
        result = result.copy();
    }
}
