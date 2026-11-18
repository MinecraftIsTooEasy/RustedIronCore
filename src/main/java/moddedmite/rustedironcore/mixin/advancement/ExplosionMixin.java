package moddedmite.rustedironcore.mixin.advancement;

import moddedmite.rustedironcore.internal.advancement.AdvancementEvents;
import net.minecraft.Entity;
import net.minecraft.Explosion;
import net.minecraft.ServerPlayer;
import net.minecraft.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(Explosion.class)
public class ExplosionMixin {

    @Shadow public Entity exploder;

    @Inject(method = "doExplosionA", at = @At("RETURN"))
    private void trackImpulse(CallbackInfo callback) {
        Map<?, ?> impacted = ((Explosion) (Object) this).func_77277_b();
        impacted.forEach((entity, value) -> {
            if (entity instanceof ServerPlayer player && value instanceof Vec3 impulse
                    && (impulse.xCoord != 0 || impulse.yCoord != 0 || impulse.zCoord != 0)) AdvancementEvents.explosionHit(player, exploder);
        });
    }
}
