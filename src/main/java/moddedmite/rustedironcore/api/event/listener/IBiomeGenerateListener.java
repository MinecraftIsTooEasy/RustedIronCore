package moddedmite.rustedironcore.api.event.listener;

import net.minecraft.BiomeGenBase;
import net.minecraft.GenLayer;

import java.util.List;

// you'd better make the method GenLayer#nextInt public, without it, you can do nothing about random
public interface IBiomeGenerateListener {
    // if you call this, the generation is entirely different from vanilla
    default void onInitialBiomesModify(List<BiomeGenBase> list) {
    }

    // here the minecraft transform some plain into ice plain
    default int onLayerAddSnow(GenLayer genLayer, int original) {
        return original;
    }

    // here the minecraft transform some ice plain into frozen ocean
    default int onLayerAddIsland(GenLayer genLayer, int original) {
        return original;
    }

    // here the minecraft transform some biomes into their hilly variants
    default int onLayerHills(GenLayer genLayer, int original) {
        return original;
    }


}
