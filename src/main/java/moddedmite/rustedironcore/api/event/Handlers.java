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
    public static final ArrowRegisterHandler ArrowRegister = new ArrowRegisterHandler();
    // this is called after items and blocks are registered
    public static final AbstractHandler<Runnable> PropertiesRegistry = new AbstractHandler<>();
    public static final SmeltingHandler Smelting = new SmeltingHandler();
    public static final SpawnConditionHandler SpawnCondition = new SpawnConditionHandler();
    public static final CraftingHandler Crafting = new CraftingHandler();
    public static final TradingHandler Trading = new TradingHandler();
    public static final CombatHandler Combat = new CombatHandler();
    public static final TickHandler Tick = new TickHandler();
    public static final LootTableHandler LootTable = new LootTableHandler();
    public static final PlayerAttributeHandler PlayerAttribute = new PlayerAttributeHandler();
    public static final EntityTrackerHandler EntityTracker = new EntityTrackerHandler();
    public static final TimedTaskHandler TimedTask = new TimedTaskHandler();
    public static final AbstractHandler<Runnable> PotionRegistry = new AbstractHandler<>();
    public static final AchievementHandler Achievement = new AchievementHandler();
    public static final BarbecueHandler Barbecue = new BarbecueHandler();
    public static final InitializationHandler Initialization = new InitializationHandler();
    public static final WorldLoadHandler WorldLoad = new WorldLoadHandler();
    public static final ChunkLoadHandler ChunkLoad = new ChunkLoadHandler();
    public static final ConnectionHandler Connection = new ConnectionHandler();
}
