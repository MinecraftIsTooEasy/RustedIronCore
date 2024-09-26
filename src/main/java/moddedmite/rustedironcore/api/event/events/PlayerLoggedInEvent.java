package moddedmite.rustedironcore.api.event.events;


import net.minecraft.ServerPlayer;

public record PlayerLoggedInEvent(ServerPlayer player, boolean firstLogin) {
}
