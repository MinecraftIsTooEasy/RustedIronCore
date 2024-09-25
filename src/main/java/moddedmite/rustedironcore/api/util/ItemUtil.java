package moddedmite.rustedironcore.api.util;

import net.minecraft.Item;
import net.minecraft.ItemNugget;
import net.minecraft.Material;

public class ItemUtil {
    public static ItemNugget getNuggetForMaterial(Material material) {
        return Item.adamantiumNugget.getForMaterial(material);
    }
}
