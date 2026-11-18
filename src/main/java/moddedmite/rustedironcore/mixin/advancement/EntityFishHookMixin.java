package moddedmite.rustedironcore.mixin.advancement;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import moddedmite.rustedironcore.api.advancement.CriteriaTriggers;
import net.minecraft.Entity;
import net.minecraft.EntityFishHook;
import net.minecraft.EntityItem;
import net.minecraft.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(EntityFishHook.class)
public class EntityFishHookMixin {

    @WrapOperation(method = "catchFish", at = @At(value = "INVOKE", target = "Lnet/minecraft/World;spawnEntityInWorld(Lnet/minecraft/Entity;)Z"))
    private boolean trackFishingLoot(World world, Entity entity, Operation<Boolean> original) {
        boolean spawned = original.call(world, entity);
        EntityFishHook hook = (EntityFishHook) (Object) this;
        if (spawned && entity instanceof EntityItem item) CriteriaTriggers.fishingRodHooked(hook.angler, hook.angler.getHeldItemStack(), hook, List.of(item.getEntityItem()));
        return spawned;
    }

    @Inject(method = "catchFish", at = @At("RETURN"))
    private void trackHookedEntity(CallbackInfoReturnable<Integer> callback) {
        EntityFishHook hook = (EntityFishHook) (Object) this;
        if (callback.getReturnValue() == 3 && hook.bobber != null) CriteriaTriggers.fishingRodHooked(hook.angler, hook.angler.getHeldItemStack(), hook.bobber,
                hook.bobber instanceof EntityItem item ? List.of(item.getEntityItem()) : List.of());
    }
}
