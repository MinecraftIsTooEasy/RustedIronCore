package moddedmite.rustedironcore.api.register;

import net.minecraft.ItemStack;
import net.minecraft.TileEntityFurnace;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class SmeltingSpecial {

    public static List<SpecialRecipe> specialRecipes = new ArrayList<>();

    public static void registerSpecialRecipe(SpecialRecipe specialRecipe) {
        registerSpecialRecipe(specialRecipe, true);
    }

    public static void registerSpecialRecipe(SpecialRecipe specialRecipe, boolean shouldCheckHeatLevel) {
        specialRecipes.add((input_item_stack, heat_level) -> {
            SmeltingResult smeltingResult = specialRecipe.getSmeltingResult(input_item_stack, heat_level);
            return heat_level < TileEntityFurnace.getHeatLevelRequired(input_item_stack.itemID) ? null : smeltingResult;
        });
    }

    public static Optional<SmeltingResult> match(ItemStack input_item_stack, int heat_level) {
        return specialRecipes.stream()
                .map(specialRecipe -> specialRecipe.getSmeltingResult(input_item_stack, heat_level))
                .filter(Objects::nonNull)
                .filter(x -> x.result != null)
                .findAny();
    }

    @FunctionalInterface
    public interface SpecialRecipe {
        @Nullable
        SmeltingResult getSmeltingResult(ItemStack input_item_stack, int heat_level);
    }

    public record SmeltingResult(int consumption, ItemStack result) {
        public SmeltingResult(ItemStack result) {
            this(1, result);
        }
    }
}
