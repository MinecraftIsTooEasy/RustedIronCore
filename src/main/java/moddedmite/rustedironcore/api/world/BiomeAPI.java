package moddedmite.rustedironcore.api.world;

import net.minecraft.BiomeGenBase;

public interface BiomeAPI {
    default String getBiomeUnlocalizedName() {
        return null;
    }

    default String setBiomeUnlocalizedName(String unlocalizedName) {
        return null;
    }

    static String getBiomeUnlocalizedName(BiomeGenBase biome) {
        return ((BiomeAPI) biome).getBiomeUnlocalizedName();
    }
}
