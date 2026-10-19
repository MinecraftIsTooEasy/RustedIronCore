package moddedmite.rustedironcore.internal.client.event.listeners;

import moddedmite.rustedironcore.api.event.listener.ITooltipListener;
import moddedmite.rustedironcore.core.TypedInstance;
import moddedmite.rustedironcore.resources.Identifier;
import moddedmite.rustedironcore.tags.TagKey;
import net.minecraft.EntityPlayer;
import net.minecraft.ItemStack;
import net.minecraft.Slot;

import java.util.List;

public class TooltipListener implements ITooltipListener {
    @Override
    public void onTooltipTail(ItemStack stack, List<String> tooltip, EntityPlayer player, boolean detailed, Slot slot) {
        if (detailed) {
            tooltip.add("Tags:");
            tooltip.addAll(
                    TypedInstance.castItem(stack).tags()
                            .map(TagKey::location)
                            .map(Identifier::toString)
                            .toList()
            );
        }
    }
}
