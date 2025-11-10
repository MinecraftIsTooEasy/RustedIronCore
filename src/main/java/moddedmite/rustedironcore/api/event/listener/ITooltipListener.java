package moddedmite.rustedironcore.api.event.listener;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.EntityPlayer;
import net.minecraft.ItemStack;
import net.minecraft.Slot;

import java.util.List;

@Environment(EnvType.CLIENT)
public interface ITooltipListener {
    default void onTooltip(ItemStack stack, List<String> tooltip, EntityPlayer player, boolean detailed, Slot slot) {
    }
}
