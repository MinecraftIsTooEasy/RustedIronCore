package moddedmite.rustedironcore.api.event.handler;

import moddedmite.rustedironcore.api.event.AbstractHandler;
import moddedmite.rustedironcore.api.event.listener.IBiomeGenerateListener;
import net.minecraft.BiomeGenBase;
import net.minecraft.GenLayer;

import java.util.List;

public class BiomeGenerateHandler extends AbstractHandler<IBiomeGenerateListener> {
    public void onInitialBiomesModify(List<BiomeGenBase> list) {
        this.listeners.forEach(x -> x.onInitialBiomesModify(list));
    }

    public int onLayerAddSnow(GenLayer genLayer, int original) {
        for (IBiomeGenerateListener listener : this.listeners) {
            original = listener.onLayerAddSnow(genLayer, original);
        }
        return original;
    }

    public int onLayerAddIsland(GenLayer genLayer, int original) {
        for (IBiomeGenerateListener listener : this.listeners) {
            original = listener.onLayerAddIsland(genLayer, original);
        }
        return original;
    }

    public int onLayerHills(GenLayer genLayer, int original) {
        for (IBiomeGenerateListener listener : this.listeners) {
            original = listener.onLayerHills(genLayer, original);
        }
        return original;
    }
}
