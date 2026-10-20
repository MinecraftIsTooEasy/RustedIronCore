package moddedmite.rustedironcore.world.item.crafting;

import net.minecraft.*;

public class ShapelessRecipeExtension extends NormalCraftingRecipe {
    @Override
    public boolean matches(InventoryCrafting inv, World world) {
        return false;
    }

    @Override
    public CraftingResult getCraftingResult(InventoryCrafting inv) {
        return null;
    }

    @Override
    public int getRecipeSize() {
        return 0;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return null;
    }

    @Override
    public ItemStack[] getComponents() {
        return new ItemStack[0];
    }
}
