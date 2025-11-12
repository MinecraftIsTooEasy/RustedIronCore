package moddedmite.rustedironcore.api.event.listener;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.EntityPlayer;
import net.minecraft.ItemStack;
import net.minecraft.Slot;

import java.util.List;

@Environment(EnvType.CLIENT)
public interface ITooltipListener {
    /**
    * Located before quality information, usually used to add annotation information
    */
    default void onTooltipHead(ItemStack stack, List<String> tooltip, EntityPlayer player, boolean detailed, Slot slot) {
    }

    /**
     * Located after the enchantment information
     */
    default void onTooltipNeck(ItemStack stack, List<String> tooltip, EntityPlayer player, boolean detailed, Slot slot) {
    }

    /**
     * After the food attribute information (Item only)
     */
    default void onTooltipBody(ItemStack stack, List<String> tooltip, EntityPlayer player, boolean detailed, Slot slot) {
    }

    /**
     * Before the attribute modifier information
     */
    default void onTooltipWaist(ItemStack stack, List<String> tooltip, EntityPlayer player, boolean detailed, Slot slot) {
    }
    /**
     * After the durability information, usually used to add annotation information
     */
    default void onTooltipTail(ItemStack stack, List<String> tooltip, EntityPlayer player, boolean detailed, Slot slot) {
    }
}
