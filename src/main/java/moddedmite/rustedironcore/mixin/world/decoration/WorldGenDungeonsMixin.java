package moddedmite.rustedironcore.mixin.world.decoration;

import moddedmite.rustedironcore.api.event.Handlers;
import net.minecraft.WeightedRandomChestContent;
import net.minecraft.WorldGenDungeons;
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

@Mixin(WorldGenDungeons.class)
public class WorldGenDungeonsMixin {
    @Mutable
    @Shadow
    @Final
    private static WeightedRandomChestContent[] field_111189_a;

    @Mutable
    @Shadow
    @Final
    private static WeightedRandomChestContent[] chest_contents_for_underworld;

    @Inject(method = "<clinit>", at = @At("RETURN"))
    private static void onClinit(CallbackInfo ci) {
        List<WeightedRandomChestContent> original_overworld = new ArrayList<>(Arrays.asList(field_111189_a));
        Handlers.LootTable.onDungeonOverworldRegister(original_overworld);
        field_111189_a = original_overworld.toArray(WeightedRandomChestContent[]::new);

        List<WeightedRandomChestContent> original_underworld = new ArrayList<>(Arrays.asList(chest_contents_for_underworld));
        Handlers.LootTable.onDungeonUnderworldRegister(original_underworld);
        chest_contents_for_underworld = original_underworld.toArray(WeightedRandomChestContent[]::new);
    }
}
