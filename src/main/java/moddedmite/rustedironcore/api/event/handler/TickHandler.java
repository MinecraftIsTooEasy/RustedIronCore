package moddedmite.rustedironcore.api.event.handler;

import moddedmite.rustedironcore.api.event.AbstractHandler;
import moddedmite.rustedironcore.api.event.listener.ITickListener;
import net.minecraft.EntityPlayer;

public class TickHandler extends AbstractHandler<ITickListener> {
    public void onEntityPlayerTick(EntityPlayer player) {
        this.listeners.forEach(x->x.onEntityPlayerTick(player));
    }
}
