package moddedmite.rustedironcore;

import moddedmite.rustedironcore.api.event.Handlers;
import moddedmite.rustedironcore.api.util.FabricUtil;
import moddedmite.rustedironcore.api.util.StringUtil;
import moddedmite.rustedironcore.internal.config.RICConfig;
import moddedmite.rustedironcore.internal.event.listeners.*;
import moddedmite.rustedironcore.internal.network.Packets;
import moddedmite.rustedironcore.internal.network.packets.S2COpenGuiTips;
import moddedmite.rustedironcore.network.Network;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.minecraft.Minecraft;
import net.xiaoyu233.fml.config.ConfigRegistry;

import java.util.Optional;

public class RustedIronCore implements ModInitializer {
    public static final String MOD_NAME = "RustedIronCore";

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
        Handlers.BiomeGenerate.register(new BiomeListener());
        if (RICConfig.UseCustomDimension.get()) {
            Handlers.Dimension.register(new DimensionRegistry());
            Handlers.Command.register(new CommandRegistry());
        }
    }

    @Environment(EnvType.CLIENT)
    public static boolean shouldRenderStatement() {
        if (S2COpenGuiTips.firstLoginStatementCounter == 0) return false;// the time is over
        if (!StringUtil.getCurrentLanguage().equals("zh_CN")) return false;// Non-Chinese skip
        if (FabricUtil.isDevelopmentEnvironment()) return false;// dev env disable
        if (RICConfig.StatementOnLogin.get()) return true;// config is true then render
        return false;
    }
}
