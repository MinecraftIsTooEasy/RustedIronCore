package moddedmite.rustedironcore.mixin.other.entity.player;

import moddedmite.rustedironcore.Constants;
import moddedmite.rustedironcore.api.event.Handlers;
import moddedmite.rustedironcore.api.player.ServerPlayerAPI;
import moddedmite.rustedironcore.network.Network;
import moddedmite.rustedironcore.network.packets.S2CUpdateNutrition;
import net.minecraft.*;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin extends EntityPlayer implements ServerPlayerAPI {
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
    @Unique
    private int nutritionSyncCountDown = 0;
    @Unique
    private int nutritionLimit;
    @Unique
    private int insulinLimit;

    public ServerPlayerMixin(World par1World, String par2Str) {
        super(par1World, par2Str);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(MinecraftServer par1MinecraftServer, World par2World, String par3Str, ItemInWorldManager par4ItemInWorldManager, CallbackInfo ci) {
        this.nutritionLimit = Handlers.PlayerAttribute.onNutritionLimitModify((EntityPlayer) (Object) this, 160000);
        this.insulinLimit = Handlers.PlayerAttribute.onInsulinResistanceLimitModify((EntityPlayer) (Object) this, 192000);
        int nutritionInit = Handlers.PlayerAttribute.onNutritionInitModify((EntityPlayer) (Object) this, 160000);
        this.phytonutrients = nutritionInit;
        this.essential_fats = nutritionInit;
        this.protein = nutritionInit;
    }

    @Inject(method = {"onUpdateEntity"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/FoodStats;getHunger()F")})
    private void updateNutrition(CallbackInfo ci) {
        if (this.nutritionSyncCountDown == 0) {
            this.syncNutrition();
            this.nutritionSyncCountDown = Constants.NutritionSyncTicks;
        } else {
            this.nutritionSyncCountDown--;
        }
    }

    @Override
    public int ric$GetNutritionLimit() {
        return this.nutritionLimit;
    }

    @Unique
    private void syncNutrition() {
        if (this.phytonutrients != this.last_phytonutrients || this.protein != this.last_protein || this.essential_fats != this.last_essential_fats) {
            this.sendNutritionPacket();
            this.last_phytonutrients = this.phytonutrients;
            this.last_protein = this.protein;
            this.last_essential_fats = this.essential_fats;
        }
    }

    @ModifyArg(method = "setProtein", at = @At(value = "INVOKE", target = "Lnet/minecraft/MathHelper;clamp_int(III)I"), index = 2)
    private int widenProtein(int par0) {
        return this.nutritionLimit;
    }

    @ModifyArg(method = "setEssentialFats", at = @At(value = "INVOKE", target = "Lnet/minecraft/MathHelper;clamp_int(III)I"), index = 2)
    private int widenFat(int par0) {
        return this.nutritionLimit;
    }

    @ModifyArg(method = "setPhytonutrients", at = @At(value = "INVOKE", target = "Lnet/minecraft/MathHelper;clamp_int(III)I"), index = 2)
    private int widenPhytonutrients(int par0) {
        return this.nutritionLimit;
    }

    @ModifyArg(method = "setInsulinResistance", at = @At(value = "INVOKE", target = "Lnet/minecraft/MathHelper;clamp_int(III)I"), index = 2)
    private int widenInsulinResistance(int par0) {
        return this.insulinLimit;
    }

    @Inject(method = "addNutrients", at = @At("RETURN"))
    private void onNutrientsChange(Item item, CallbackInfo ci) {
        this.sendNutritionPacket();
    }

    @Unique
    private void sendNutritionPacket() {
        Network.sendToClient((ServerPlayer) (Object) this, new S2CUpdateNutrition(this.phytonutrients, this.protein, this.essential_fats));
    }

    @Inject(method = "travelToDimension", at = @At("HEAD"))
    private void onDimensionTravel(int par1, CallbackInfo ci) {
        Handlers.Achievement.onDimensionTravel(this, this.dimension, par1);
    }
}

