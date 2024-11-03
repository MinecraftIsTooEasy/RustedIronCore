package huix.glacier.mixin.item;

import huix.glacier.api.extension.material.IBowMaterial;
import huix.glacier.util.GlacierHelper;
import net.minecraft.Item;
import net.minecraft.ItemBow;
import net.minecraft.Material;
import net.minecraft.Translator;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;

@Mixin(ItemBow.class)
public class ItemBowMixin {

    @Shadow
    private Material reinforcement_material;

    @Shadow
    @Final
    @Mutable
    private static Material[] possible_arrow_materials;
    @Shadow
    @Final
    @Mutable
    public static String[] bow_pull_icon_name_array;

    @Redirect(method = "<init>",at = @At(value = "INVOKE", target = "Lnet/minecraft/ItemBow;setMaxDamage(I)Lnet/minecraft/Item;"))
    private Item setMaterialDamage(ItemBow instance, int i){
        if (reinforcement_material instanceof IBowMaterial bowMaterial) {
            return instance.setMaxDamage(bowMaterial.maxDamage());
        }
        return instance.setMaxDamage(reinforcement_material == Material.mithril ? 128 : (reinforcement_material == Material.ancient_metal ? 64 : 32));
    }

    @Inject(method = "<clinit>",at = @At(value = "FIELD", target = "Lnet/minecraft/ItemBow;possible_arrow_materials:[Lnet/minecraft/Material;"
            , ordinal = 0, shift = At.Shift.AFTER))
    private static void removeOrdinal(CallbackInfo callbackInfo){
        possible_arrow_materials = new Material[]{};
    }

    @Inject(method = "<clinit>",at = @At(value = "RETURN"))
    private static void injectClinit(CallbackInfo callbackInfo){
        ArrayList<Material> materialTypes = GlacierHelper.arrow_material_types;
        bow_pull_icon_name_array = new String[materialTypes.size() * 3];

        for(int arrow_index = 0; arrow_index < materialTypes.size(); ++arrow_index) {
            Material material = materialTypes.get(arrow_index);

            for(int icon_index = 0; icon_index < 3; ++icon_index) {
                bow_pull_icon_name_array[arrow_index * 3 + icon_index] = material.name + "_arrow_" + icon_index;
            }
        }
    }


    @Redirect(method = "addInformation", at = @At(value = "INVOKE", target = "Lnet/minecraft/Translator;getFormatted(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;"))
    private String injectAddInfo(String key, Object[] par1ArrayOfObj){
        if (this.reinforcement_material instanceof IBowMaterial bowMaterial){
            return Translator.getFormatted(key,bowMaterial.velocityBonus());
        }
        return Translator.getFormatted(key,par1ArrayOfObj);
    }
}
