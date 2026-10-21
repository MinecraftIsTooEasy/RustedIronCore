package moddedmite.rustedironcore.world.item.crafting;

import net.minecraft.IRecipe;
import net.minecraft.Material;
import org.jspecify.annotations.Nullable;

public abstract class NormalCraftingRecipe implements CraftingRecipe {
    protected final Recipe.CommonInfo commonInfo;
    protected final CraftingRecipe.CraftingBookInfo bookInfo;
    private @Nullable PlacementInfo placementInfo;

    private float difficulty;
    private Material materialCheck;
    private boolean difficultyFlag;

    protected NormalCraftingRecipe(final Recipe.CommonInfo commonInfo, final CraftingRecipe.CraftingBookInfo bookInfo) {
        this.commonInfo = commonInfo;
        this.bookInfo = bookInfo;
    }

//    @Override
//    public abstract RecipeSerializer<? extends NormalCraftingRecipe> getSerializer();

    @Override
    public final String group() {
        return this.bookInfo.group();
    }

    @Override
    public final CraftingBookCategory category() {
        return this.bookInfo.category();
    }

    @Override
    public final boolean showNotification() {
        return this.commonInfo.showNotification();
    }

    protected abstract PlacementInfo createPlacementInfo();

    @Override
    public final PlacementInfo placementInfo() {
        if (this.placementInfo == null) {
            this.placementInfo = this.createPlacementInfo();
        }

        return this.placementInfo;
    }

    @Override
    public IRecipe setDifficulty(float difficulty) {
        this.difficulty = difficulty;
        return this;
    }

    @Override
    public IRecipe scaleDifficulty(float scale) {
        this.difficulty *= scale;
        return this;
    }

    @Override
    public float getUnmodifiedDifficulty() {
        return this.difficulty;
    }

    @Override
    public void setIncludeInLowestCraftingDifficultyDetermination() {
        this.difficultyFlag = true;
    }

    @Override
    public boolean getIncludeInLowestCraftingDifficultyDetermination() {
        return this.difficultyFlag;
    }

    @Override
    public void setSkillsets(int[] is) {
    }

    @Override
    public void setSkillset(int i) {
    }

    @Override
    public int[] getSkillsets() {
        return new int[0];
    }

    @Override
    public void setMaterialToCheckToolBenchHardnessAgainst(Material material) {
        this.materialCheck = material;
    }

    @Override
    public Material getMaterialToCheckToolBenchHardnessAgainst() {
        return this.materialCheck;
    }
}
