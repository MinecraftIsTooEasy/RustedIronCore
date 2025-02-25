package moddedmite.rustedironcore.mixin.entity.ai;

import moddedmite.rustedironcore.api.event.Handlers;
import net.minecraft.Entity;
import net.minecraft.EntityTrackerEntry;
import net.minecraft.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityTrackerEntry.class)
public abstract class EntityTrackerEntryMixin {
    @Shadow
    public Entity myEntity;

    @Inject(method = "getPacketForThisEntity", at = @At("HEAD"), cancellable = true)
    private void morePackets(CallbackInfoReturnable<Packet> cir) {
        if (this.myEntity.isDead) {
            this.myEntity.worldObj.getWorldLogAgent().logWarning("Fetching addPacket for removed entity");
        }
        Handlers.EntityTracker.matchEntity(this.myEntity).ifPresent(cir::setReturnValue);
    }
}
