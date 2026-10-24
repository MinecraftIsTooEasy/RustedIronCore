package moddedmite.rustedironcore.mixin.entity;

import moddedmite.rustedironcore.api.event.Handlers;
import net.minecraft.Entity;
import net.minecraft.NBTTagCompound;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

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

    @Redirect(method = "writeToNBT", at = @At(value = "INVOKE", target = "Ljava/util/UUID;getMostSignificantBits()J"))
    private long writeUuidMost(UUID uuid) {
        return uuid == null ? 0L : uuid.getMostSignificantBits();
    }

    @Redirect(method = "writeToNBT", at = @At(value = "INVOKE", target = "Ljava/util/UUID;getLeastSignificantBits()J"))
    private long writeUuidLeast(UUID uuid) {
        return uuid == null ? 0L : uuid.getLeastSignificantBits();
    }

    @Redirect(method = "writeToNBT", at = @At(value = "INVOKE", target = "Lnet/minecraft/NBTTagCompound;setLong(Ljava/lang/String;J)V"))
    private void writeUuidTag(NBTTagCompound tag, String name, long value) {
        Entity entity = (Entity) (Object) this;
        if (entity.getUniqueIDSilent() == null && ("UUIDMost".equals(name) || "UUIDLeast".equals(name))) return;
        tag.setLong(name, value);
    }
}
