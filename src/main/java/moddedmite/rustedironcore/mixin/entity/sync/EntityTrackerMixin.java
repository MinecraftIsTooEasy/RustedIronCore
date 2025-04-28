package moddedmite.rustedironcore.mixin.entity.sync;

import moddedmite.rustedironcore.api.event.Handlers;
import net.minecraft.Entity;
import net.minecraft.EntityTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityTracker.class)
public abstract class EntityTrackerMixin {
    @Shadow
    public abstract void addEntityToTracker(Entity par1Entity, int par2, int par3, boolean par4);

    @Inject(method = "addEntityToTracker(Lnet/minecraft/Entity;)V", at = @At("RETURN"))
    private void moreEntities(Entity par1Entity, CallbackInfo ci) {
        Handlers.EntityTracker.matchEntityTracker(par1Entity).ifPresent(x -> this.addEntityToTracker(par1Entity, x.blocksDistanceThreshold(), x.updateFrequency(), x.sendVelocityUpdates()));
    }
}
