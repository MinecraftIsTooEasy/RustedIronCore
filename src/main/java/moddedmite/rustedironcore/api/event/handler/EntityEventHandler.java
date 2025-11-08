package moddedmite.rustedironcore.api.event.handler;

import moddedmite.rustedironcore.api.event.AbstractHandler;
import moddedmite.rustedironcore.api.event.listener.IEntityEventListener;
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
}
