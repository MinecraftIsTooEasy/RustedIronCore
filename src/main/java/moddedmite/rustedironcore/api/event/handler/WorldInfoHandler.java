package moddedmite.rustedironcore.api.event.handler;

import moddedmite.rustedironcore.api.event.AbstractHandler;
import moddedmite.rustedironcore.api.event.listener.IWorldInfoListener;
import net.minecraft.NBTTagCompound;

public class WorldInfoHandler extends AbstractHandler<IWorldInfoListener> implements IWorldInfoListener {
    @Override
    public void onNBTWrite(NBTTagCompound nbt) {
        this.listeners.forEach(x -> x.onNBTWrite(nbt));
    }

    @Override
    public void onNBTRead(NBTTagCompound nbt) {
        this.listeners.forEach(x -> x.onNBTRead(nbt));
    }
}
