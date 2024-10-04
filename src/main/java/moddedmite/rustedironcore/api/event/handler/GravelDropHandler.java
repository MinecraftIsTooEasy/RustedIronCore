package moddedmite.rustedironcore.api.event.handler;

import moddedmite.rustedironcore.api.event.AbstractHandler;
import moddedmite.rustedironcore.api.event.listener.IGravelDropListener;
import moddedmite.rustedironcore.random.FloatWeightedEntry;
import moddedmite.rustedironcore.random.RandomUtil;
import net.minecraft.BlockBreakInfo;
import net.minecraft.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

public class GravelDropHandler extends AbstractHandler<IGravelDropListener> {
    public GravelDropHandler() {
        this.register(GravelDropHandler.VanillaListener);
        this.registerGravelLootEntry(GravelDropHandler.CopperEntry);
        this.registerGravelLootEntry(GravelDropHandler.SilverEntry);
        this.registerGravelLootEntry(GravelDropHandler.GoldEntry);
        this.registerGravelLootEntry(GravelDropHandler.ObsidianEntry);
        this.registerGravelLootEntry(GravelDropHandler.EmeraldEntry);
        this.registerGravelLootEntry(GravelDropHandler.DiamondEntry);
        this.registerGravelLootEntry(GravelDropHandler.MithrilEntry);
        this.registerGravelLootEntry(GravelDropHandler.AdamantiumEntry);
    }

    public static final IGravelDropListener VanillaListener = new IGravelDropListener() {
        @Override
        public int onNetherGravelDropIDModify(BlockBreakInfo info, int original) {
            if (original == Item.copperNugget.itemID || original == Item.silverNugget.itemID || original == Item.mithrilNugget.itemID || original == Item.adamantiumNugget.itemID) {
                return Item.goldNugget.itemID;
            }
            if (original == Item.shardObsidian.itemID || original == Item.shardEmerald.itemID || original == Item.shardDiamond.itemID) {
                return Item.shardNetherQuartz.itemID;
            }
            return original;
        }
    };

    public int onNetherGravelDropIDModify(BlockBreakInfo info, int original) {
        for (IGravelDropListener listener : this.listeners) {
            original = listener.onNetherGravelDropIDModify(info, original);
        }
        return original;
    }

    private final List<GravelLootEntry> lootEntries = new ArrayList<>();

    public void registerGravelLootEntry(GravelLootEntry gravelLootEntry) {
        this.lootEntries.add(gravelLootEntry);
    }

    public void unregisterGravelLootEntry(GravelLootEntry gravelLootEntry){
        this.lootEntries.remove(gravelLootEntry);
    }

    public int getRandomDropID(BlockBreakInfo info, Random random) {
        GravelLootEntry randomEntry = RandomUtil.getRandomEntryFloat(this.lootEntries, random);
        if (randomEntry != null) return randomEntry.dropFunc.apply(info);
        return -1;
    }

    public static final GravelLootEntry CopperEntry = new GravelLootEntry((float) 2 / 3, blockBreakInfo -> Item.copperNugget.itemID);
    public static final GravelLootEntry SilverEntry = new GravelLootEntry((float) 2 / 9, blockBreakInfo -> Item.silverNugget.itemID);
    public static final GravelLootEntry GoldEntry = new GravelLootEntry((float) 2 / 27, blockBreakInfo -> Item.goldNugget.itemID);
    public static final GravelLootEntry ObsidianEntry = new GravelLootEntry((float) 2 / 81, blockBreakInfo -> blockBreakInfo.wasExploded() ? -1 : Item.shardObsidian.itemID);
    public static final GravelLootEntry EmeraldEntry = new GravelLootEntry((float) 2 / 243, blockBreakInfo -> blockBreakInfo.wasExploded() ? -1 : Item.shardEmerald.itemID);
    public static final GravelLootEntry DiamondEntry = new GravelLootEntry((float) 2 / 729, blockBreakInfo -> blockBreakInfo.wasExploded() ? -1 : Item.shardDiamond.itemID);
    public static final GravelLootEntry MithrilEntry = new GravelLootEntry((float) 2 / 2187, blockBreakInfo -> Item.mithrilNugget.itemID);
    public static final GravelLootEntry AdamantiumEntry = new GravelLootEntry((float) 2 / 4374, blockBreakInfo -> Item.adamantiumNugget.itemID);

    public record GravelLootEntry(float weight,
                                  Function<BlockBreakInfo, Integer> dropFunc) implements FloatWeightedEntry {
        @Override
        public float getWeight() {
            return this.weight;
        }
    }

}
