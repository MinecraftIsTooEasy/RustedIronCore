package moddedmite.rustedironcore.world.item.crafting;

import net.minecraft.CraftingResult;
import net.minecraft.InventoryCrafting;
import net.minecraft.ItemStack;
import net.minecraft.World;

public class ShapedRecipeExtension extends NormalCraftingRecipe {

    @Override
    public boolean matches(InventoryCrafting inv, World world) {
//        for (int x = 0; x <= inv.getWidth() - width; x++) {
//            for (int y = 0; y <= inv.getHeight() - height; ++y) {
//                if (checkMatch(inv, x, y, false)) {
//                    return true;
//                }
//
//                if (mirrored && checkMatch(inv, x, y, true)) {
//                    return true;
//                }
//            }
//        }

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
