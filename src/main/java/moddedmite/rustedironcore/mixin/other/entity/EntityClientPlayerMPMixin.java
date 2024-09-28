package moddedmite.rustedironcore.mixin.other.entity;

import moddedmite.rustedironcore.api.player.ClientPlayerAPI;
import net.minecraft.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(EntityClientPlayerMP.class)
public abstract class EntityClientPlayerMPMixin extends ClientPlayer implements ClientPlayerAPI {
    @Unique
    private int phytonutrients;
    @Unique
    private int protein;
    @Unique
    private int essential_fats;

    public EntityClientPlayerMPMixin(Minecraft par1Minecraft, World par2World, Session par3Session, NetClientHandler par4NetClientHandler) {
        super(par1Minecraft, par2World, par3Session, 0);
    }

    @Override
    public int getPhytonutrients() {
        return this.phytonutrients;
    }

    @Override
    public void setPhytonutrients(int phytonutrients) {
        this.phytonutrients = phytonutrients;
    }

    @Override
    public int getProtein() {
        return this.protein;
    }

    @Override
    public void setProtein(int protein) {
        this.protein = protein;
    }

    @Override
    public int getEssentialFats() {
        return this.essential_fats;
    }

    @Override
    public void setEssentialFats(int essential_fats) {
        this.essential_fats = essential_fats;
    }
}
