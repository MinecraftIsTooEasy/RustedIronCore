package moddedmite.rustedironcore.mixin.other.entity;

import moddedmite.rustedironcore.api.item.BowItem;
import net.minecraft.Entity;
import net.minecraft.EntityArrow;
import net.minecraft.EntityPlayer;
import net.minecraft.ItemStack;
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
        if (launcher.getItem() instanceof BowItem bowItem && this.shootingEntity instanceof EntityPlayer)
            velocity *= 1.0F + (bowItem.getVelocityBonusPercentage() / 100.0F);
        return velocity;
    }
}
