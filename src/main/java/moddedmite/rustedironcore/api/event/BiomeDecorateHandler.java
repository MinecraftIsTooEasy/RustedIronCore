package moddedmite.rustedironcore.api.event;

import moddedmite.rustedironcore.api.interfaces.IBiomeDecorateListener;
import net.minecraft.BiomeDecorator;

import java.util.ArrayList;
import java.util.List;

public class BiomeDecorateHandler {
    private static BiomeDecorateHandler INSTANCE = new BiomeDecorateHandler();

    public static BiomeDecorateHandler getInstance() {
        return INSTANCE;
    }

    private List<IBiomeDecorateListener> listeners = new ArrayList<>();

    public void registerBiomeDecorateListener(IBiomeDecorateListener listener) {
        if (!this.listeners.contains(listener)) {
            this.listeners.add(listener);
        }
    }

    public void unregisterBiomeDecorateListener(IBiomeDecorateListener listener) {
        this.listeners.remove(listener);
    }

    public void onBiomeDecoratorInit(BiomeDecorator biomeDecorator) {
        this.listeners.forEach(x -> x.onBiomeDecoratorInit(biomeDecorator));
    }

    public void onOresGeneration(BiomeDecorator biomeDecorator) {
        this.listeners.forEach(x -> x.onOresGeneration(biomeDecorator));
    }

    public void onDecorate(BiomeDecorator biomeDecorator) {
        this.listeners.forEach(x -> x.onBiomeDecoratorInit(biomeDecorator));
    }

}
