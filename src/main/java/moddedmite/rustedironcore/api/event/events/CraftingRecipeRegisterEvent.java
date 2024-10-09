package moddedmite.rustedironcore.api.event.events;

import moddedmite.rustedironcore.api.event.Handlers;
import moddedmite.rustedironcore.api.interfaces.IRecipeExtend;
import net.minecraft.*;

import java.util.ArrayList;
import java.util.List;

public class CraftingRecipeRegisterEvent {
    private final List<RecipeArgs> shaped = new ArrayList<>();
    private final List<RecipeArgs> shapeless = new ArrayList<>();

    public List<RecipeArgs> getShaped() {
        return this.shaped;
    }

    public List<RecipeArgs> getShapeless() {
        return this.shapeless;
    }

    public RecipeArgs registerShapedRecipe(ItemStack result, boolean include_in_lowest_crafting_difficulty_determination, Object... inputs) {
        RecipeArgs recipeArgs = new RecipeArgs(result, include_in_lowest_crafting_difficulty_determination, inputs);
        this.shaped.add(recipeArgs);
        return recipeArgs;
    }

    public RecipeArgs registerShapelessRecipe(ItemStack result, boolean include_in_lowest_crafting_difficulty_determination, Object... inputs) {
        RecipeArgs recipeArgs = new RecipeArgs(result, include_in_lowest_crafting_difficulty_determination, inputs);
        this.shapeless.add(recipeArgs);
        return recipeArgs;
    }

    public void registerArmorRepairRecipe(Item repairItem, Material materialForArmor) {
        Handlers.Crafting.registerArmorRepairRecipe(repairItem, materialForArmor);
    }


    public static class RecipeArgs {
        public final ItemStack result;

        public final Object[] inputs;

        public final boolean include_in_lowest_crafting_difficulty_determination;

        private boolean extendsNBT;

        private Float difficulty = null;

        private int[] skillsets;

        private boolean allowDamaged;

        private RecipeArgs(ItemStack result, boolean include_in_lowest_crafting_difficulty_determination, Object... inputs) {
            this.result = result;
            this.inputs = inputs;
            this.include_in_lowest_crafting_difficulty_determination = include_in_lowest_crafting_difficulty_determination;
        }

        public RecipeArgs extendsNBT() {
            this.extendsNBT = true;
            return this;
        }

        public RecipeArgs difficulty(float difficulty) {
            this.difficulty = difficulty;
            return this;
        }

        public RecipeArgs skillSet(int skillSet) {
            this.skillsets = skillSet == 0 ? null : new int[]{skillSet};
            return this;
        }

        public RecipeArgs skillSet(int[] skillSet) {
            this.skillsets = skillSet;
            return this;
        }

        public RecipeArgs allowDamaged() {
            this.allowDamaged = true;
            return this;
        }

        public void modifyRecipe(ShapedRecipes shapedRecipes) {
            if (this.extendsNBT) shapedRecipes.func_92100_c();
            if (this.difficulty != null) shapedRecipes.setDifficulty(this.difficulty);
            shapedRecipes.setSkillsets(this.skillsets);
            if (this.allowDamaged) ((IRecipeExtend) shapedRecipes).ric$SetAllowDamaged(true);
        }

        public void modifyRecipe(ShapelessRecipes shapelessRecipes) {
            if (this.extendsNBT) shapelessRecipes.propagateTagCompound();
            if (this.difficulty != null) shapelessRecipes.setDifficulty(this.difficulty);
            shapelessRecipes.setSkillsets(this.skillsets);
            if (this.allowDamaged) ((IRecipeExtend) shapelessRecipes).ric$SetAllowDamaged(true);
        }
    }


}
