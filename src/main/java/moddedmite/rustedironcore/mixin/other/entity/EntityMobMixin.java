package moddedmite.rustedironcore.mixin.other.entity;

import moddedmite.rustedironcore.api.event.Handlers;
import net.minecraft.EntityCreature;
import net.minecraft.EntityMob;
import net.minecraft.NBTTagCompound;
import net.minecraft.World;
import org.spongepowered.asm.mixin.Mixin;

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
}
