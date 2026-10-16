package moddedmite.rustedironcore.api.event.listener;

import net.minecraft.Attribute;
import net.minecraft.EntityLivingBase;

public interface IEntityAttributeListener {

    default void onEntityAttributeRegister(EntityLivingBase entity) {
    }

    default double onEntityAttributeModify(EntityLivingBase entity, Attribute attribute, double original) {
        return original;
    }
}
