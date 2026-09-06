package moddedmite.rustedironcore.api.entity;

import moddedmite.rustedironcore.internal.entity.DeathExperienceBridge;
import net.minecraft.EntityLivingBase;

/** Controls whether a dying entity still has experience available to drop. */
public final class DeathExperience {

    public static void consume(EntityLivingBase entity) {
        ((DeathExperienceBridge) entity).rusted_iron_core$consumeDeathExperience();
    }

    public static boolean isConsumed(EntityLivingBase entity) {
        return ((DeathExperienceBridge) entity).rusted_iron_core$isDeathExperienceConsumed();
    }
}
