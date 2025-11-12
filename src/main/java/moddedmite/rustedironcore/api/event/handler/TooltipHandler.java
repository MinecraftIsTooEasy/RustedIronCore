package moddedmite.rustedironcore.api.event.handler;

import moddedmite.rustedironcore.api.event.AbstractHandler;
import moddedmite.rustedironcore.api.event.listener.ITooltipListener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.EntityPlayer;
import net.minecraft.ItemStack;
import net.minecraft.Slot;

import java.util.List;

@Environment(EnvType.CLIENT)
public class TooltipHandler extends AbstractHandler<ITooltipListener> {
    public void onTooltipHead(ItemStack stack, List<String> tooltip, EntityPlayer player, boolean detailed, Slot slot) {
        this.listeners.forEach(x -> x.onTooltipHead(stack, tooltip, player, detailed, slot));
    }
    public void onTooltipNeck(ItemStack stack, List<String> tooltip, EntityPlayer player, boolean detailed, Slot slot) {
        this.listeners.forEach(x -> x.onTooltipNeck(stack, tooltip, player, detailed, slot));
    }
    public void onTooltipBody(ItemStack stack, List<String> tooltip, EntityPlayer player, boolean detailed, Slot slot) {
        this.listeners.forEach(x -> x.onTooltipBody(stack, tooltip, player, detailed, slot));
    }
    public void onTooltipWaist(ItemStack stack, List<String> tooltip, EntityPlayer player, boolean detailed, Slot slot) {
        this.listeners.forEach(x -> x.onTooltipWaist(stack, tooltip, player, detailed, slot));
    }
    public void onTooltipTail(ItemStack stack, List<String> tooltip, EntityPlayer player, boolean detailed, Slot slot) {
        this.listeners.forEach(x -> x.onTooltipTail(stack, tooltip, player, detailed, slot));
    }
}
