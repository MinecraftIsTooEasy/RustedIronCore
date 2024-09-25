package moddedmite.rustedironcore.api.event.handler;

import moddedmite.rustedironcore.api.event.AbstractHandler;
import moddedmite.rustedironcore.api.event.listener.IBiomeDecorateListener;
import net.minecraft.BiomeDecorator;

public class BiomeDecorateHandler extends AbstractHandler<IBiomeDecorateListener> {

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
