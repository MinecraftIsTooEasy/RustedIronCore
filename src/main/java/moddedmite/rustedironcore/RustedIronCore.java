package moddedmite.rustedironcore;

import moddedmite.rustedironcore.api.event.Handlers;
import moddedmite.rustedironcore.internal.config.RICConfig;
import moddedmite.rustedironcore.internal.event.listeners.PlayerEventListener;
import moddedmite.rustedironcore.internal.event.listeners.TickListener;
import moddedmite.rustedironcore.internal.network.Packets;
import moddedmite.rustedironcore.network.Network;
import net.fabricmc.api.ModInitializer;
import net.minecraft.Minecraft;
import net.xiaoyu233.fml.FishModLoader;
import net.xiaoyu233.fml.config.ConfigRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

public class RustedIronCore implements ModInitializer {
    public static final String MOD_ID = "RustedIronCore";

    public static final Logger logger = LogManager.getLogger(MOD_ID);

    @Override
    public Optional<ConfigRegistry> createConfig() {
        return Optional.of(new ConfigRegistry(RICConfig.ROOT, RICConfig.CONFIG_FILE));
    }

    @Override
    public void onInitialize() {
        if (!FishModLoader.isServer()) {
            this.initClient();
        }
        this.initServer();
        this.registerVanillaEvents();
    }

    private void initClient() {
        Network.initClient(packet -> Minecraft.getMinecraft().getNetHandler().addToSendQueue(packet.toVanilla()));
        Packets.registerClientReaders();
    }

    private void initServer() {
        Network.initServer((serverPlayer, packet) -> serverPlayer.playerNetServerHandler.sendPacketToPlayer(packet.toVanilla()));
        Packets.registerServerReaders();
    }

    private void registerVanillaEvents() {
        Handlers.PlayerEvent.register(new PlayerEventListener());
        Handlers.Tick.register(new TickListener());
    }
}
