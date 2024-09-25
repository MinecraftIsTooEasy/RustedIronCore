package moddedmite.rustedironcore.api.item;

import net.minecraft.ItemNugget;
import net.minecraft.Material;

import java.util.HashMap;
import java.util.Map;

public class NuggetItem extends ItemNugget {
    public NuggetItem(int id, Material material) {
        super(id, material);
        materialItemNuggetMap.put(material, this);
    }

    private static final Map<Material, ItemNugget> materialItemNuggetMap = new HashMap<>();

    public static Map<Material, ItemNugget> getMaterialItemNuggetMap() {
        return materialItemNuggetMap;
    }
}
