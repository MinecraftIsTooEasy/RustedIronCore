package huix.glacier.api.extension.material;

import net.minecraft.ItemIngot;
import net.minecraft.ItemNugget;

public interface IComboMaterial {

    ItemNugget getNugget();
    ItemIngot getIngot();
}
