package moddedmite.rustedironcore.api.event.events;

import moddedmite.rustedironcore.api.world.Dimension;
import net.minecraft.ServerPlayer;

public record PlayerCrossDimensionEvent(ServerPlayer player, Dimension from, Dimension to) {
}
