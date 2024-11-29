package moddedmite.rustedironcore.api.event.listener;

import moddedmite.rustedironcore.api.event.events.PlayerLoggedInEvent;
import moddedmite.rustedironcore.api.event.events.PlayerLoggedOutEvent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.SERVER)
public interface IPlayerEventListener {
    default void onPlayerLoggedIn(PlayerLoggedInEvent event) {
    }

    default void onPlayerLoggedOut(PlayerLoggedOutEvent event) {

    }
}
