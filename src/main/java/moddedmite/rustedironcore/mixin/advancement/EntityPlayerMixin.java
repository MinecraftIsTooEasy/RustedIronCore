package moddedmite.rustedironcore.mixin.advancement;

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import moddedmite.rustedironcore.api.advancement.CriteriaTriggers;
import moddedmite.rustedironcore.internal.advancement.AdvancementActor;
import net.minecraft.Entity;
import net.minecraft.EntityAnimal;
import net.minecraft.EntityItem;
import net.minecraft.EntityPlayer;
import net.minecraft.EntityTameable;
import net.minecraft.EntityZombie;
import net.minecraft.ItemStack;
import net.minecraft.RaycastCollision;
import net.minecraft.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityPlayer.class)
public class EntityPlayerMixin {

    @Inject(method = {"dropPlayerItemWithRandomChoice", "dropPlayerItemWithNoTrajectory"}, at = @At("RETURN"))
    private void trackThrower(CallbackInfoReturnable<EntityItem> callback) {
        if ((Object) this instanceof ServerPlayer player && callback.getReturnValue() != null)
            ((AdvancementActor) callback.getReturnValue()).ric$setAdvancementActor(player.getCommandSenderName());
    }

    @Inject(method = "checkForEntityInteraction", at = @At("HEAD"))
    private void captureInteraction(RaycastCollision hit, CallbackInfoReturnable<Boolean> callback, @Share("ric$interactionItem") LocalRef<ItemStack> item, @Share("ric$wasTamed") LocalBooleanRef tamed, @Share("ric$wasInLove") LocalBooleanRef love, @Share("ric$wasConverting") LocalBooleanRef converting) {
        if (!((Object) this instanceof ServerPlayer player) || hit == null || !hit.isEntity()) return;
        ItemStack held = player.getHeldItemStack();
        item.set(held == null ? null : held.copy());
        Entity entity = hit.getEntityHit();
        tamed.set(entity instanceof EntityTameable animal && animal.isTamed());
        love.set(entity instanceof EntityAnimal animal && animal.isInLove());
        converting.set(entity instanceof EntityZombie zombie && zombie.isConverting());
    }

    @Inject(method = "checkForEntityInteraction", at = @At("RETURN"))
    private void trackInteraction(RaycastCollision hit, CallbackInfoReturnable<Boolean> callback, @Share("ric$interactionItem") LocalRef<ItemStack> item, @Share("ric$wasTamed") LocalBooleanRef tamed, @Share("ric$wasInLove") LocalBooleanRef love, @Share("ric$wasConverting") LocalBooleanRef converting) {
        if (!((Object) this instanceof ServerPlayer player) || !callback.getReturnValue()) return;
        Entity entity = hit.getEntityHit();
        CriteriaTriggers.interactedWithEntity(player, item.get(), entity);
        if (entity instanceof EntityTameable animal && !tamed.get() && animal.isTamed()) CriteriaTriggers.tamedAnimal(player, animal);
        if ((entity instanceof EntityAnimal animal && !love.get() && animal.isInLove())
                || (entity instanceof EntityZombie zombie && !converting.get() && zombie.isConverting()))
            ((AdvancementActor) entity).ric$setAdvancementActor(player.getCommandSenderName());
    }
}
