package moddedmite.rustedironcore.mixin.other.world.structure;

import moddedmite.rustedironcore.api.event.Handlers;
import net.minecraft.ComponentScatteredFeatureJunglePyramid;
import net.minecraft.WeightedRandomChestContent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Mixin(ComponentScatteredFeatureJunglePyramid.class)
public class WorldGenJungleTempleMixin {

    @Mutable
    @Shadow
    @Final
    private static WeightedRandomChestContent[] junglePyramidsChestContents;

    @Inject(method = "<clinit>", at = @At("RETURN"))
    private static void addLoot(CallbackInfo ci) {
        List<WeightedRandomChestContent> original = new ArrayList<>(Arrays.asList(junglePyramidsChestContents));
        Handlers.LootTable.onJunglePyramidRegister(original);
        junglePyramidsChestContents = original.toArray(WeightedRandomChestContent[]::new);
    }
}
