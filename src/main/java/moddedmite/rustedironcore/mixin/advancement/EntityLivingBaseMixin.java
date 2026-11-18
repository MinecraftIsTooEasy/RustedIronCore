package moddedmite.rustedironcore.mixin.advancement;

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import moddedmite.rustedironcore.api.advancement.CriteriaTriggers;
import moddedmite.rustedironcore.internal.advancement.AdvancementActor;
import net.minecraft.Damage;
import net.minecraft.Entity;
import net.minecraft.EntityDamageResult;
import net.minecraft.EntityLivingBase;
import net.minecraft.EntityPlayer;
import net.minecraft.EntityItem;
import net.minecraft.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.PotionEffect;
import net.minecraft.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityLivingBase.class)
public class EntityLivingBaseMixin {

    @Inject(method = "onItemPickup", at = @At("HEAD"))
    private void trackThrownItemPickup(Entity entity, int amount, CallbackInfo callback) {
        EntityLivingBase collector = (EntityLivingBase) (Object) this;
        if (collector.worldObj.isRemote || !(entity instanceof EntityItem item)) return;
        String name = ((AdvancementActor) item).ric$getAdvancementActor();
        if (name.isEmpty()) return;
        ItemStack stack = item.getEntityItem().copy();
        stack.stackSize = amount;
        ServerPlayer thrower = MinecraftServer.getServer().getConfigurationManager().getPlayerForUsername(name);
        if (collector instanceof ServerPlayer player) CriteriaTriggers.pickedUpThrownItem(player, stack, thrower, true);
        if (thrower != null) CriteriaTriggers.pickedUpThrownItem(thrower, stack, collector, false);
    }

    @Inject(method = "attackEntityFrom(Lnet/minecraft/Damage;)Lnet/minecraft/EntityDamageResult;", at = @At("HEAD"))
    private void captureIncomingDamage(Damage damage, CallbackInfoReturnable<EntityDamageResult> callback, @Share("ric$incomingDamage") LocalFloatRef amount, @Share("ric$blockedDamage") LocalBooleanRef blocked) {
        amount.set(damage.getAmount());
        blocked.set((Object) this instanceof EntityPlayer player && player.isBlocking() && !damage.getSource().isUnblockable());
    }

    @Inject(method = "attackEntityFrom(Lnet/minecraft/Damage;)Lnet/minecraft/EntityDamageResult;", at = @At("RETURN"))
    private void trackDamage(Damage damage, CallbackInfoReturnable<EntityDamageResult> callback, @Share("ric$incomingDamage") LocalFloatRef amount, @Share("ric$blockedDamage") LocalBooleanRef blocked) {
        EntityLivingBase victim = (EntityLivingBase) (Object) this;
        EntityDamageResult result = callback.getReturnValue();
        if (victim.worldObj.isRemote || result == null || !result.entityWasNegativelyAffected()) return;
        Entity attacker = damage.getSource().getResponsibleEntity();
        if (victim instanceof ServerPlayer player) CriteriaTriggers.hurt(player, attacker, damage.getSource(), amount.get(), result.getAmountOfHealthLost(), blocked.get(), true);
        if (attacker instanceof ServerPlayer player) CriteriaTriggers.hurt(player, victim, damage.getSource(), amount.get(), result.getAmountOfHealthLost(), blocked.get(), false);
    }

    @Inject(method = {"onNewPotionEffect(Lnet/minecraft/PotionEffect;)V", "onChangedPotionEffect(Lnet/minecraft/PotionEffect;Z)V", "onFinishedPotionEffect(Lnet/minecraft/PotionEffect;)V"}, at = @At("RETURN"))
    private void trackEffects(CallbackInfo callback) {
        if ((Object) this instanceof ServerPlayer player) CriteriaTriggers.effectsChanged(player, null);
    }
}
