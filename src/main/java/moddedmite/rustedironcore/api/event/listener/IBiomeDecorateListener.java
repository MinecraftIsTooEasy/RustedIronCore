package moddedmite.rustedironcore.api.event.listener;

import net.minecraft.BiomeDecorator;

public interface IBiomeDecorateListener {
    default void onBiomeDecoratorInit(BiomeDecorator biomeDecorator) {
    }

    default void onOresGeneration(BiomeDecorator biomeDecorator) {
    }

    default void onDecorate(BiomeDecorator biomeDecorator) {
    }
}
