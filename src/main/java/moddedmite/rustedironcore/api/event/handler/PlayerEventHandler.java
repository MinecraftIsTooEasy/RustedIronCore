package moddedmite.rustedironcore.api.event.handler;

import moddedmite.rustedironcore.api.event.AbstractHandler;
import moddedmite.rustedironcore.api.event.events.PlayerLoggedInEvent;
import moddedmite.rustedironcore.api.event.events.PlayerLoggedOutEvent;
import moddedmite.rustedironcore.api.event.listener.IPlayerEventListener;

public class PlayerEventHandler extends AbstractHandler<IPlayerEventListener> {
    public void onPlayerLoggedIn(PlayerLoggedInEvent event) {
        this.listeners.forEach(x -> x.onPlayerLoggedIn(event));
    }

    public void onPlayerLoggedOut(PlayerLoggedOutEvent event) {
        this.listeners.forEach(x -> x.onPlayerLoggedOut(event));
    }
}
