package moddedmite.rustedironcore.api.block;

import moddedmite.rustedironcore.api.event.events.CraftingRecipeRegisterEvent;
import net.minecraft.*;
import net.xiaoyu233.fml.reload.event.RecipeRegistryEvent;

public class WorkbenchBlock extends BlockWorkbench {
    private Icon my_workbench_icon_top;
    protected Icon my_front_icon;
    protected Icon my_side_icon;
    private final Material material;
    private final float speedModifier;
    private final Material checkAgainst;

    public WorkbenchBlock(int id, Material material, float speedModifier, Material checkAgainst) {
        super(id);
        this.material = material;
        this.speedModifier = speedModifier;
        this.checkAgainst = checkAgainst;
    }

    public Material getMaterial() {
        return this.material;
    }

    public float getSpeedModifier() {
        return this.speedModifier;
    }

    public Material getMaterialToCheckToolBenchHardnessAgainst() {
        return this.checkAgainst;
    }

    public void registerSimpleRecipe(RecipeRegistryEvent event) {
        for (int plank_subtype = 0; plank_subtype < 4; ++plank_subtype) {
            event.registerShapedRecipe(new ItemStack(this), true, "IL", "s#", 'I', ItemIngot.getMatchingItem(ItemIngot.class, this.material), 'L', Item.leather, 's', Item.stick, '#', new ItemStack(Block.planks, 1, plank_subtype));
        }
    }// you should call it when you event through fml api

    public void registerSimpleRecipe(CraftingRecipeRegisterEvent event) {
        for (int plank_subtype = 0; plank_subtype < 4; ++plank_subtype) {
            event.registerShapedRecipe(new ItemStack(this), true, "IL", "s#", 'I', ItemIngot.getMatchingItem(ItemIngot.class, this.material), 'L', Item.leather, 's', Item.stick, '#', new ItemStack(Block.planks, 1, plank_subtype));
        }
    }

    @Override
    public Icon getIcon(int side, int metadata) {
        if (side == 0) {
            return Block.planks.getBlockTextureFromSide(side);
        }
        if (side == 1) {
            return this.my_workbench_icon_top;
        }
        if (side == 2 || side == 3) {
            return this.my_front_icon;
        }
        return this.my_side_icon;
    }

    @Override
    public void registerIcons(IconRegister par1IconRegister) {
        this.my_workbench_icon_top = par1IconRegister.registerIcon("crafting_table_top");
        this.my_front_icon = par1IconRegister.registerIcon("crafting_table/" + this.material.name + "/front");
        this.my_side_icon = par1IconRegister.registerIcon("crafting_table/" + this.material.name + "/side");
    }


    @Override
    public boolean isValidMetadata(int metadata) {
        return metadata == 0;
    }
}
