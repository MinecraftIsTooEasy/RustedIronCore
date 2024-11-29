package moddedmite.rustedironcore.api.event.listener;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ItemArmor;
import net.minecraft.ModelBiped;
import net.minecraft.ResourceLocation;

import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
public interface IArmorModelListener {
    // slotIndex: 0 for helmet, ... , 3 for boots
    default ResourceLocation getArmorTexture(ItemArmor itemArmor, int slotIndex) {
        return null;
    }

    // the model you used must register here, to sync some property
    default void onArmorModelRegister(Consumer<ModelBiped> registry) {
    }

    // return the model you use
    default ModelBiped getArmorModel(ItemArmor itemArmor, int slotIndex) {
        return null;
    }
}
