package moddedmite.rustedironcore.mixin.other.world.structure;

import moddedmite.rustedironcore.api.event.Handlers;
import net.minecraft.ComponentVillageHouse2;
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

@Mixin(ComponentVillageHouse2.class)
public class ComponentSmithMixin {
    @Mutable
    @Shadow
    @Final
    private static WeightedRandomChestContent[] villageBlacksmithChestContents;

    @Inject(method = "<clinit>", at = @At("RETURN"))
    private static void modifyLoot(CallbackInfo ci) {
        List<WeightedRandomChestContent> original = new ArrayList<>(Arrays.asList(villageBlacksmithChestContents));
        Handlers.LootTable.onBlackSmithRegister(original);
        villageBlacksmithChestContents = original.toArray(WeightedRandomChestContent[]::new);
    }
}
