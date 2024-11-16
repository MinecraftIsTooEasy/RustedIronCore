package moddedmite.rustedironcore.api.event.events;

import moddedmite.rustedironcore.api.event.Handlers;
import moddedmite.rustedironcore.api.event.handler.SmeltingHandler;
import net.minecraft.*;

public class SmeltingRecipeRegisterEvent {
    public void register(Item input, ItemStack output_item_stack) {
        this.register(input.itemID, output_item_stack);
    }

    public void register(Item input, Item output) {
        this.register(input.itemID, new ItemStack(output));
    }

    public void register(Item input, Block output) {
        this.register(input.itemID, new ItemStack(output));
    }

    public void register(Block input, ItemStack output_item_stack) {
        this.register(input.blockID, output_item_stack);
    }

    public void register(Block input, Item output) {
        this.register(input.blockID, new ItemStack(output));
    }

    public void register(Block input, Block output) {
        this.register(input.blockID, new ItemStack(output));
    }

    public void register(int input_item_id, ItemStack output_item_stack) {
        FurnaceRecipes.smelting().addSmelting(input_item_id, output_item_stack);
    }

    public void registerSpecial(SmeltingHandler.SpecialRecipe specialRecipe) {
        this.registerSpecial(specialRecipe, true);
    }

    public void registerSpecial(SmeltingHandler.SpecialRecipe specialRecipe, boolean shouldCheckHeatLevel) {
        Handlers.Smelting.addSpecialRecipe((input_item_stack, heat_level) -> {
            if (shouldCheckHeatLevel && heat_level < TileEntityFurnace.getHeatLevelRequired(input_item_stack.itemID)) {
                return null;
            }
            return specialRecipe.getSmeltingResult(input_item_stack, heat_level);
        });
    }
}
