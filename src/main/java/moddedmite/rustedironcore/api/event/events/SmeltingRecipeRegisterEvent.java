package moddedmite.rustedironcore.api.event.events;

import moddedmite.rustedironcore.api.event.Handlers;
import moddedmite.rustedironcore.api.event.handler.SmeltingHandler;
import net.minecraft.FurnaceRecipes;
import net.minecraft.ItemStack;
import net.minecraft.TileEntityFurnace;

public class SmeltingRecipeRegisterEvent {
    public void register(int input_item_id, ItemStack output_item_stack) {
        FurnaceRecipes.smelting().addSmelting(input_item_id, output_item_stack);
    }

    public void registerSpecial(SmeltingHandler.SpecialRecipe specialRecipe) {
        this.registerSpecial(specialRecipe, true);
    }

    public void registerSpecial(SmeltingHandler.SpecialRecipe specialRecipe, boolean shouldCheckHeatLevel) {
        Handlers.Smelting.addSpecialRecipe((input_item_stack, heat_level) -> {
            SmeltingHandler.SmeltingResult smeltingResult = specialRecipe.getSmeltingResult(input_item_stack, heat_level);
            if (shouldCheckHeatLevel && heat_level < TileEntityFurnace.getHeatLevelRequired(input_item_stack.itemID)) {
                return null;
            }
            return smeltingResult;
        });
    }
}
