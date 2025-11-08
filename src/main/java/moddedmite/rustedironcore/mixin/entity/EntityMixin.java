package moddedmite.rustedironcore.mixin.entity;

import moddedmite.rustedironcore.api.event.Handlers;
import net.minecraft.Entity;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class EntityMixin {
    @Inject(method = "onSpawned", at = @At("RETURN"))
    private void onSpawned(CallbackInfo ci) {
        Handlers.EntityEvent.onSpawn((Entity) (Object) this);
    }

    @Inject(method = "onEntityUpdate", at = @At(value = "FIELD", target = "Lnet/minecraft/Entity;firstUpdate:Z", opcode = Opcodes.PUTFIELD))
    private void onTick(CallbackInfo ci) {
        Handlers.Tick.onEntityTick((Entity) (Object) this);
    }
}
