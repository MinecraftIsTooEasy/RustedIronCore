package moddedmite.rustedironcore.mixin.entity;

import moddedmite.rustedironcore.api.player.ClientPlayerAPI;
import net.minecraft.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityClientPlayerMP.class)
public abstract class EntityClientPlayerMPMixin extends ClientPlayer implements ClientPlayerAPI {
    @Unique
    private int phytonutrients;
    @Unique
    private int protein;
    @Unique
    private int essential_fats;
    @Unique
    private int nutrition_limit = 160000;

    public EntityClientPlayerMPMixin(Minecraft par1Minecraft, World par2World, Session par3Session, NetClientHandler par4NetClientHandler) {
        super(par1Minecraft, par2World, par3Session, 0);
    }

    @Inject(method = "onUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/ClientPlayer;onUpdate()V", shift = At.Shift.AFTER))
    private void onUpdate(CallbackInfo ci) {
        if (this.inCreativeMode()) {
            return;
        }
        if (this.protein > 0) {
            --this.protein;
        }
        if (this.essential_fats > 0) {
            --this.essential_fats;
        }
        if (this.phytonutrients > 0) {
            --this.phytonutrients;
        }
    }

    @Override
    public int ric$GetPhytonutrients() {
        return this.phytonutrients;
    }

    @Override
    public void ric$SetPhytonutrients(int phytonutrients) {
        this.phytonutrients = phytonutrients;
    }

    @Override
    public int ric$GetProtein() {
        return this.protein;
    }

    @Override
    public void ric$SetProtein(int protein) {
        this.protein = protein;
    }

    @Override
    public int ric$GetEssentialFats() {
        return this.essential_fats;
    }

    @Override
    public void ric$SetEssentialFats(int essential_fats) {
        this.essential_fats = essential_fats;
    }

    @Override
    public int ric$GetNutritionLimit() {
        return this.nutrition_limit;
    }

    @Override
    public void ric$SetNutritionLimit(int nutrition_limit) {
        this.nutrition_limit = nutrition_limit;
    }
}
