package moddedmite.rustedironcore.api.event.listener;

import moddedmite.rustedironcore.api.event.events.PlayerLoggedInEvent;
import moddedmite.rustedironcore.api.event.events.PlayerLoggedOutEvent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.SERVER)
public interface IPlayerEventListener {
    // at this point, the player is completely in the server.
    default void onPlayerLoggedIn(PlayerLoggedInEvent event) {
    }

    // at this point, almost all player lists have already removed this player.
    default void onPlayerLoggedOut(PlayerLoggedOutEvent event) {
    }
}
