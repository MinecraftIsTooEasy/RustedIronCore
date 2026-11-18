package moddedmite.rustedironcore.internal.advancement;

import net.minecraft.Block;
import net.minecraft.BlockBed;
import net.minecraft.BlockButton;
import net.minecraft.BlockCrops;
import net.minecraft.BlockDoor;
import net.minecraft.BlockFarmland;
import net.minecraft.BlockFurnace;
import net.minecraft.BlockLever;
import net.minecraft.BlockLog;
import net.minecraft.BlockPressurePlate;
import net.minecraft.BlockRailBase;
import net.minecraft.BlockSapling;
import net.minecraft.BlockSlab;
import net.minecraft.BlockDoubleSlab;
import net.minecraft.BlockStairs;
import net.minecraft.BlockTrapDoor;
import net.minecraft.EnumDirection;
import net.minecraft.World;

import java.util.LinkedHashMap;
import java.util.Map;

/** Native state values; mod-owned resolvers take precedence over these defaults. */
public class AdvancementBlockStates {

    public static Map<String, String> resolve(World world, int x, int y, int z) {
        int metadata = world.getBlockMetadata(x, y, z);
        Map<String, String> values = new LinkedHashMap<>();
        put(values, "metadata", metadata);
        Block block = world.getBlock(x, y, z);
        if (block == null) return values;
        EnumDirection facing = block instanceof BlockDoor door ? door.getDirectionFacing(world, x, y, z) : block.getDirectionFacing(metadata);
        if (facing != null) put(values, "facing", facing.getDescriptor(false));
        if (block instanceof BlockCrops crops) put(values, "age", crops.getGrowthStage(metadata));
        if (block instanceof BlockSapling) put(values, "stage", (metadata >> 3) & 1);
        if (block instanceof BlockLog) put(values, "axis", switch (metadata & 12) { case 4 -> "x"; case 8 -> "z"; default -> "y"; });
        if (block instanceof BlockFurnace furnace) put(values, "lit", block.blockID == furnace.getActiveBlockID());
        if (block instanceof BlockLever || block instanceof BlockButton) put(values, "powered", (metadata & 8) != 0);
        if (block instanceof BlockPressurePlate) put(values, "powered", metadata != 0);
        if (block instanceof BlockFarmland) put(values, "moisture", BlockFarmland.getWetness(metadata));
        if (block instanceof BlockStairs) put(values, "half", (metadata & 4) == 0 ? "bottom" : "top");
        if (block instanceof BlockSlab) put(values, "type", (metadata & 8) == 0 ? "bottom" : "top");
        if (block instanceof BlockDoubleSlab) put(values, "type", "double");
        if (block instanceof BlockTrapDoor) {
            put(values, "open", BlockTrapDoor.isTrapdoorOpen(metadata));
            put(values, "half", (metadata & 8) == 0 ? "bottom" : "top");
        }
        if (block instanceof BlockDoor) {
            int lower = BlockDoor.isTopHalf(metadata) ? world.getBlockMetadata(x, y - 1, z) : metadata;
            put(values, "half", BlockDoor.isTopHalf(metadata) ? "upper" : "lower");
            put(values, "open", BlockDoor.isOpen(lower));
        }
        if (block instanceof BlockBed) {
            put(values, "part", BlockBed.isBlockHeadOfBed(metadata) ? "head" : "foot");
            put(values, "occupied", BlockBed.isBedOccupied(metadata));
        }
        if (block instanceof BlockRailBase rail) {
            int shape = rail.isPowered() ? metadata & 7 : metadata;
            String[] shapes = {"north_south", "east_west", "ascending_east", "ascending_west", "ascending_north", "ascending_south", "south_east", "south_west", "north_west", "north_east"};
            if (shape < shapes.length) put(values, "shape", shapes[shape]);
            if (rail.isPowered()) put(values, "powered", (metadata & 8) != 0);
        }
        if (block == Block.waterStill || block == Block.waterMoving || block == Block.lavaStill || block == Block.lavaMoving) put(values, "level", metadata);
        if (block == Block.redstoneWire) put(values, "power", metadata);
        if (block == Block.redstoneLampIdle || block == Block.redstoneLampActive) put(values, "lit", block == Block.redstoneLampActive);
        if (block == Block.oreRedstone || block == Block.oreRedstoneGlowing) put(values, "lit", block == Block.oreRedstoneGlowing);
        if (block == Block.torchRedstoneIdle || block == Block.torchRedstoneActive) put(values, "lit", block == Block.torchRedstoneActive);
        if (block == Block.reed || block == Block.cactus || block == Block.netherStalk) put(values, "age", metadata);
        if (block == Block.cake) put(values, "bites", metadata);
        return values;
    }

    private static void put(Map<String, String> values, String name, Object value) {
        values.put(name, value.toString());
    }
}
