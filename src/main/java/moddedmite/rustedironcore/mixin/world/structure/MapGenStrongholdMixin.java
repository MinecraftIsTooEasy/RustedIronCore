package moddedmite.rustedironcore.mixin.world.structure;

import moddedmite.rustedironcore.api.event.Handlers;
import net.minecraft.BiomeGenBase;
import net.minecraft.MapGenStronghold;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Mixin(MapGenStronghold.class)
public class MapGenStrongholdMixin {
    @Shadow
    private BiomeGenBase[] allowedBiomeGenBases;


    @Inject(method = "<init>()V", at = @At("TAIL"))
    private void injectAddBiomes(CallbackInfo ci) {
        this.addBiomes();
    }

    @Inject(method = "<init>(Ljava/util/Map;)V", at = @At("TAIL"))
    private void injectAddBiomes(Map par1Map, CallbackInfo ci) {
        this.addBiomes();
    }

    @Unique
    private void addBiomes() {
        List<BiomeGenBase> original = new ArrayList<>(Arrays.asList(this.allowedBiomeGenBases));
        Handlers.BiomeGenerate.onStrongholdAllowedRegister(original);
        allowedBiomeGenBases = original.toArray(BiomeGenBase[]::new);
    }
}
