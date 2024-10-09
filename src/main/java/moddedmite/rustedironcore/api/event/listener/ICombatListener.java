package moddedmite.rustedironcore.api.event.listener;

import net.minecraft.*;

public interface ICombatListener {
    default void onPlayerReceiveDamageModify(EntityPlayer player, Damage damage) {
    }

    default void onMobReceiveDamageModify(EntityMob mob, Damage damage) {
    }

    default float onArmorProtectionModify(ItemStack item_stack, EntityLivingBase owner, float original) {
        return original;
    }

    default float onPlayerRawMeleeDamageModify(EntityPlayer player, Entity target, boolean critical, boolean suspended_in_liquid, float original) {
        return original;
    }

    default float onPlayerBlockReachModify(EntityPlayer player, Block block, int metadata, float original) {
        return original;
    }

    default float onPlayerEntityReachModify(EntityPlayer player, EnumEntityReachContext context, Entity entity, float original) {
        return original;
    }

    default float onPlayerRawStrVsBlockModify(EntityPlayer player, float original) {
        return original;
    }

    default float onPlayerStrVsBlockModify(EntityPlayer player, float original) {
        return original;
    }

    default float onPlayerReceiveKnockBackModify(EntityPlayer player, Entity attacker, float original) {
        return original;
    }

}
