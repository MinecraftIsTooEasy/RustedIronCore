package moddedmite.rustedironcore.api.interfaces;

import net.minecraft.BiomeDecorator;

public interface IBiomeDecorateListener {
    default void onBiomeDecoratorInit(BiomeDecorator biomeDecorator) {
    }

    default void onOresGeneration(BiomeDecorator biomeDecorator) {
    }

    default void onDecorate(BiomeDecorator biomeDecorator) {
    }
}
