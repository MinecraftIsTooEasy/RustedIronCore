package moddedmite.rustedironcore.api.event.handler;

import moddedmite.rustedironcore.api.event.EventHandler;
import moddedmite.rustedironcore.api.event.events.SmeltingRecipeRegisterEvent;
import net.minecraft.ItemStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class SmeltingHandler extends EventHandler<SmeltingRecipeRegisterEvent> {
    private final List<SpecialRecipe> specialRecipes = new ArrayList<>();

    public void addSpecialRecipe(SpecialRecipe specialRecipe) {
        this.specialRecipes.add(specialRecipe);
    }

    public Optional<SmeltingResult> match(ItemStack input_item_stack, int heat_level) {
        return specialRecipes.stream()
                .map(specialRecipe -> specialRecipe.getSmeltingResult(input_item_stack, heat_level))
                .filter(Objects::nonNull)
                .filter(x -> x.result != null)
                .findAny();
    }

    public static SmeltingResult result(int consumption, ItemStack result) {
        return new SmeltingResult(consumption, result);
    }

    public static SmeltingResult result(ItemStack result) {
        return new SmeltingResult(1, result);
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
