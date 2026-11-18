package moddedmite.rustedironcore.api.item;

import huix.glacier.api.registry.sync.RegistryHelper;
import huix.glacier.util.Identifier;
import net.minecraft.Block;
import net.minecraft.Item;
import net.minecraft.ItemStack;

import java.util.Map;
import java.util.function.BiFunction;

/** Converts vanilla resource names to the corresponding MITE items and subtypes. */
public class VanillaItems {

    private static final String[] WOODS = {"oak", "spruce", "birch", "jungle", "acacia", "dark_oak"};
    private static final String[] COLORS = {"white", "orange", "magenta", "light_blue", "yellow", "lime", "pink", "gray",
            "light_gray", "cyan", "purple", "blue", "brown", "green", "red", "black"};
    private static final String[] STONES = {"granite", "polished_granite", "diorite", "polished_diorite", "andesite", "polished_andesite"};
    private static final Map<String, String> ALIASES = Map.ofEntries(
            Map.entry("oak_fence", "fence"), Map.entry("oak_fence_gate", "fence_gate"),
            Map.entry("terracotta", "hardened_clay"), Map.entry("stone_bricks", "stonebrick"),
            Map.entry("bricks", "brick_block"), Map.entry("note_block", "noteblock"),
            Map.entry("cod", "fish"), Map.entry("cooked_cod", "cooked_fish"));

    /** Resolves a modern vanilla ID. Materials absent from MITE return null. */
    public static ItemStack resolve(String identifier) {
        return resolve(identifier, VanillaItems::resolveLegacy);
    }

    /**
     * Converts flattened vanilla names using a caller-owned legacy item resolver.
     * The resolver receives a qualified ID and its 1.12 subtype, allowing optional
     * material bindings without registering global substitutions.
     */
    public static ItemStack resolve(String identifier, BiFunction<String, Integer, ItemStack> resolver) {
        String id = identifier.contains(":") ? identifier : "minecraft:" + identifier;
        if (!id.startsWith("minecraft:")) return resolver.apply(id, 0);
        String name = id.substring(10);
        for (int i = 0; i < WOODS.length; i++) {
            if (name.equals(WOODS[i] + "_planks")) return resolver.apply("minecraft:planks", i);
            if (name.equals(WOODS[i] + "_log")) return resolver.apply(i < 4 ? "minecraft:log" : "minecraft:log2", i < 4 ? i : i - 4);
            if (name.equals(WOODS[i] + "_leaves")) return resolver.apply(i < 4 ? "minecraft:leaves" : "minecraft:leaves2", i < 4 ? i : i - 4);
        }
        for (int i = 0; i < COLORS.length; i++) {
            String color = COLORS[i] + "_";
            if (!name.startsWith(color)) continue;
            String suffix = name.substring(color.length());
            String legacy = switch (suffix) {
                case "wool", "carpet", "concrete", "stained_glass", "stained_glass_pane", "bed", "dye" -> suffix;
                case "terracotta" -> "stained_hardened_clay";
                default -> null;
            };
            if (legacy != null) return resolver.apply("minecraft:" + legacy, suffix.equals("dye") ? 15 - i : i);
        }
        if (name.equals("charcoal")) return resolver.apply("minecraft:coal", 1);
        for (int i = 0; i < STONES.length; i++) {
            if (name.equals(STONES[i])) return resolver.apply("minecraft:stone", i + 1);
        }
        return resolver.apply("minecraft:" + ALIASES.getOrDefault(name, name), 0);
    }

    /**
     * Resolves vanilla names with 1.12 metadata; for example, a red bed is bed:14.
     * Wildcard subtype 32767 is retained for legacy ingredient matching.
     */
    public static ItemStack resolveLegacy(String identifier, int metadata) {
        String id = identifier.contains(":") ? identifier : "minecraft:" + identifier;
        if (!id.startsWith("minecraft:") || metadata < 0) return null;
        String path = id.substring(10);
        if (path.equals("bed")) {
            if (metadata != 14) return null;
            metadata = 0;
        }
        if (metadata == 1 && (path.equals("fish") || path.equals("cooked_fish"))) {
            path = path.equals("fish") ? "salmon" : "cooked_salmon";
            metadata = 0;
        }
        int max = switch (path) {
            case "planks", "log", "leaves", "wooden_slab" -> 3;
            case "stone", "fish", "cooked_fish" -> 0;
            default -> Integer.MAX_VALUE;
        };
        if (metadata != 32767 && metadata > max) return null;
        Item item = switch (path) {
            case "stone" -> Item.itemsList[Block.stone.blockID];
            case "cobblestone" -> Item.itemsList[Block.cobblestone.blockID];
            case "planks" -> Item.itemsList[Block.planks.blockID];
            case "log" -> Item.itemsList[Block.wood.blockID];
            case "leaves" -> Item.itemsList[Block.leaves.blockID];
            case "glass" -> Item.itemsList[Block.glass.blockID];
            case "wool" -> Item.itemsList[Block.cloth.blockID];
            case "carpet" -> Item.itemsList[Block.carpet.blockID];
            case "brick_block" -> Item.itemsList[Block.brick.blockID];
            case "chest" -> Item.itemsList[Block.chest.blockID];
            case "furnace" -> Item.itemsList[Block.furnaceIdle.blockID];
            case "lever" -> Item.itemsList[Block.lever.blockID];
            case "stone_button" -> Item.itemsList[Block.stoneButton.blockID];
            case "fence" -> Item.itemsList[Block.fence.blockID];
            case "fence_gate" -> Item.itemsList[Block.fenceGate.blockID];
            case "glowstone" -> Item.itemsList[Block.glowStone.blockID];
            case "jukebox" -> Item.itemsList[Block.jukebox.blockID];
            case "noteblock" -> Item.itemsList[Block.music.blockID];
            case "piston" -> Item.itemsList[Block.pistonBase.blockID];
            case "iron_bars" -> Item.itemsList[Block.fenceIron.blockID];
            case "glass_pane" -> Item.itemsList[Block.thinGlass.blockID];
            case "iron_block" -> Item.itemsList[Block.blockIron.blockID];
            case "heavy_weighted_pressure_plate" -> Item.itemsList[Block.pressurePlateIron.blockID];
            case "daylight_detector" -> Item.itemsList[Block.daylightSensor.blockID];
            case "redstone_block" -> Item.itemsList[Block.blockRedstone.blockID];
            case "quartz_block" -> Item.itemsList[Block.blockNetherQuartz.blockID];
            case "stained_hardened_clay" -> Item.itemsList[Block.stainedClay.blockID];
            case "hardened_clay" -> Item.itemsList[Block.hardenedClay.blockID];
            case "wooden_slab" -> Item.itemsList[Block.woodSingleSlab.blockID];
            case "nether_brick_fence" -> Item.itemsList[Block.netherFence.blockID];
            case "redstone_torch" -> Item.itemsList[Block.torchRedstoneActive.blockID];
            case "flower_pot" -> Item.flowerPot;
            case "bed" -> Item.bed;
            case "obsidian" -> Item.itemsList[Block.obsidian.blockID];
            case "ice" -> Item.itemsList[Block.ice.blockID];
            case "stonebrick" -> Item.itemsList[Block.stoneBrick.blockID];
            case "iron_ingot" -> Item.ingotIron;
            case "gold_ingot" -> Item.ingotGold;
            case "clay_ball" -> Item.clay;
            case "paper" -> Item.paper;
            case "stick" -> Item.stick;
            case "string" -> Item.silk;
            case "redstone" -> Item.redstone;
            case "dye" -> Item.dyePowder;
            case "clock" -> Item.pocketSundial;
            case "item_frame" -> Item.itemFrame;
            case "book" -> Item.book;
            case "water_bucket" -> Item.bucketIronWater;
            case "lava_bucket" -> Item.bucketIronLava;
            case "bucket" -> Item.bucketIronEmpty;
            case "nether_brick" -> Item.netherrackBrick;
            case "beef" -> Item.beefRaw;
            case "cooked_beef" -> Item.beefCooked;
            case "porkchop" -> Item.porkRaw;
            case "cooked_porkchop" -> Item.porkCooked;
            case "chicken" -> Item.chickenRaw;
            case "cooked_chicken" -> Item.chickenCooked;
            case "mutton" -> Item.lambchopRaw;
            case "cooked_mutton" -> Item.lambchopCooked;
            case "fish" -> Item.fishRaw;
            case "cooked_fish" -> Item.fishCooked;
            case "salmon" -> Item.fishLargeRaw;
            case "cooked_salmon" -> Item.fishLargeCooked;
            case "potato" -> Item.potato;
            case "baked_potato" -> Item.bakedPotato;
            case "poisonous_potato" -> Item.poisonousPotato;
            case "rotten_flesh" -> Item.rottenFlesh;
            case "slime_ball" -> Item.slimeBall;
            case "snowball" -> Item.snowball;
            case "bread" -> Item.bread;
            case "cookie" -> Item.cookie;
            case "coal" -> Item.coal;
            case "apple" -> Item.appleRed;
            case "melon" -> Item.melon;
            case "carrot" -> Item.carrot;
            case "pumpkin_pie" -> Item.pumpkinPie;
            case "egg" -> Item.egg;
            case "milk_bucket" -> Item.bucketIronMilk;
            case "sugar" -> Item.sugar;
            case "fire_charge" -> Item.fireballCharge;
            case "enchanted_book" -> Item.enchantedBook;
            case "written_book" -> Item.writtenBook;
            case "emerald" -> Item.emerald;
            case "diamond" -> Item.diamond;
            case "iron_axe" -> Item.axeIron;
            case "golden_axe" -> Item.axeGold;
            case "bow" -> Item.bow;
            case "iron_pickaxe" -> Item.pickaxeIron;
            case "iron_shovel" -> Item.shovelIron;
            case "iron_hoe" -> Item.hoeIron;
            case "iron_sword" -> Item.swordIron;
            case "golden_pickaxe" -> Item.pickaxeGold;
            case "golden_shovel" -> Item.shovelGold;
            case "golden_hoe" -> Item.hoeGold;
            case "golden_sword" -> Item.swordGold;
            case "wooden_shovel" -> Item.shovelWood;
            case "fishing_rod" -> Item.fishingRodIron;
            case "flint_and_steel" -> Item.flintAndSteel;
            case "shears" -> Item.shears;
            case "leather_helmet" -> Item.helmetLeather;
            case "leather_chestplate" -> Item.plateLeather;
            case "leather_leggings" -> Item.legsLeather;
            case "leather_boots" -> Item.bootsLeather;
            case "chainmail_helmet" -> Item.helmetChainIron;
            case "chainmail_chestplate" -> Item.plateChainIron;
            case "chainmail_leggings" -> Item.legsChainIron;
            case "chainmail_boots" -> Item.bootsChainIron;
            case "iron_helmet" -> Item.helmetIron;
            case "iron_chestplate" -> Item.plateIron;
            case "iron_leggings" -> Item.legsIron;
            case "iron_boots" -> Item.bootsIron;
            case "golden_helmet" -> Item.helmetGold;
            case "golden_chestplate" -> Item.plateGold;
            case "golden_leggings" -> Item.legsGold;
            case "golden_boots" -> Item.bootsGold;
            case "wheat" -> Item.wheat;
            default -> RegistryHelper.getItem(new Identifier(id));
        };
        if (item == null) return null;
        if (metadata != 32767 && !item.isDamageable() && metadata >= Math.max(1, item.getNumSubtypes())) return null;
        return new ItemStack(item, 1, metadata);
    }
}
