package moddedmite.rustedironcore.api.event.listener;

import net.minecraft.WeightedRandomChestContent;

import java.util.List;

public interface ILootTableRegisterListener {

    default void onDesertPyramidRegister(List<WeightedRandomChestContent> original) {
    }

    default void onJunglePyramidRegister(List<WeightedRandomChestContent> original) {
    }

    default void onFortressRegister(List<WeightedRandomChestContent> original) {
    }

    default void onMineshaftRegister(List<WeightedRandomChestContent> original) {
    }

    default void onStrongholdCorridorRegister(List<WeightedRandomChestContent> original) {
    }

    default void onStrongholdCrossingRegister(List<WeightedRandomChestContent> original) {
    }

    default void onStrongholdLibraryRegister(List<WeightedRandomChestContent> original) {
    }

    default void onSwampHutRegister(List<WeightedRandomChestContent> original) {
    }

    default void onBlackSmithRegister(List<WeightedRandomChestContent> original){
    }

    default void onFishingRegister(List<WeightedRandomChestContent> original){
    }
}
