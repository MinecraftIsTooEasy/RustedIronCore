package moddedmite.rustedironcore.api.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.EntityLivingBase;
import net.minecraft.ItemStack;

/** Supplies a runtime value for a standard item-model override predicate. */
@Environment(EnvType.CLIENT)
@FunctionalInterface
public interface JsonItemModelPredicate {

    float getValue(ItemStack stack, EntityLivingBase entity);
}
