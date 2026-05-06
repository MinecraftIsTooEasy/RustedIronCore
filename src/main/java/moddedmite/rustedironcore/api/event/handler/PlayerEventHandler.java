package moddedmite.rustedironcore.api.event.handler;

import moddedmite.rustedironcore.api.event.AbstractHandler;
import moddedmite.rustedironcore.api.event.events.PlayerCrossDimensionEvent;
import moddedmite.rustedironcore.api.event.events.PlayerLoggedInEvent;
import moddedmite.rustedironcore.api.event.events.PlayerLoggedOutEvent;
import moddedmite.rustedironcore.api.event.events.PlayerRespawnEvent;
import moddedmite.rustedironcore.api.event.listener.IPlayerEventListener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.SERVER)
public class PlayerEventHandler extends AbstractHandler<IPlayerEventListener> {
    public void onPlayerLoggedIn(PlayerLoggedInEvent event) {
        this.listeners.forEach(x -> x.onPlayerLoggedIn(event));
    }

    public void onPlayerLoggedOut(PlayerLoggedOutEvent event) {
        this.listeners.forEach(x -> x.onPlayerLoggedOut(event));
    }

    public void onPlayerRespawn(PlayerRespawnEvent event) {
        this.listeners.forEach(x -> x.onPlayerRespawn(event));
    }

    public void onPlayerCrossDimension(PlayerCrossDimensionEvent event) {
        this.listeners.forEach(x -> x.onPlayerCrossDimension(event));
    }
}
