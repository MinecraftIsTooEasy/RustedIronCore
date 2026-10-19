package moddedmite.rustedironcore.util;

import moddedmite.rustedironcore.api.util.LogUtil;
import moddedmite.rustedironcore.core.registries.BuiltInRegistries;
import moddedmite.rustedironcore.core.registries.Registries;
import moddedmite.rustedironcore.mixin.block.IBlockMixin;
import moddedmite.rustedironcore.mixin.item.IItemMixin;
import moddedmite.rustedironcore.resources.Identifier;
import moddedmite.rustedironcore.resources.ResourceKey;
import net.minecraft.Block;
import net.minecraft.Item;
import net.minecraft.ItemBlock;
import org.apache.logging.log4j.Logger;
import org.jspecify.annotations.Nullable;

public class RegistryUtil {
    private static final Logger LOGGER = LogUtil.getLogger();

    @Nullable
    public static Identifier getId(Block block) {
        String name = ((IBlockMixin) block).ric$getUnlocalizedName();
        if (name == null) return null;
        return Identifier.parse(name);
    }

    @Nullable
    public static Identifier getId(Item item) {
        if (item instanceof ItemBlock itemBlock) return getId(itemBlock.getBlock());
        String name = ((IItemMixin) item).ric$getUnlocalizedName();
        if (name == null) return null;
        return Identifier.parse(name);
    }

    public static Block toBlock(Identifier identifier) {
        return BuiltInRegistries.BLOCK.getValue(identifier);
    }

    public static Item toItem(Identifier identifier) {
        return BuiltInRegistries.ITEM.getValue(identifier);
    }

    public static void buildCopy() {
        for (Block block : Block.blocksList) {
            if (block == null) continue;
            Identifier id = getId(block);
            if (id == null) {
                LOGGER.warn("id not set for block {}", block);
                continue;
            }
            if (BuiltInRegistries.BLOCK.containsKey(id)) {
                LOGGER.warn("duplicate id {} for blocks {} and {}", id, BuiltInRegistries.BLOCK.getValue(id), block);
                continue;
            }
            BuiltInRegistries.BLOCK.register(ResourceKey.create(Registries.BLOCK, id), block);
        }
        for (Item item : Item.itemsList) {
            if (item == null) continue;
            Identifier id = getId(item);
            if (id == null) {
                LOGGER.warn("id not set for item {}", item);
                continue;
            }
            if (BuiltInRegistries.ITEM.containsKey(id)) {
                LOGGER.warn("duplicate id {} for items {} and {}", id, BuiltInRegistries.ITEM.getValue(id), item);
                continue;
            }
            BuiltInRegistries.ITEM.register(ResourceKey.create(Registries.ITEM, id), item);
        }
    }
}
