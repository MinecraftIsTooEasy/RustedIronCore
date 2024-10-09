package moddedmite.rustedironcore.api.event.handler;

import moddedmite.rustedironcore.api.event.AbstractHandler;
import moddedmite.rustedironcore.api.event.listener.ILootTableRegisterListener;
import net.minecraft.WeightedRandomChestContent;

import java.util.List;

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
}
