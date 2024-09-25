package moddedmite.rustedironcore.api.event.listener;

import net.minecraft.EntityMob;
import net.minecraft.NBTTagCompound;

public interface IEntityMobListener {
    default void onReadEntityFromNBT(EntityMob entityMob, NBTTagCompound nbtTagCompound) {
    }

    default void onWriteEntityToNBT(EntityMob entityMob, NBTTagCompound nbtTagCompound) {
    }
}
