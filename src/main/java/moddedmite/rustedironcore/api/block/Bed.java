package moddedmite.rustedironcore.api.block;

import net.minecraft.BiomeGenBase;
import net.minecraft.Block;
import net.minecraft.BlockBed;
import net.minecraft.ChunkCoordinates;
import net.minecraft.EntityPlayer;
import net.minecraft.IBlockAccess;
import net.minecraft.Vec3;
import net.minecraft.World;

/** Bed behavior for blocks that cannot extend {@link BedBlock}. */
public interface Bed {

    default boolean isBed(IBlockAccess world, int x, int y, int z) {
        return true;
    }

    default int getBedDirection(IBlockAccess world, int x, int y, int z) {
        return world.getBlockMetadata(x, y, z) & 3;
    }

    default void setBedOccupied(World world, int x, int y, int z, EntityPlayer player, boolean occupied) {
        BlockBed.setBedOccupied(world, x, y, z, occupied);
    }

    default ChunkCoordinates getBedSpawnPosition(World world, int x, int y, int z, int skipped, Vec3 preferredPosition) {
        return BlockBed.getNearestEmptyChunkCoordinates(world, x, y, z, skipped, preferredPosition);
    }

    /** Coordinates must refer to the sleeping position, normally the head of the bed. */
    default boolean activateBed(World world, int x, int y, int z, EntityPlayer player) {
        if (!player.onGround) return false;
        if (!world.provider.canRespawnHere() || world.getBiomeGenForCoords(x, z) == BiomeGenBase.hell) {
            player.addChatMessage("tile.bed.mobsDigging");
            if (player.isLocalClient()) player.getPlayerController().setUseButtonDelay();
            return false;
        }
        for (Object value : world.playerEntities) {
            EntityPlayer sleeper = (EntityPlayer) value;
            if (sleeper.inBed() && sleeper.bed_location.posX == x && sleeper.bed_location.posY == y && sleeper.bed_location.posZ == z) {
                player.addChatMessage("tile.bed.occupied");
                if (player.isLocalClient()) player.getPlayerController().setUseButtonDelay();
                return false;
            }
        }
        if (!world.isRemote) {
            setBedOccupied(world, x, y, z, player, false);
            player.tryToSleepInBedAt(x, y, z);
        }
        return true;
    }

    static Bed getBed(IBlockAccess world, int x, int y, int z) {
        Block block = Block.blocksList[world.getBlockId(x, y, z)];
        return block instanceof Bed bed && bed.isBed(world, x, y, z) ? bed : null;
    }
}
