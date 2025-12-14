package moddedmite.rustedironcore.api.event.handler;

import moddedmite.rustedironcore.api.event.AbstractHandler;
import moddedmite.rustedironcore.api.event.listener.IEntityEventListener;
import net.minecraft.Damage;
import net.minecraft.DamageSource;
import net.minecraft.Entity;
import net.minecraft.EntityLivingBase;

public class EntityEventHandler extends AbstractHandler<IEntityEventListener> {
    public void onLoot(EntityLivingBase entity, DamageSource damageSource) {
        this.listeners.forEach(x -> x.onLoot(entity, damageSource));
    }

    public void onSpawn(Entity entity) {
        this.listeners.forEach(x -> x.onSpawn(entity));
    }

    public void onDeath(EntityLivingBase entity, DamageSource damageSource) {
        this.listeners.forEach(x -> x.onDeath(entity, damageSource));
    }

    public void onUpdate(EntityLivingBase entity) {
        this.listeners.forEach(x -> x.onUpdate(entity));
    }

    public void onAttackEntityFrom(EntityLivingBase entity, Damage damage) {
        this.listeners.forEach(x -> x.onAttackEntityFrom(entity, damage));
    }

    public void onFall(EntityLivingBase entity, float distance) {
        this.listeners.forEach(x -> x.onFall(entity, distance));
    }

    public void onJump(EntityLivingBase entity) {
        this.listeners.forEach(x -> x.onJump(entity));
    }
}
