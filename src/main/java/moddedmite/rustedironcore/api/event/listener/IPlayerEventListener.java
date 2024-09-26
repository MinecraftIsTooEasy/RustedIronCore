package moddedmite.rustedironcore.api.event.listener;

import moddedmite.rustedironcore.api.event.events.PlayerLoggedInEvent;
import moddedmite.rustedironcore.api.event.events.PlayerLoggedOutEvent;

public interface IPlayerEventListener {
    default void onPlayerLoggedIn(PlayerLoggedInEvent event) {
    }

    default void onPlayerLoggedOut(PlayerLoggedOutEvent event) {

    }
}
