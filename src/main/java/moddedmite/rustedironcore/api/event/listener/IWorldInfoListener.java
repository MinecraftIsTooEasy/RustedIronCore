package moddedmite.rustedironcore.api.event.listener;

import net.minecraft.NBTTagCompound;

/*
 * Write and read from the 'level.dat' file.
 * */
public interface IWorldInfoListener {
    default void onNBTWrite(NBTTagCompound nbt) {
    }

    default void onNBTRead(NBTTagCompound nbt) {
    }
}
