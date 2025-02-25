package moddedmite.rustedironcore.mixin.util;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import moddedmite.rustedironcore.api.event.Handlers;
import net.minecraft.EntityPlayer;
import net.minecraft.FoodStats;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FoodStats.class)
public class FoodStatsMixin {
    @Shadow
    private EntityPlayer player;

    @ModifyReturnValue(method = "getNutritionLimit", at = @At("RETURN"))
    private int onHungerLimitModify(int original) {
        return Handlers.PlayerAttribute.onHungerLimitModify(this.player, original);
    }

    @ModifyReturnValue(method = "getSatiationLimit", at = @At("RETURN"))
    private int onSatiationLimitModify(int original) {
        return Handlers.PlayerAttribute.onSaturationLimitModify(this.player, original);
    }
}
