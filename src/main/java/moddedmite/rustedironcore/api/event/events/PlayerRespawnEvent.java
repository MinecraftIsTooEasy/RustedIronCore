package moddedmite.rustedironcore.api.event.events;

import moddedmite.rustedironcore.api.world.Dimension;
import net.minecraft.ServerPlayer;

public record PlayerRespawnEvent(ServerPlayer player, Dimension dimension) {
}
