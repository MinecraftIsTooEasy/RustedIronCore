package moddedmite.rustedironcore.api.advancement;

import moddedmite.rustedironcore.internal.advancement.AdvancementJson;
import moddedmite.rustedironcore.internal.advancement.AdvancementNetwork;
import net.minecraft.EntityPlayer;
import net.minecraft.IRecipe;
import net.minecraft.ItemStack;
import net.minecraft.ResourceLocation;
import net.minecraft.ServerPlayer;

import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/** Binds modern recipe IDs to actual legacy recipe instances; never identifies recipes by output. */
public class AdvancementRecipes {

    private static final Map<IRecipe, ResourceLocation> IDS = new IdentityHashMap<>();
    private static final Map<String, IRecipe> RECIPES = new LinkedHashMap<>();

    public static void register(ResourceLocation id, IRecipe recipe) {
        String key = AdvancementJson.id(id.toString()).toString();
        if (RECIPES.containsKey(key) || IDS.containsKey(recipe)) throw new IllegalArgumentException("Duplicate advancement recipe: " + id);
        RECIPES.put(key, recipe);
        IDS.put(recipe, id);
    }

    public static ResourceLocation getId(IRecipe recipe) {
        return IDS.get(recipe);
    }

    public static boolean contains(ResourceLocation id) {
        return RECIPES.containsKey(id.toString());
    }

    public static boolean unlock(EntityPlayer player, ResourceLocation id) {
        if (!contains(id)) throw new IllegalArgumentException("Unregistered recipe: " + id);
        if (!(player instanceof ServerPlayer serverPlayer) || !Advancements.getProgress(player).unlockRecipe(id)) return false;
        AdvancementNetwork.sendRecipe(serverPlayer, id);
        CriteriaTriggers.recipeUnlocked(player, id);
        return true;
    }

    public static ItemStack getResult(ResourceLocation id) {
        IRecipe recipe = RECIPES.get(id.toString());
        ItemStack output = recipe == null ? null : recipe.getRecipeOutput();
        return output == null ? null : output.copy();
    }

    public static boolean isUnlocked(EntityPlayer player, ResourceLocation id) {
        return Advancements.getProgress(player).hasRecipe(id);
    }
}
