package moddedmite.rustedironcore.core;

import moddedmite.rustedironcore.api.RicRegistry;
import moddedmite.rustedironcore.api.event.Handlers;
import moddedmite.rustedironcore.api.event.handler.SpawnConditionHandler;
import moddedmite.rustedironcore.api.util.IdUtilExtra;
import moddedmite.rustedironcore.core.registries.BuiltInRegistries;
import moddedmite.rustedironcore.core.registries.Registries;
import moddedmite.rustedironcore.data.tags.TagsProvider;
import moddedmite.rustedironcore.mixin.block.entity.ITileEntityMixin;
import moddedmite.rustedironcore.mixin.entity.IEntityListMixin;
import moddedmite.rustedironcore.resources.Identifier;
import moddedmite.rustedironcore.resources.ResourceKey;
import net.minecraft.*;
import net.xiaoyu233.fml.api.block.IBlock;
import net.xiaoyu233.fml.api.item.IItem;

public class RicRegistryImpl implements RicRegistry {
    public static final RicRegistry INSTANCE = new RicRegistryImpl();

    @Override
    public void registerItem(Item item, ResourceLocation id, ResourceLocation texture) {
        item.setUnlocalizedName(id.toString());
        ((IItem) item).setNamespace(id.getResourceDomain());
        ((IItem) item).setItemTextureName(texture.toString());
    }

    @Override
    public void registerBlock(Block block, ResourceLocation id, ResourceLocation texture) {
        block.setUnlocalizedName(id.toString());
        ((IBlock) block).setNamespace(id.getResourceDomain());
        ((IBlock) block).setBlockTextureName(texture.toString());
        Item item = new ItemBlock(block).setUnlocalizedName(id.toString());
        ((IItem) item).setNamespace(id.getResourceDomain());
        item.setMaxStackSize(block.getItemStackLimit());
    }

    @Override
    public void registerEntity(Class<? extends Entity> clazz, ResourceLocation id, int numberId) {
        IEntityListMixin.invokeAddMapping(clazz, id.toString(), numberId);
        BuiltInRegistries.MOD_ENTITY.register(ResourceKey.create(Registries.ENTITY, Identifier.fromRL(id)), clazz);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void registerEntityEgg(Class<? extends Entity> clazz, int color1, int color2) {
        int numberId = EntityList.getEntityID(clazz);
        EntityList.entityEggs.put(numberId, new EntityEggInfo(numberId, color1, color2));
    }

    @Override
    public void registerTileEntity(Class<? extends TileEntity> clazz, ResourceLocation id) {
        ITileEntityMixin.invokeAddMapping(clazz, id.toString());
    }

    @Override
    public int registerTileEntityData(Class<? extends TileEntity> clazz) {
        int type = IdUtilExtra.getNextTileEntityDataType();
        Handlers.TileEntityData.CLASS_MAP.put(type, clazz);
        return type;
    }

    @Override
    public void registerBiome(BiomeGenBase biome, ResourceLocation id) {
        BuiltInRegistries.MOD_BIOME.register(ResourceKey.create(Registries.BIOME, Identifier.fromRL(id)), biome);
    }

    @Override
    public void registerPotion(Potion potion, ResourceLocation id) {
        BuiltInRegistries.MOD_POTION.register(ResourceKey.create(Registries.POTION, Identifier.fromRL(id)), potion);
    }

    @Override
    public void registerSpawnCondition(Class<? extends Entity> clazz, SpawnConditionHandler.SpawnCondition condition) {
        Handlers.SpawnCondition.addCondition(clazz, condition);
    }

    @Override
    public void registerAchievement(Achievement achievement, ResourceLocation id) {
        BuiltInRegistries.ACHIEVEMENT.register(ResourceKey.create(Registries.ACHIEVEMENT, Identifier.fromRL(id)), achievement);
    }

    @Override
    public void registerTagsProvider(TagsProvider<?> tagsProvider, ResourceLocation id) {
        BuiltInRegistries.TAGS_PROVIDER.register(ResourceKey.create(Registries.TAGS_PROVIDER, Identifier.fromRL(id)), tagsProvider);
    }
}
