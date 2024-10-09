package moddedmite.rustedironcore.api.event.events;

import moddedmite.rustedironcore.api.event.Handlers;
import moddedmite.rustedironcore.villager.VillagerSettings;
import net.minecraft.ResourceLocation;
import net.minecraft.Tuple;

import java.util.Map;
import java.util.Optional;

public record TradingRegisterEvent(Map<Integer, Tuple> villagerStockList, Map<Integer, Tuple> blacksmithSellingList) {

    public VillagerSettings registerProfession(int profession, ResourceLocation texture) {
        return this.registerProfession(profession, new VillagerSettings(profession, texture));
    }

    public VillagerSettings registerProfession(int profession, VillagerSettings settings) {
        Handlers.Trading.registerProfession(profession, settings);
        return settings;
    }

    public Optional<VillagerSettings> getForProfession(int profession) {
        return Handlers.Trading.getForProfession(profession);
    }

    public void registerVillagerStock(int id, Tuple data) {
        this.villagerStockList.put(id, data);
    }

    public void registerBlackSmithSelling(int id, Tuple data) {
        this.blacksmithSellingList.put(id, data);
    }
}
