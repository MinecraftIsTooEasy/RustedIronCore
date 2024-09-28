package moddedmite.rustedironcore.api.item;

import moddedmite.rustedironcore.api.util.ItemUtil;
import moddedmite.rustedironcore.property.MaterialProperties;
import net.minecraft.*;
import net.xiaoyu233.fml.reload.event.RecipeRegistryEvent;

public class FishingRodItem extends ItemFishingRod {
    private Icon my_cast_icon;
    private Icon my_uncast_icon;
    private final Material material;

    public FishingRodItem(int id, Material hook_material) {
        super(id, hook_material);
        this.material = hook_material;
        MaterialProperties.PeerFishingRod.register(hook_material, this);
    }

    @Override
    public void registerIcons(IconRegister par1IconRegister) {
        this.my_cast_icon = par1IconRegister.registerIcon(this.getIconString() + "_cast");
        this.my_uncast_icon = par1IconRegister.registerIcon("fishing_rods/" + this.material.name + "_uncast");
    }

    @Override
    public Icon getIconFromSubtype(int par1) {
        return this.my_uncast_icon;
    }

    @Override
    public Icon func_94597_g() {
        return this.my_cast_icon;
    }

    public void registerSimpleRecipe(RecipeRegistryEvent event) {
        event.registerShapedRecipe(new ItemStack(this), true, "  S", " SG", "SAG", 'A', ItemUtil.getNuggetForMaterial(this.material), 'S', Item.stick, 'G', Item.silk);
    }
}
