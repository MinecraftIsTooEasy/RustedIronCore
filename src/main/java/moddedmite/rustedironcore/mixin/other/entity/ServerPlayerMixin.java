package moddedmite.rustedironcore.mixin.other.entity;

import moddedmite.rustedironcore.network.Network;
import moddedmite.rustedironcore.network.packets.S2CUpdateNutrition;
import net.minecraft.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin {
    @Shadow
    private int phytonutrients;
    @Shadow
    private int protein;
    @Shadow
    private int essential_fats;
    @Unique
    private int last_phytonutrients;
    @Unique
    private int last_protein;
    @Unique
    private int last_essential_fats;

    @Inject(method = {"onUpdateEntity"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/FoodStats;getHunger()F")})
    private void updateNutrition(CallbackInfo ci) {
        if (this.phytonutrients != this.last_phytonutrients || this.protein != this.last_protein || this.essential_fats != this.last_essential_fats) {
            Network.sendToClient((ServerPlayer) (Object) this, new S2CUpdateNutrition(this.phytonutrients, this.protein, this.essential_fats));
            this.last_phytonutrients = this.phytonutrients;
            this.last_protein = this.protein;
            this.last_essential_fats = this.essential_fats;
        }
    }
}

