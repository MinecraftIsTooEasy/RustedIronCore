package moddedmite.rustedironcore.api.event;

import moddedmite.rustedironcore.api.event.handler.*;

public class Handlers {
    public static final BeaconUpdateHandler BeaconUpdate = new BeaconUpdateHandler();
    public static final BiomeDecorateHandler BiomeDecorate = new BiomeDecorateHandler();
    public static final BiomeGenerateHandler BiomeGenerate = new BiomeGenerateHandler();
    public static final EnchantingHandler Enchanting = new EnchantingHandler();
    public static final EntityMobMixinHandler EntityMobMixin = new EntityMobMixinHandler();
    public static final FurnaceUpdateHandler FurnaceUpdate = new FurnaceUpdateHandler();
    public static final GravelDropHandler GravelDrop = new GravelDropHandler();

    static {
        GravelDrop.register(GravelDropHandler.VanillaListener);
        GravelDrop.registerGravelLootEntry(GravelDropHandler.CopperEntry);
        GravelDrop.registerGravelLootEntry(GravelDropHandler.SilverEntry);
        GravelDrop.registerGravelLootEntry(GravelDropHandler.GoldEntry);
        GravelDrop.registerGravelLootEntry(GravelDropHandler.ObsidianEntry);
        GravelDrop.registerGravelLootEntry(GravelDropHandler.EmeraldEntry);
        GravelDrop.registerGravelLootEntry(GravelDropHandler.DiamondEntry);
        GravelDrop.registerGravelLootEntry(GravelDropHandler.MithrilEntry);
        GravelDrop.registerGravelLootEntry(GravelDropHandler.AdamantiumEntry);
    }
}
