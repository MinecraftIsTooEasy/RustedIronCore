package moddedmite.rustedironcore.mixin.entity.sync;

import moddedmite.rustedironcore.api.event.Handlers;
import moddedmite.rustedironcore.api.event.handler.EntityTrackerHandler;
import net.minecraft.Entity;
import net.minecraft.EntityTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(EntityTracker.class)
public abstract class EntityTrackerMixin {
    @Shadow
    public abstract void addEntityToTracker(Entity par1Entity, int par2, int par3, boolean par4);

    @Inject(method = "addEntityToTracker(Lnet/minecraft/Entity;)V", at = @At("HEAD"), cancellable = true)
    private void trackRegisteredEntity(Entity par1Entity, CallbackInfo ci) {
        Optional<EntityTrackerHandler.EntityTrackerEntry> entry = Handlers.EntityTracker.matchEntityTracker(par1Entity);
        if (entry.isEmpty()) {
            return;
        }
        EntityTrackerHandler.EntityTrackerEntry tracker = entry.get();
        this.addEntityToTracker(par1Entity, tracker.blocksDistanceThreshold(), tracker.updateFrequency(), tracker.sendVelocityUpdates());
        ci.cancel();
    }
}
