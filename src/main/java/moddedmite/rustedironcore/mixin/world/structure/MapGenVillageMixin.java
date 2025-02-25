package moddedmite.rustedironcore.mixin.world.structure;

import moddedmite.rustedironcore.api.event.Handlers;
import net.minecraft.BiomeGenBase;
import net.minecraft.MapGenVillage;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(MapGenVillage.class)
public class MapGenVillageMixin {
    @Mutable
    @Shadow
    @Final
    public static List<BiomeGenBase> villageSpawnBiomes;

    @Inject(method = "<clinit>", at = @At("RETURN"))
    private static void onClinit(CallbackInfo ci) {
        villageSpawnBiomes = new ArrayList<>(villageSpawnBiomes);
        Handlers.BiomeGenerate.onVillageAllowedRegister(villageSpawnBiomes);
    }
}
