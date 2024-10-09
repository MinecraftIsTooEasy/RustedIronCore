package moddedmite.rustedironcore.api.event.handler;

import moddedmite.rustedironcore.api.event.AbstractHandler;
import moddedmite.rustedironcore.api.event.listener.ICombatListener;
import net.minecraft.*;

public class CombatHandler extends AbstractHandler<ICombatListener> {
    public void onPlayerReceiveDamageModify(EntityPlayer player, Damage damage) {
        this.listeners.forEach(x -> x.onPlayerReceiveDamageModify(player, damage));
    }

    public void onMobReceiveDamageModify(EntityMob mob, Damage damage) {
        this.listeners.forEach(x -> x.onMobReceiveDamageModify(mob, damage));
    }

    public float onArmorProtectionModify(ItemStack item_stack, EntityLivingBase owner, float original) {
        for (ICombatListener listener : this.listeners) {
            original = listener.onArmorProtectionModify(item_stack, owner, original);
        }
        return original;
    }

    public float onPlayerRawMeleeDamageModify(EntityPlayer player, Entity target, boolean critical, boolean suspended_in_liquid, float original) {
        for (ICombatListener listener : this.listeners) {
            original = listener.onPlayerRawMeleeDamageModify(player, target, critical, suspended_in_liquid, original);
        }
        return original;
    }

    public float onPlayerBlockReachModify(EntityPlayer player, Block block, int metadata, float original) {
        for (ICombatListener listener : this.listeners) {
            original = listener.onPlayerBlockReachModify(player, block, metadata, original);
        }
        return original;
    }

    public float onPlayerEntityReachModify(EntityPlayer player, EnumEntityReachContext context, Entity entity, float original) {
        for (ICombatListener listener : this.listeners) {
            original = listener.onPlayerEntityReachModify(player, context, entity, original);
        }
        return original;
    }

    public float onPlayerRawStrVsBlockModify(EntityPlayer player, float original) {
        for (ICombatListener listener : this.listeners) {
            original = listener.onPlayerRawStrVsBlockModify(player, original);
        }
        return original;
    }

    public float onPlayerStrVsBlockModify(EntityPlayer player, float original) {
        for (ICombatListener listener : this.listeners) {
            original = listener.onPlayerStrVsBlockModify(player, original);
        }
        return original;
    }

    public float onPlayerReceiveKnockBackModify(EntityPlayer player, Entity attacker, float original) {
        for (ICombatListener listener : this.listeners) {
            original = listener.onPlayerReceiveKnockBackModify(player, attacker, original);
        }
        return original;
    }
}
