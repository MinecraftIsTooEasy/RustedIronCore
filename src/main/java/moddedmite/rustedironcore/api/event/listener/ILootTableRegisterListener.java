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

    default void onBlackSmithRegister(List<WeightedRandomChestContent> original) {
    }

    default void onDungeonOverworldRegister(List<WeightedRandomChestContent> original) {
    }

    default void onDungeonUnderworldRegister(List<WeightedRandomChestContent> original) {
    }

    /**
     * Treat the original weight as 80 fish and 20 large fish, and fill in your custom weights.
     */
    default void onFishingRegister(List<WeightedRandomChestContent> original) {
    }
}
