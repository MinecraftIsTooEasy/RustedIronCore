package moddedmite.rustedironcore.api.event.listener;

import net.minecraft.EntityPlayer;

public interface ITickListener {
    default void onEntityPlayerTick(EntityPlayer player) {
    }
}
