package moddedmite.rustedironcore.mixin.world;

import moddedmite.rustedironcore.api.event.Handlers;
import net.minecraft.WorldChunkManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(WorldChunkManager.class)
public class WorldChunkManagerMixin {
    @Shadow
    private List biomesToSpawnIn;

    @Inject(method = {"<init>()V"}, at = {@At("RETURN")})
    private void playerSpawnable(CallbackInfo callbackInfo) {
        Handlers.BiomeGenerate.onPlayerSpawnableRegister(this.biomesToSpawnIn);
    }
}
