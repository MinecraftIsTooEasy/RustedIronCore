package moddedmite.rustedironcore.api.event.listener;

import net.minecraft.*;

import java.util.List;


/**
 * You'd better make the method {@link GenLayer#nextInt} public, without it, you can do nothing about random
 */
public interface IBiomeGenerateListener {
    /**
     * This method modifies the list of initial biomes: {@link GenLayerBiome#allowedBiomes}
     * <br>
     * If no mod modifies this, the generation is generally like in vanilla.
     */
    default void onInitialBiomesModify(List<BiomeGenBase> list) {
    }

    /**
     * Here the minecraft transform some plain into ice plain: {@link GenLayerAddSnow}
     */
    default int onLayerAddSnow(GenLayer genLayer, int original) {
        return original;
    }

    /**
     * Here the minecraft transform some ice plain into frozen ocean: {@link GenLayerAddIsland}
     */
    default int onLayerAddIsland(GenLayer genLayer, int original) {
        return original;
    }

    /**
     * Here the minecraft transform some biomes into their hilly variants: {@link GenLayerHills}
     */
    default int onLayerHills(GenLayer genLayer, int original) {
        return original;
    }

    /**
     * Modifies the array: {@link MapGenStronghold#allowedBiomeGenBases}
     */
    default void onStrongholdAllowedRegister(List<BiomeGenBase> original) {
    }
    /**
     * Modifies the list: {@link MapGenVillage#villageSpawnBiomes}
     */
    default void onVillageAllowedRegister(List<BiomeGenBase> original) {
    }

    default void onUnderworldBiomesRegister(List<BiomeGenBase> original) {
    }
}
