package moddedmite.rustedironcore.internal.advancement;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import moddedmite.rustedironcore.api.advancement.Advancements;
import moddedmite.rustedironcore.api.advancement.LocationCondition;
import net.minecraft.Block;
import net.minecraft.BlockFurnace;
import net.minecraft.ItemBlock;
import net.minecraft.ItemStack;
import net.minecraft.Material;
import net.minecraft.NBTTagCompound;
import net.minecraft.TileEntity;
import net.minecraft.World;
import net.minecraft.WorldServer;
import net.minecraft.IChunkProvider;
import net.minecraft.ChunkProviderHell;

import java.util.HashMap;
import java.util.Map;

public class AdvancementLocationPredicates {

    private static final Map<String, LocationCondition> STRUCTURES = new HashMap<>();

    public static void registerStructure(String id, LocationCondition predicate) {
        STRUCTURES.put(id, predicate);
    }

    public static void validate(JsonObject json) {
        AdvancementPredicates.keys(json, "dimension", "position", "block", "fluid", "biome", "biomes", "structure", "structures", "light", "can_see_sky");
        if (json.has("dimension")) AdvancementJson.id(json.get("dimension").getAsString());
        for (String field : new String[]{"biome", "biomes", "structure", "structures"}) AdvancementPredicates.validateIds(json.get(field));
        if (json.has("position")) {
            JsonObject position = json.getAsJsonObject("position");
            AdvancementPredicates.keys(position, "x", "y", "z");
            position.entrySet().forEach(entry -> AdvancementPredicates.validateRange(entry.getValue()));
        }
        if (json.has("block")) validateBlock(json.getAsJsonObject("block"));
        if (json.has("fluid")) {
            JsonObject fluid = json.getAsJsonObject("fluid");
            AdvancementPredicates.keys(fluid, "fluid", "fluids", "tag", "state");
            AdvancementPredicates.validateIds(fluid.get("fluid"));
            AdvancementPredicates.validateIds(fluid.get("fluids"));
            if (fluid.has("tag")) AdvancementJson.id(fluid.get("tag").getAsString());
            if (fluid.has("state")) AdvancementPredicates.validateState(fluid.getAsJsonObject("state"));
        }
        if (json.has("light")) {
            AdvancementPredicates.keys(json.getAsJsonObject("light"), "light");
            AdvancementPredicates.validateRange(json.getAsJsonObject("light").get("light"));
        }
        if (json.has("can_see_sky") && (!json.get("can_see_sky").isJsonPrimitive() || !json.get("can_see_sky").getAsJsonPrimitive().isBoolean()))
            throw new IllegalArgumentException("can_see_sky must be boolean");
    }

    public static void validateBlock(JsonObject json) {
        AdvancementPredicates.keys(json, "blocks", "block", "tag", "data", "state", "nbt");
        AdvancementPredicates.validateIds(json.get("blocks"));
        AdvancementPredicates.validateIds(json.get("block"));
        if (json.has("tag")) AdvancementJson.id(json.get("tag").getAsString());
        AdvancementPredicates.validateRange(json.get("data"));
        if (json.has("state")) AdvancementPredicates.validateState(json.getAsJsonObject("state"));
        if (json.has("nbt")) AdvancementItemPredicates.pattern(json.get("nbt").getAsString());
    }

    public static boolean matches(JsonObject json, World world, double x, double y, double z) {
        if (json.has("dimension") && !AdvancementPredicates.dimension(json.get("dimension").getAsString(), world.provider.dimensionId)) return false;
        if (json.has("position")) {
            JsonObject position = json.getAsJsonObject("position");
            if (!AdvancementPredicates.range(position.get("x"), x) || !AdvancementPredicates.range(position.get("y"), y) || !AdvancementPredicates.range(position.get("z"), z)) return false;
        }
        int blockX = (int) Math.floor(x), blockY = (int) Math.floor(y), blockZ = (int) Math.floor(z);
        if ((json.has("block") || json.has("fluid") || json.has("light") || json.has("can_see_sky") || json.has("biome") || json.has("biomes") || json.has("structure") || json.has("structures"))
                && !world.blockExists(blockX, blockY, blockZ)) return false;
        for (String field : new String[]{"biome", "biomes"}) if (json.has(field)
                && !AdvancementPredicates.anyId(json.get(field), id -> AdvancementAdapters.biome(id, world.getBiomeGenForCoords(blockX, blockZ)))) return false;
        for (String field : new String[]{"structure", "structures"}) if (json.has(field)
                && !AdvancementPredicates.anyId(json.get(field), id -> structure(id, world, blockX, blockY, blockZ))) return false;
        if (json.has("light") && !AdvancementPredicates.range(json.getAsJsonObject("light").get("light"), world.getBlockLightValue(blockX, blockY, blockZ))) return false;
        if (json.has("can_see_sky") && json.get("can_see_sky").getAsBoolean() != world.canBlockSeeTheSky(blockX, blockY, blockZ)) return false;
        if (json.has("block") && !block(json.getAsJsonObject("block"), world, blockX, blockY, blockZ)) return false;
        return !json.has("fluid") || fluid(json.getAsJsonObject("fluid"), world, blockX, blockY, blockZ);
    }

    private static boolean structure(String id, World world, int x, int y, int z) {
        if (id.startsWith("#")) return AdvancementTags.matches("worldgen/structure", id.substring(1), member -> structure(member, world, x, y, z));
        id = AdvancementJson.id(id).toString();
        LocationCondition condition = STRUCTURES.get(id);
        if (condition != null) return condition.matches(world, x, y, z);
        if (!(world instanceof WorldServer server)) return false;
        IChunkProvider provider = server.theChunkProviderServer.getChunkProvider();
        if (provider instanceof AdvancementStructures structures) return structures.ric$containsStructure(id, x, y, z);
        return provider instanceof ChunkProviderHell nether && id.equals("minecraft:fortress") && nether.genNetherBridge.hasStructureAt(x, y, z);
    }

    public static boolean block(JsonObject json, World world, int x, int y, int z) {
        if (json.has("block") && !AdvancementPredicates.anyId(json.get("block"), id -> blockId(id, world, x, y, z))) return false;
        if (json.has("blocks") && !AdvancementPredicates.anyId(json.get("blocks"), id -> blockId(id, world, x, y, z))) return false;
        if (json.has("tag") && !Advancements.matchesBlockTag(AdvancementJson.id(json.get("tag").getAsString()).toString(), world, x, y, z)) return false;
        if (!AdvancementPredicates.range(json.get("data"), world.getBlockMetadata(x, y, z))) return false;
        if (json.has("state") && !state(json.getAsJsonObject("state"), Advancements.blockState(world, x, y, z))) return false;
        if (json.has("nbt")) {
            TileEntity tile = world.getBlockTileEntity(x, y, z);
            if (tile == null) return false;
            NBTTagCompound data = new NBTTagCompound();
            tile.writeToNBT(data);
            if (!AdvancementItemPredicates.pattern(json.get("nbt").getAsString()).test(data)) return false;
        }
        return true;
    }

    public static boolean state(JsonObject json, Map<String, String> actual) {
        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
            String value = actual.get(entry.getKey());
            if (value == null) return false;
            if (entry.getValue().isJsonPrimitive()) {
                if (!entry.getValue().getAsString().equals(value)) return false;
            } else try {
                if (!AdvancementPredicates.range(entry.getValue(), Double.parseDouble(value))) return false;
            } catch (NumberFormatException ignored) {
                return false;
            }
        }
        return true;
    }

    public static boolean blockId(String id, World world, int x, int y, int z) {
        if (id.startsWith("#")) return Advancements.matchesBlockTag(AdvancementJson.id(id.substring(1)).toString(), world, x, y, z);
        id = AdvancementJson.id(id).toString();
        if (id.equals("minecraft:air")) return world.isAirBlock(x, y, z);
        Block actualBlock = world.getBlock(x, y, z);
        Block nativeBlock = switch (id) {
            case "minecraft:wheat" -> Block.crops;
            case "minecraft:carrots" -> Block.carrot;
            case "minecraft:potatoes" -> Block.potato;
            case "minecraft:sugar_cane" -> Block.reed;
            case "minecraft:nether_wart" -> Block.netherStalk;
            case "minecraft:melon_stem" -> Block.melonStem;
            case "minecraft:pumpkin_stem" -> Block.pumpkinStem;
            case "minecraft:cocoa" -> Block.cocoaPlant;
            case "minecraft:oak_door" -> Block.doorWood;
            case "minecraft:iron_door" -> Block.doorIron;
            case "minecraft:flower_pot" -> Block.flowerPot;
            case "minecraft:cake" -> Block.cake;
            case "minecraft:brewing_stand" -> Block.brewingStand;
            default -> null;
        };
        if (nativeBlock != null) return actualBlock == nativeBlock;
        if (id.equals("minecraft:water")) return actualBlock == Block.waterStill || actualBlock == Block.waterMoving;
        if (id.equals("minecraft:lava")) return actualBlock == Block.lavaStill || actualBlock == Block.lavaMoving;
        if (id.equals("minecraft:redstone_ore")) return actualBlock == Block.oreRedstone || actualBlock == Block.oreRedstoneGlowing;
        if (id.equals("minecraft:redstone_lamp")) return actualBlock == Block.redstoneLampIdle || actualBlock == Block.redstoneLampActive;
        if (id.equals("minecraft:redstone_torch")) return actualBlock == Block.torchRedstoneIdle || actualBlock == Block.torchRedstoneActive;
        if (id.equals("minecraft:red_bed")) return actualBlock == Block.bed;
        ItemStack expected = Advancements.resolveItem(id);
        if (expected == null || !(expected.getItem() instanceof ItemBlock item) || actualBlock == null) return false;
        if (item.getBlock() != actualBlock && !(actualBlock instanceof BlockFurnace furnace && furnace.getIdleBlockID() == item.getBlock().blockID)) return false;
        ItemStack actual = actualBlock.createStackedBlock(world.getBlockMetadata(x, y, z));
        return !id.startsWith("minecraft:") || item.getNumSubtypes() <= 1
                || actual != null && expected.getItemSubtype() == actual.getItemSubtype();
    }

    private static boolean fluid(JsonObject json, World world, int x, int y, int z) {
        Material material = world.getBlockMaterial(x, y, z);
        String actual = material == Material.water ? "minecraft:water" : material == Material.lava ? "minecraft:lava" : "minecraft:empty";
        for (String field : new String[]{"fluid", "fluids"}) if (json.has(field) && !AdvancementPredicates.anyId(json.get(field), id -> fluidId(id, actual))) return false;
        if (json.has("tag") && !fluidId("#" + json.get("tag").getAsString(), actual)) return false;
        return !json.has("state") || state(json.getAsJsonObject("state"), Map.of("level", Integer.toString(world.getBlockMetadata(x, y, z))));
    }

    private static boolean fluidId(String id, String actual) {
        if (id.startsWith("#")) return AdvancementTags.matches("fluid", AdvancementJson.id(id.substring(1)).toString(), value -> fluidId(value, actual));
        return AdvancementJson.id(id).toString().equals(actual);
    }
}
