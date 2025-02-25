package moddedmite.rustedironcore.mixin.item;

import huix.glacier.api.extension.material.IBowMaterial;
import moddedmite.rustedironcore.api.event.Handlers;
import net.minecraft.IDamageableItem;
import net.minecraft.Item;
import net.minecraft.ItemBow;
import net.minecraft.Material;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemBow.class)
public abstract class ItemBowMixin extends Item implements IDamageableItem {
    @Mutable
    @Shadow
    @Final
    private static Material[] possible_arrow_materials;

    @Mutable
    @Shadow
    @Final
    public static String[] bow_pull_icon_name_array;

    @Shadow private Material reinforcement_material;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(int id, Material reinforcement_material, CallbackInfo ci) {
        if (reinforcement_material instanceof IBowMaterial bowMaterial) {
            this.setMaxDamage(bowMaterial.maxDamage());
        }
    }

    @Inject(method = "<clinit>", at = @At("RETURN"))
    private static void addArrowMaterials(CallbackInfo ci) {
        Handlers.ArrowRegister.onRegister();
        Material[] original = possible_arrow_materials;
        Material[] arrowMaterials = Handlers.ArrowRegister.keySet().toArray(Material[]::new);
        Material[] expanded = new Material[original.length + arrowMaterials.length];
        System.arraycopy(original, 0, expanded, 0, original.length);
        System.arraycopy(arrowMaterials, 0, expanded, original.length, arrowMaterials.length);
        possible_arrow_materials = expanded;
        bow_pull_icon_name_array = new String[possible_arrow_materials.length * 3];
        for (int arrow_index = 0; arrow_index < possible_arrow_materials.length; ++arrow_index) {
            Material material = possible_arrow_materials[arrow_index];
            for (int icon_index = 0; icon_index < 3; ++icon_index) {
                bow_pull_icon_name_array[arrow_index * 3 + icon_index] = material.name + "_arrow_" + icon_index;
            }
        }
    }

    @ModifyConstant(method = "addInformation", constant = @Constant(intValue = 10))
    private int modifyBonus(int bonus) {
        if (this.reinforcement_material instanceof IBowMaterial iBowMaterial) {
            return iBowMaterial.velocityBonus();
        }
        return bonus;
    }
}
