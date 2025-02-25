package moddedmite.rustedironcore.mixin.entity;

import moddedmite.rustedironcore.api.event.Handlers;
import net.minecraft.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityMob.class)
public abstract class EntityMobMixin extends EntityCreature {
    public EntityMobMixin(World par1World) {
        super(par1World);
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound) {
        super.writeEntityToNBT(par1NBTTagCompound);
        Handlers.EntityMobMixin.onWriteEntityToNBT((EntityMob) (Object) this, par1NBTTagCompound);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound) {
        super.readEntityFromNBT(par1NBTTagCompound);
        Handlers.EntityMobMixin.onReadEntityFromNBT((EntityMob) (Object) this, par1NBTTagCompound);
    }

    @Inject(method = "attackEntityFrom", at = @At("HEAD"))
    private void halfDamageByPlayer(Damage damage, CallbackInfoReturnable<EntityDamageResult> cir) {
        Handlers.Combat.onMobReceiveDamageModify((EntityMob) (Object) this, damage);
    }
}
