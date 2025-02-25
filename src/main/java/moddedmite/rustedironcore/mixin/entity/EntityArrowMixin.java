package moddedmite.rustedironcore.mixin.entity;

import huix.glacier.api.extension.material.IBowMaterial;
import moddedmite.rustedironcore.api.item.BowItem;
import net.minecraft.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(EntityArrow.class)
public abstract class EntityArrowMixin {
    @Shadow
    public Entity shootingEntity;

    @Shadow
    public abstract ItemStack getLauncher();

    @ModifyVariable(method = "setThrowableHeading", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private float modifySpeed(float velocity) {
        ItemStack launcher = this.getLauncher();
        if (launcher == null) return velocity;
        if (launcher.getItem() instanceof ItemBow itemBow && itemBow.getMaterialForRepairs() instanceof IBowMaterial iBowMaterial && this.shootingEntity instanceof EntityPlayer) {
            velocity *= 1.0F + (iBowMaterial.velocityBonus() / 100.0F);
        }
        if (launcher.getItem() instanceof BowItem bowItem) {// this is for compat
            velocity *= 1.0F + (bowItem.velocityBonus() / 100.0F);
        }
        return velocity;
    }
}
