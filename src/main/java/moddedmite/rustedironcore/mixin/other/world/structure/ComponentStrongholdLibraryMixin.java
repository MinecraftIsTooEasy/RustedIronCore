package moddedmite.rustedironcore.mixin.other.world.structure;

import moddedmite.rustedironcore.api.event.Handlers;
import net.minecraft.ComponentStrongholdLibrary;
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

@Mixin(ComponentStrongholdLibrary.class)
public class ComponentStrongholdLibraryMixin {
    @Mutable
    @Shadow
    @Final
    private static WeightedRandomChestContent[] strongholdLibraryChestContents;

    @Inject(method = "<clinit>", at = @At("RETURN"))
    private static void addLoot(CallbackInfo ci) {
        List<WeightedRandomChestContent> original = new ArrayList<>(Arrays.asList(strongholdLibraryChestContents));
        Handlers.LootTable.onStrongholdLibraryRegister(original);
        strongholdLibraryChestContents = original.toArray(WeightedRandomChestContent[]::new);
    }
}
