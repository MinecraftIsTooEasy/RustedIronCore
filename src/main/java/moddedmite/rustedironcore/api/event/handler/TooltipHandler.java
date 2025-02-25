package moddedmite.rustedironcore.api.event.handler;

import moddedmite.rustedironcore.api.event.AbstractHandler;
import moddedmite.rustedironcore.api.event.listener.ITooltipListener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.EntityPlayer;
import net.minecraft.Slot;

import java.util.List;

@Environment(EnvType.CLIENT)
public class TooltipHandler extends AbstractHandler<ITooltipListener> {
    public void onTooltip(List<String> tooltip, EntityPlayer player, boolean detailed, Slot slot) {
        this.listeners.forEach(x -> x.onTooltip(tooltip, player, detailed, slot));
    }
}
