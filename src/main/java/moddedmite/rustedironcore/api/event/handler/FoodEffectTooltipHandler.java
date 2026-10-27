package moddedmite.rustedironcore.api.event.handler;

import moddedmite.rustedironcore.api.event.AbstractHandler;
import moddedmite.rustedironcore.api.event.listener.IFoodEffectProvider;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.EnumChatFormatting;
import net.minecraft.ItemStack;
import net.minecraft.Potion;
import net.minecraft.PotionEffect;
import net.minecraft.StatCollector;

import java.util.List;

@Environment(EnvType.CLIENT)
public class FoodEffectTooltipHandler extends AbstractHandler<IFoodEffectProvider> {

    public void appendTooltip(ItemStack stack, List<String> tooltip) {
        for (IFoodEffectProvider provider : this.listeners) {
            for (PotionEffect effect : provider.getEffects(stack)) {
                String description = StatCollector.translateToLocal(effect.getEffectName()).trim();
                if (effect.getAmplifier() > 0) {
                    description += " " + StatCollector.translateToLocal("potion.potency." + effect.getAmplifier()).trim();
                }
                if (effect.getDuration() > 20) {
                    description += " (" + Potion.getDurationString(effect) + ")";
                }
                EnumChatFormatting color = effect.getPotion().isBadEffect()
                        ? EnumChatFormatting.RED : EnumChatFormatting.BLUE;
                tooltip.add(color + description);
            }
        }
    }
}
