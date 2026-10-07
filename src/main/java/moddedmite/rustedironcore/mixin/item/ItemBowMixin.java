package moddedmite.rustedironcore.mixin.item;

import huix.glacier.api.extension.material.IBowMaterial;
import moddedmite.rustedironcore.api.event.Handlers;
import net.minecraft.EntityPlayer;
import net.minecraft.IDamageableItem;
import net.minecraft.Icon;
import net.minecraft.IconRegister;
import net.minecraft.Item;
import net.minecraft.ItemBow;
import net.minecraft.Material;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemBow.class)
public abstract class ItemBowMixin extends Item implements IDamageableItem {
    @Shadow
    @Final
    private static Material[] possible_arrow_materials;

    @Shadow
    private Icon[] iconArray;

    @Shadow private Material reinforcement_material;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(int id, Material reinforcement_material, CallbackInfo ci) {
        if (reinforcement_material instanceof IBowMaterial bowMaterial) {
            this.setMaxDamage(bowMaterial.maxDamage());
        }
    }

    @Inject(method = "registerIcons", at = @At("HEAD"))
    private void registerArrowMaterialsBeforeIconRegistration(IconRegister register, CallbackInfo ci) {
        Handlers.ArrowRegister.onRegister();
    }

    @Inject(method = "getItemIconForUseDuration", at = @At("HEAD"), cancellable = true)
    private void guardArrowIconIndex(int pullStage, EntityPlayer player, CallbackInfoReturnable<Icon> cir) {
        if (player.nocked_arrow == null || this.iconArray == null || this.iconArray.length == 0) {
            return;
        }

        int iconIndex = pullStage + player.nocked_arrow.getArrowIndex() * 3;
        if (iconIndex < 0 || iconIndex >= this.iconArray.length) {
            int fallbackIndex = Math.max(0, Math.min(pullStage, this.iconArray.length - 1));
            cir.setReturnValue(this.iconArray[fallbackIndex]);
        }
    }

    @ModifyArg(method = "registerIcons", at = @At(value = "INVOKE", target = "Lnet/minecraft/IconRegister;registerIcon(Ljava/lang/String;)Lnet/minecraft/Icon;", ordinal = 1), index = 0)
    private String applyRegisteredArrowDomain(String texture) {
        String iconName = texture.substring(texture.lastIndexOf('/') + 1);
        for (Material material : possible_arrow_materials) {
            if (iconName.startsWith(material.name + "_arrow_")) {
                return Handlers.ArrowRegister.resolveTexture(material, texture);
            }
        }
        return texture;
    }

    @ModifyConstant(method = "addInformation", constant = @Constant(intValue = 10))
    private int modifyBonus(int bonus) {
        if (this.reinforcement_material instanceof IBowMaterial iBowMaterial) {
            return iBowMaterial.velocityBonus();
        }
        return bonus;
    }
}
