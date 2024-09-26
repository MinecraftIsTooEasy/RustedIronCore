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
    public static final PlayerEventHandler PlayerEvent = new PlayerEventHandler();
}
