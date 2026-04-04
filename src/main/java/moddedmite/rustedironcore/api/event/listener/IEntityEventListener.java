package moddedmite.rustedironcore.api.event.listener;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.*;

public interface IEntityEventListener {
    @Environment(EnvType.SERVER)
    default void onLoot(EntityLivingBase entity, DamageSource damageSource) {
    }

    default void onSpawn(Entity entity) {
    }

    default void onDeath(EntityLivingBase entity, DamageSource damageSource) {
    }

    @Environment(EnvType.SERVER)
    default void onServerPlayerDeath(ServerPlayer player, DamageSource damageSource) {
    }

    default void onUpdate(EntityLivingBase entity) {
    }

    default void onAttackEntityFrom(EntityLivingBase entity, Damage damage) {
    }

    default void onFall(EntityLivingBase entity, float distance) {
    }

    default void onJump(EntityLivingBase entity) {
    }
}
