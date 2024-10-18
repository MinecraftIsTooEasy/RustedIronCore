package moddedmite.rustedironcore.villager;

import moddedmite.rustedironcore.api.util.StringUtil;
import net.minecraft.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class VillagerSettings {
    private final int profession;
    private final String name;
    private final ResourceLocation texture;
    private final List<RecipeEntry> recipeEntries = new ArrayList<>();
    private boolean banned = false;

    public static final ResourceLocation FoolTexture = new ResourceLocation("textures/entity/villager/villager.png");
    public static final ResourceLocation FarmerTexture = new ResourceLocation("textures/entity/villager/farmer.png");
    public static final ResourceLocation LibrarianTexture = new ResourceLocation("textures/entity/villager/librarian.png");
    public static final ResourceLocation PriestTexture = new ResourceLocation("textures/entity/villager/priest.png");
    public static final ResourceLocation SmithTexture = new ResourceLocation("textures/entity/villager/smith.png");
    public static final ResourceLocation ButcherTexture = new ResourceLocation("textures/entity/villager/butcher.png");

    public VillagerSettings(int profession, String name, ResourceLocation texture) {
        this.profession = profession;
        this.name = name;
        this.texture = texture;
    }

    public int getProfession() {
        return this.profession;
    }

    public String getName() {
        return this.name;
    }

    public String getTranslatedName() {
        return StringUtil.translate(this.name);
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
