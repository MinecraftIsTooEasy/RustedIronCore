package moddedmite.rustedironcore.world.item.crafting;

import net.minecraft.IRecipe;
import net.minecraft.Material;

public abstract class NormalCraftingRecipe implements CraftingRecipe {
    private float difficulty;
    private Material materialCheck;
    private boolean difficultyFlag;

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
