package moddedmite.rustedironcore.api.interfaces;

import net.minecraft.ResourceLocation;

public interface IPotion {
    default boolean ric$UsesIndividualTexture() {
        return false;
    }

    ResourceLocation ric$GetTexture();
}
