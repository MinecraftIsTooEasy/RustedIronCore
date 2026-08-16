package moddedmite.rustedironcore;

import moddedmite.rustedironcore.api.event.Handlers;
import moddedmite.rustedironcore.api.util.FabricUtil;
import moddedmite.rustedironcore.internal.config.RICConfig;
import moddedmite.rustedironcore.internal.event.listeners.CommandRegistry;
import moddedmite.rustedironcore.internal.event.listeners.DimensionRegistry;
import moddedmite.rustedironcore.internal.event.listeners.PlayerEventListener;
import moddedmite.rustedironcore.internal.event.listeners.TickListener;
import moddedmite.rustedironcore.internal.network.Packets;
import moddedmite.rustedironcore.network.Network;
import net.fabricmc.api.ModInitializer;
import net.minecraft.*;
import net.xiaoyu233.fml.ModResourceManager;
import net.xiaoyu233.fml.config.ConfigRegistry;

import java.util.Optional;

public class RustedIronCore implements ModInitializer {
    public static final String MOD_ID = "rusted_iron_core";
    public static final String MOD_NAME = "RustedIronCore";
    public static final String MOD_ID_ABBR = "ric";

    @Override
    public Optional<ConfigRegistry> createConfig() {
        return Optional.of(RICConfig.INSTANCE);
    }

    @Override
    public void onInitialize() {
        if (!FabricUtil.isServer()) {
            this.initClient();
        }
        this.initServer();
        this.registerInternals();
    }

    private void initClient() {
        ModResourceManager.addResourcePackDomain(MOD_ID_ABBR);
        Network.initClient(packet -> Minecraft.getMinecraft().getNetHandler().addToSendQueue(packet.toVanilla()));
        Packets.registerClientReaders();
    }

    private void initServer() {
        Network.initServer((serverPlayer, packet) -> serverPlayer.playerNetServerHandler.sendPacketToPlayer(packet.toVanilla()));
        Packets.registerServerReaders();
    }

    private void registerInternals() {
        Handlers.PlayerEvent.register(new PlayerEventListener());
        Handlers.Tick.register(new TickListener());
        if (RICConfig.UseCustomDimension.get()) {
            Handlers.Dimension.register(new DimensionRegistry());
            Handlers.Command.register(new CommandRegistry());
        }
    }

}
