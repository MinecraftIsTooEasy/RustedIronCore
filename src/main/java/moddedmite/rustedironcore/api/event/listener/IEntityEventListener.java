package moddedmite.rustedironcore.api.event.listener;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.Damage;
import net.minecraft.DamageSource;
import net.minecraft.Entity;
import net.minecraft.EntityLivingBase;

@Environment(EnvType.SERVER)
public interface IEntityEventListener {
    default void onLoot(EntityLivingBase entity, DamageSource damageSource) {
    }

    default void onSpawn(Entity entity) {
    }

    default void onDeath(EntityLivingBase entity, DamageSource damageSource) {
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
