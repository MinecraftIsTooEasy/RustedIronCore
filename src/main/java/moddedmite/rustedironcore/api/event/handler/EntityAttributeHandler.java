package moddedmite.rustedironcore.api.event.handler;

import moddedmite.rustedironcore.api.event.AbstractHandler;
import moddedmite.rustedironcore.api.event.listener.IEntityAttributeListener;
import net.minecraft.Attribute;
import net.minecraft.AttributeInstance;
import net.minecraft.BaseAttributeMap;
import net.minecraft.EntityLivingBase;
import net.minecraft.EntityPlayer;
import net.minecraft.SharedMonsterAttributes;

import java.util.Collection;

public class EntityAttributeHandler extends AbstractHandler<IEntityAttributeListener> {

    public void onEntityAttributeRegister(EntityLivingBase entity) {
        for (IEntityAttributeListener listener : this.listeners) {
            listener.onEntityAttributeRegister(entity);
        }
    }

    public double onEntityAttributeModify(EntityLivingBase entity, Attribute attribute, double original) {
        for (IEntityAttributeListener listener : this.listeners) {
            original = listener.onEntityAttributeModify(entity, attribute, original);
        }
        return original;
    }

    public void applyEntityAttributes(EntityLivingBase entity) {
        if (entity instanceof EntityPlayer) return;
        this.onEntityAttributeRegister(entity);
        for (AttributeInstance attribute : (Collection<AttributeInstance>) entity.getAttributeMap().getAllAttributes()) {
            attribute.setAttribute(this.onEntityAttributeModify(entity, attribute.func_111123_a(), attribute.getBaseValue()));
        }
    }
}
