package moddedmite.rustedironcore.api.event.listener;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ItemStack;
import net.minecraft.PotionEffect;

import java.util.List;

@Environment(EnvType.CLIENT)
public interface IFoodEffectProvider {

    List<PotionEffect> getEffects(ItemStack stack);
}
