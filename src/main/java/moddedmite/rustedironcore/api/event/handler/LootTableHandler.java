package moddedmite.rustedironcore.api.event.handler;

import moddedmite.rustedironcore.api.event.AbstractHandler;
import moddedmite.rustedironcore.api.event.listener.ILootTableRegisterListener;
import net.minecraft.Item;
import net.minecraft.ItemStack;
import net.minecraft.WeightedRandom;
import net.minecraft.WeightedRandomChestContent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LootTableHandler extends AbstractHandler<ILootTableRegisterListener> {
    public void onDesertPyramidRegister(List<WeightedRandomChestContent> original) {
        this.listeners.forEach(x -> x.onDesertPyramidRegister(original));
    }

    public void onJunglePyramidRegister(List<WeightedRandomChestContent> original) {
        this.listeners.forEach(x -> x.onJunglePyramidRegister(original));
    }

    public void onFortressRegister(List<WeightedRandomChestContent> original) {
        this.listeners.forEach(x -> x.onFortressRegister(original));
    }

    public void onMineshaftRegister(List<WeightedRandomChestContent> original) {
        this.listeners.forEach(x -> x.onMineshaftRegister(original));
    }

    public void onStrongholdCorridorRegister(List<WeightedRandomChestContent> original) {
        this.listeners.forEach(x -> x.onStrongholdCorridorRegister(original));
    }

    public void onStrongholdCrossingRegister(List<WeightedRandomChestContent> original) {
        this.listeners.forEach(x -> x.onStrongholdCrossingRegister(original));
    }

    public void onStrongholdLibraryRegister(List<WeightedRandomChestContent> original) {
        this.listeners.forEach(x -> x.onStrongholdLibraryRegister(original));
    }

    public void onSwampHutRegister(List<WeightedRandomChestContent> original) {
        this.listeners.forEach(x -> x.onSwampHutRegister(original));
    }

    public void onBlackSmithRegister(List<WeightedRandomChestContent> original) {
        this.listeners.forEach(x -> x.onBlackSmithRegister(original));
    }

    public void onDungeonOverworldRegister(List<WeightedRandomChestContent> original) {
        this.listeners.forEach(x -> x.onDungeonOverworldRegister(original));
    }

    public void onDungeonUnderworldRegister(List<WeightedRandomChestContent> original) {
        this.listeners.forEach(x -> x.onDungeonUnderworldRegister(original));
    }

    private final List<WeightedRandomChestContent> fishingEntries = new ArrayList<>();

    /**
     * Treat the original weight as 80 fish and 20 large fish.
     * <br>
     * The entry I added is dummy, because if this entry is selected, I will cancel injecting.
     */
    public void onFishingRegister() {
        this.fishingEntries.add(new WeightedRandomChestContent(Item.fishRaw.itemID, 0, 1, 1, 100));
        this.listeners.forEach(x -> x.onFishingRegister(this.fishingEntries));
    }

    public ItemStack getFishingResult(Random rand) {
        WeightedRandomChestContent randomItem = (WeightedRandomChestContent) WeightedRandom.getRandomItem(rand, this.fishingEntries);
        return randomItem.theItemId;
    }
}
