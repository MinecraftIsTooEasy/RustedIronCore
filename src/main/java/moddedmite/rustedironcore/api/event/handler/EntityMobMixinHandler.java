package moddedmite.rustedironcore.api.event.handler;

import moddedmite.rustedironcore.api.event.AbstractHandler;
import moddedmite.rustedironcore.api.event.listener.IEntityMobListener;
import net.minecraft.EntityMob;
import net.minecraft.NBTTagCompound;

public class EntityMobMixinHandler extends AbstractHandler<IEntityMobListener> {
    public void onReadEntityFromNBT(EntityMob entityMob, NBTTagCompound nbtTagCompound) {
        this.listeners.forEach(x -> x.onReadEntityFromNBT(entityMob, nbtTagCompound));
    }

    public void onWriteEntityToNBT(EntityMob entityMob, NBTTagCompound nbtTagCompound) {
        this.listeners.forEach(x -> x.onWriteEntityToNBT(entityMob, nbtTagCompound));
    }
}
