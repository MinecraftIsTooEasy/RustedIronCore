package moddedmite.rustedironcore.mixin.item;

import huix.glacier.api.extension.material.IComboMaterial;
import moddedmite.rustedironcore.api.item.NuggetItem;
import net.minecraft.ItemNugget;
import net.minecraft.Material;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(ItemNugget.class)
public class ItemNuggetMixin {
    @Inject(method = "getForMaterial", at = @At("HEAD"), cancellable = true)
    private void addMaterials(Material material, CallbackInfoReturnable<ItemNugget> cir) {
        Map<Material, ItemNugget> materialItemNuggetMap = NuggetItem.getMaterialItemNuggetMap();
        if (materialItemNuggetMap.containsKey(material)) {
            cir.setReturnValue(materialItemNuggetMap.get(material));
        }
        if (material instanceof IComboMaterial comboMaterial){
            cir.setReturnValue(comboMaterial.getNugget());
        }
    }
}
