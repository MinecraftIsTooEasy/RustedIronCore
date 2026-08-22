package moddedmite.rustedironcore.api;

import moddedmite.rustedironcore.api.event.handler.SpawnConditionHandler;
import moddedmite.rustedironcore.registry.RicRegistryImpl;
import net.minecraft.*;

public interface RicRegistry {
    static RicRegistry getInstance() {
        return RicRegistryImpl.INSTANCE;
    }

    void registerItem(Item item, ResourceLocation id, ResourceLocation texture);

    void registerBlock(Block block, ResourceLocation id, ResourceLocation texture);

    void registerEntity(Class<? extends Entity> clazz, ResourceLocation id, int numberId);

    void registerEntityEgg(Class<? extends Entity> clazz, int color1, int color2);

    void registerTileEntity(Class<? extends TileEntity> clazz, ResourceLocation id);

    /**
     * Register here if you need {@link TileEntity#getDescriptionPacket}
     */
    int registerTileEntityData(Class<? extends TileEntity> clazz);

    void registerBiome(BiomeGenBase biome, ResourceLocation id);

    void registerPotion(Potion potion, ResourceLocation id);

    void registerSpawnCondition(Class<? extends Entity> clazz, SpawnConditionHandler.SpawnCondition condition);
}
