package moddedmite.rustedironcore.api.event.events;

import moddedmite.rustedironcore.api.event.Handlers;
import moddedmite.rustedironcore.api.util.IdUtilExtra;
import moddedmite.rustedironcore.villager.VillagerSettings;
import net.minecraft.Item;
import net.minecraft.ResourceLocation;
import net.minecraft.Tuple;

import java.util.Map;
import java.util.Optional;

public record TradingRegisterEvent(Map<Integer, Tuple> villagerStockList, Map<Integer, Tuple> blacksmithSellingList) {
    public VillagerSettings registerProfession(ResourceLocation texture) {
        return registerProfession(IdUtilExtra.getNextVillagerProfessionID(), texture);
    }

    public VillagerSettings registerProfession(int profession, ResourceLocation texture) {
        return this.registerProfession(profession, "villager.profession.unknown", texture);
    }

    public VillagerSettings registerProfession(int profession, String name, ResourceLocation texture) {
        return this.registerProfession(profession, new VillagerSettings(profession, name, texture));
    }

    public VillagerSettings registerProfession(int profession, VillagerSettings settings) {
        Handlers.Trading.registerProfession(profession, settings);
        return settings;
    }

    public Optional<VillagerSettings> getForProfession(int profession) {
        return Handlers.Trading.getForProfession(profession);
    }

    public void registerVillagerStock(Item item, int minCount, int maxCount) {
        int itemStackLimit = item.getItemStackLimit(0, 0);
        if (minCount > itemStackLimit) {
            minCount = itemStackLimit;
        }
        if (maxCount > itemStackLimit) {
            maxCount = itemStackLimit;
        }
        this.villagerStockList.put(item.itemID, new Tuple(minCount, maxCount));
    }

    @Deprecated(since = "1.3.6")
    public void registerVillagerStock(int id, Tuple data) {
        this.registerVillagerStock(Item.getItem(id), ((int) data.getFirst()), ((int) data.getSecond()));
    }

    // negative count: one emerald for many items
    // positive count: one item for many emeralds
    public void registerBlackSmithSelling(Item item, int minCount, int maxCount) {
        this.blacksmithSellingList.put(item.itemID, new Tuple(minCount, maxCount));
    }

    @Deprecated(since = "1.3.6")
    public void registerBlackSmithSelling(int id, Tuple data) {
        this.blacksmithSellingList.put(id, data);
    }
}
