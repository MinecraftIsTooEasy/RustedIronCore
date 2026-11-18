package moddedmite.rustedironcore.api.sound;

import net.minecraft.Packet62LevelSound;
import net.minecraft.ResourceLocation;
import net.minecraft.ServerPlayer;
import net.minecraft.TileEntity;
import net.minecraft.World;

/** A registered sounds.json event. Audio selection belongs to the client's resource packs. */
public final class SoundEvent {

    private final ResourceLocation id;

    SoundEvent(ResourceLocation id) {
        this.id = id;
    }

    public ResourceLocation getId() {
        return id;
    }

    /** Broadcasts through the native world sound and distance checks. Call on the logical server. */
    public void play(World world, double x, double y, double z, float volume, float pitch) {
        if (!world.isRemote) world.playSoundEffect(x, y, z, id.toString(), volume, pitch);
    }

    /** Broadcasts at the center of a TileEntity's block. The TileEntity must belong to a world. */
    public void play(TileEntity tile, float volume, float pitch) {
        play(tile.getWorldObj(), tile.xCoord + 0.5, tile.yCoord + 0.5, tile.zCoord + 0.5, volume, pitch);
    }

    /** Sends a positional sound only to this player. */
    public void play(ServerPlayer player, float volume, float pitch) {
        player.playerNetServerHandler.sendPacketToPlayer(new Packet62LevelSound(id.toString(), player.posX, player.posY, player.posZ, volume, pitch));
    }
}
