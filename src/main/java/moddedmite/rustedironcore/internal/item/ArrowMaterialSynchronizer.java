package moddedmite.rustedironcore.internal.item;

import moddedmite.rustedironcore.mixin.accessor.ItemArrowAccessor;
import moddedmite.rustedironcore.mixin.accessor.ItemBowAccessor;
import net.minecraft.ItemArrow;
import net.minecraft.Material;

public final class ArrowMaterialSynchronizer {

    public static void register(Material material) {
        Material[] arrowMaterials = ItemArrow.material_types;
        Material[] expandedArrowMaterials = append(arrowMaterials, material);
        if (expandedArrowMaterials != arrowMaterials) {
            ItemArrowAccessor.rustedIronCore$setMaterialTypes(expandedArrowMaterials);
        }

        Material[] bowMaterials = ItemBowAccessor.rustedIronCore$getPossibleArrowMaterials();
        Material[] expandedBowMaterials = append(bowMaterials, material);
        if (expandedBowMaterials == bowMaterials) {
            return;
        }

        ItemBowAccessor.rustedIronCore$setPossibleArrowMaterials(expandedBowMaterials);
        ItemBowAccessor.rustedIronCore$setBowPullIconNameArray(createBowPullIconNames(expandedBowMaterials));
    }

    private static Material[] append(Material[] materials, Material material) {
        for (Material registeredMaterial : materials) {
            if (registeredMaterial == material) {
                return materials;
            }
        }

        Material[] expanded = new Material[materials.length + 1];
        System.arraycopy(materials, 0, expanded, 0, materials.length);
        expanded[materials.length] = material;
        return expanded;
    }

    private static String[] createBowPullIconNames(Material[] materials) {
        String[] names = new String[materials.length * 3];
        for (int arrowIndex = 0; arrowIndex < materials.length; arrowIndex++) {
            for (int pullStage = 0; pullStage < 3; pullStage++) {
                names[arrowIndex * 3 + pullStage] = materials[arrowIndex].name + "_arrow_" + pullStage;
            }
        }
        return names;
    }
}
