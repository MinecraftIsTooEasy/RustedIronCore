package moddedmite.rustedironcore.util;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import moddedmite.rustedironcore.api.util.LogUtil;
import moddedmite.rustedironcore.core.Registry;
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
    private static final IntSet EXCEPTIONS = new IntOpenHashSet();
    // TODO push modders to fix
    private static final boolean HANDLE_MISSING_ID = true;
    private static final boolean LOG_ID_DUPLICATION = false;
    // TODO temporary solution
    private static final boolean SUFFIX_FOR_ID_DUPLICATION = true;

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
//        putExceptions();

        iterateBlocks();
        iterateItems();

        EXCEPTIONS.clear();
    }

    private static void iterateBlocks() {
        for (Block block : Block.blocksList) {
            if (block == null) continue;
            if (EXCEPTIONS.contains(block.blockID)) continue;
            Identifier id = getId(block);
            if (id == null) {
                LOGGER.warn("id not set for block {}: {}", block.blockID, block.getClass());
                if (HANDLE_MISSING_ID) {
                    id = Identifier.withDefaultNamespace("missing");
                    do {
                        id = Identifier.withDefaultNamespace(id.getPath() + "_");
                    } while (BuiltInRegistries.BLOCK.containsKey(id));
                    registerBlock(id, block);
                }
                continue;
            }
            if (BuiltInRegistries.BLOCK.containsKey(id)) {
                if (LOG_ID_DUPLICATION) {
                    LOGGER.warn("duplicate id {} for blocks:", id);
                    Block registeredBlock = BuiltInRegistries.BLOCK.getValue(id);
                    LOGGER.warn("{}: {}", registeredBlock.blockID, registeredBlock);
                    LOGGER.warn("{}: {}", block.blockID, block);
                }
                if (SUFFIX_FOR_ID_DUPLICATION) {
                    do {
                        id = Identifier.fromNamespaceAndPath(id.getNamespace(), id.getPath() + "_");
                    } while (BuiltInRegistries.BLOCK.containsKey(id));
                    registerBlock(id, block);
                }
                continue;
            }
            registerBlock(id, block);
        }
    }

    private static void iterateItems() {
        for (Item item : Item.itemsList) {
            if (item == null) continue;
            if (EXCEPTIONS.contains(item.itemID)) continue;
            Identifier id = getId(item);
            if (id == null) {
                LOGGER.warn("id not set for item {}: {}", item.itemID, item.getClass());
                if (HANDLE_MISSING_ID) {
                    id = Identifier.withDefaultNamespace("missing");
                    do {
                        id = Identifier.withDefaultNamespace(id.getPath() + "_");
                    } while (BuiltInRegistries.ITEM.containsKey(id));
                    registerItem(id, item);
                }
                continue;
            }
            if (BuiltInRegistries.ITEM.containsKey(id)) {
                if (LOG_ID_DUPLICATION) {
                    LOGGER.warn("duplicate id {} for items:", id);
                    Item registeredItem = BuiltInRegistries.ITEM.getValue(id);
                    LOGGER.warn("{}: {}", registeredItem.itemID, registeredItem);
                    LOGGER.warn("{}: {}", item.itemID, item);
                }
                if (SUFFIX_FOR_ID_DUPLICATION) {
                    do {
                        id = Identifier.fromNamespaceAndPath(id.getNamespace(), id.getPath() + "_");
                    } while (BuiltInRegistries.ITEM.containsKey(id));
                    registerItem(id, item);
                }
                continue;
            }
            registerItem(id, item);
        }
    }

    /**
     * Forge 1.12 names used
     */
    private static void putExceptions() {
        registerBlockException(Identifier.withDefaultNamespace("flowing_water"), Block.waterMoving);
        registerBlockException(Identifier.withDefaultNamespace("water"), Block.waterStill);
        registerBlockException(Identifier.withDefaultNamespace("flowing_lava"), Block.lavaMoving);
        registerBlockException(Identifier.withDefaultNamespace("lava"), Block.lavaStill);
        registerBlockException(Identifier.withDefaultNamespace("brown_mushroom"), Block.mushroomBrown);
        registerBlockException(Identifier.withDefaultNamespace("red_mushroom"), Block.mushroomRed);
        registerBlockException(Identifier.withDefaultNamespace("double_stone_slab"), Block.stoneDoubleSlab);
        registerBlockException(Identifier.withDefaultNamespace("stone_slab"), Block.stoneSingleSlab);
        registerBlockException(Identifier.withDefaultNamespace("furnace"), Block.furnaceIdle);
        registerBlockException(Identifier.withDefaultNamespace("lit_furnace"), Block.furnaceBurning);
        registerBlockException(Identifier.withDefaultNamespace("standing_sign"), Block.signPost);
        registerBlockException(Identifier.withDefaultNamespace("wall_sign"), Block.signWall);
        registerBlockException(Identifier.withDefaultNamespace("stone_pressure_plate"), Block.pressurePlateStone);
        registerBlockException(Identifier.withDefaultNamespace("wooden_pressure_plate"), Block.pressurePlatePlanks);
        registerBlockException(Identifier.withDefaultNamespace("redstone_ore"), Block.oreRedstone);
        registerBlockException(Identifier.withDefaultNamespace("lit_redstone_ore"), Block.oreRedstoneGlowing);
        registerBlockException(Identifier.withDefaultNamespace("unlit_redstone_torch"), Block.torchRedstoneIdle);
        registerBlockException(Identifier.withDefaultNamespace("redstone_torch"), Block.torchRedstoneActive);
        registerBlockException(Identifier.withDefaultNamespace("snow_layer"), Block.snow);
        registerBlockException(Identifier.withDefaultNamespace("snow"), Block.blockSnow);
        registerBlockException(Identifier.withDefaultNamespace("unpowered_repeater"), Block.redstoneRepeaterIdle);
        registerBlockException(Identifier.withDefaultNamespace("powered_repeater"), Block.redstoneRepeaterActive);
        registerBlockException(Identifier.withDefaultNamespace("brown_mushroom_block"), Block.mushroomCapBrown);
        registerBlockException(Identifier.withDefaultNamespace("red_mushroom_block"), Block.mushroomCapRed);
        registerBlockException(Identifier.withDefaultNamespace("pumpkin_stem"), Block.pumpkinStem);
        registerBlockException(Identifier.withDefaultNamespace("melon_stem"), Block.melonStem);
        registerBlockException(Identifier.withDefaultNamespace("redstone_lamp"), Block.redstoneLampIdle);
        registerBlockException(Identifier.withDefaultNamespace("lit_redstone_lamp"), Block.redstoneLampActive);
        registerBlockException(Identifier.withDefaultNamespace("double_wooden_slab"), Block.woodDoubleSlab);
        registerBlockException(Identifier.withDefaultNamespace("wooden_slab"), Block.woodSingleSlab);
        registerBlockException(Identifier.withDefaultNamespace("stone_button"), Block.stoneButton);
        registerBlockException(Identifier.withDefaultNamespace("wooden_button"), Block.woodenButton);
        // TODO too many works
    }

    private static void registerBlock(Identifier id, Block block) {
        Registry.register(BuiltInRegistries.BLOCK, ResourceKey.create(Registries.BLOCK, id), block);
    }

    private static void registerItem(Identifier id, Item item) {
        Registry.register(BuiltInRegistries.ITEM, ResourceKey.create(Registries.ITEM, id), item);
    }

    private static void registerBlockException(Identifier id, Block block) {
        registerBlock(id, block);
        EXCEPTIONS.add(block.blockID);
    }

    private static void registerItemException(Identifier id, Item item) {
        registerItem(id, item);
        EXCEPTIONS.add(item.itemID);
    }
}
