package huix.glacier.api.extension.item;

import net.minecraft.Icon;
import net.minecraft.IconRegister;
import net.minecraft.ItemFishingRod;
import net.minecraft.Material;

import java.util.ArrayList;

public class GlacierItemFishingRod extends ItemFishingRod {
    private Icon newUncastIcon;
    private Icon newCastIcon;
    private final Material material;

    public GlacierItemFishingRod(int par1, Material hook_material) {
        super(par1, hook_material);
        this.material = hook_material;
    }


    @Override
    public Icon getIconFromSubtype(int par1) {
        return this.newCastIcon;
    }

    @Override
    public Icon func_94597_g() {
        return this.newUncastIcon;
    }

    @Override
    public void registerIcons(IconRegister par1IconRegister) {
        this.newCastIcon = par1IconRegister.registerIcon(this.getIconString() + "_cast");
        this.newUncastIcon = par1IconRegister.registerIcon("fishing_rods/" + this.material.name + "_uncast");
    }
}
