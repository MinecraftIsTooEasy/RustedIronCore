package moddedmite.rustedironcore.api.register;

import net.minecraft.Material;

import java.util.LinkedHashMap;
import java.util.Map;

public class MaterialProperties {

    public static Map<Material, Float> arrowMaterials = new LinkedHashMap<>();

    public static void registerArrowMaterial(Material material, float chanceOfRecovery) {
        arrowMaterials.put(material, chanceOfRecovery);
    }// should be called very early, for example the clinit part of your Materials class
}
