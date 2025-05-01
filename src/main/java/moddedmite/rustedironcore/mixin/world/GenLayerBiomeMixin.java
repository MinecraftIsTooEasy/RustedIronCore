package moddedmite.rustedironcore.mixin.world;

import moddedmite.rustedironcore.api.event.Handlers;
import net.minecraft.BiomeGenBase;
import net.minecraft.GenLayer;
import net.minecraft.GenLayerBiome;
import net.minecraft.WorldType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Mixin(GenLayerBiome.class)
public class GenLayerBiomeMixin {
    @Shadow
    private BiomeGenBase[] allowedBiomes;

    @Inject(method = "<init>(JLnet/minecraft/GenLayer;Lnet/minecraft/WorldType;)V", at = @At("RETURN"))
    private void addBiomes(long par1, GenLayer par3GenLayer, WorldType par4WorldType, CallbackInfo callbackInfo) {
        List<BiomeGenBase> original = new ArrayList<>(Arrays.stream(this.allowedBiomes).toList());
        Handlers.BiomeGenerate.onInitialBiomesModify(original);
        this.allowedBiomes = original.toArray(BiomeGenBase[]::new);
    }
}
