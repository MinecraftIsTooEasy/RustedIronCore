package moddedmite.rustedironcore.villager;

import net.minecraft.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class VillagerSettings {
    private final int profession;
    private final ResourceLocation texture;
    private final List<RecipeEntry> recipeEntries = new ArrayList<>();
    private boolean banned = false;

    public static final ResourceLocation FarmerTexture = new ResourceLocation("textures/entity/villager/farmer");
    public static final ResourceLocation LibrarianTexture = new ResourceLocation("textures/entity/villager/librarian");
    public static final ResourceLocation PriestTexture = new ResourceLocation("textures/entity/villager/priest");
    public static final ResourceLocation SmithTexture = new ResourceLocation("textures/entity/villager/smith");
    public static final ResourceLocation ButcherTexture = new ResourceLocation("textures/entity/villager/butcher");

    public VillagerSettings(int profession, ResourceLocation texture) {
        this.profession = profession;
        this.texture = texture;
    }

    public int getProfession() {
        return this.profession;
    }

    public ResourceLocation getTexture() {
        return this.texture;
    }

    public List<RecipeEntry> getRecipeEntries() {
        return this.recipeEntries;
    }

    // let the villager buy items from players and offer emerald to players
    public VillagerSettings buyEntry(int id, float originalProbability) {
        this.recipeEntries.add(new BuyEntry(id, originalProbability));
        return this;
    }

    // let the villager get emerald from players and reward items to players
    public VillagerSettings sellEntry(int id, float originalProbability) {
        this.recipeEntries.add(new SellEntry(id, originalProbability));
        return this;
    }

    public VillagerSettings addEntry(RecipeEntry recipe) {
        this.recipeEntries.add(recipe);
        return this;
    }

    public void removeEntry(RecipeEntry recipe) {
        this.recipeEntries.remove(recipe);
    }

    public void removeBuyEntryForId(int id) {
        this.recipeEntries.removeIf(recipeEntry -> recipeEntry instanceof BuyEntry buy && buy.id() == id);
    }

    public void removeSellEntryForId(int id) {
        this.recipeEntries.removeIf(recipeEntry -> recipeEntry instanceof SellEntry sellEntry && sellEntry.id() == id);
    }

    public void setBanned(boolean banned) {
        this.banned = banned;
    }

    public boolean isBanned() {
        return this.banned;
    }
}
