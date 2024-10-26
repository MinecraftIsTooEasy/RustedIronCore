package moddedmite.rustedironcore;

import moddedmite.rustedironcore.api.event.Handlers;
import moddedmite.rustedironcore.api.event.events.PlayerLoggedInEvent;
import moddedmite.rustedironcore.api.event.listener.IPlayerEventListener;
import moddedmite.rustedironcore.api.event.listener.ITickListener;
import moddedmite.rustedironcore.api.player.ServerPlayerAPI;
import moddedmite.rustedironcore.api.util.GuiUtil;
import moddedmite.rustedironcore.api.util.StringUtil;
import moddedmite.rustedironcore.network.Network;
import moddedmite.rustedironcore.network.packets.S2COpenGuiTips;
import moddedmite.rustedironcore.network.packets.S2CSyncNutritionLimit;
import net.fabricmc.api.ModInitializer;
import net.minecraft.Minecraft;
import net.minecraft.ServerPlayer;
import net.xiaoyu233.fml.FishModLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RustedIronCore implements ModInitializer {
    public static final String MOD_ID = "RustedIronCore";

    public static final Logger logger = LogManager.getLogger(MOD_ID);

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
    }

    private void initServer() {
        Network.initServer((serverPlayer, itfPacket) -> serverPlayer.playerNetServerHandler.sendPacketToPlayer(itfPacket.toVanilla()));
    }

    private void registerVanillaEvents() {
        Handlers.PlayerEvent.register(new IPlayerEventListener() {
            @Override
            public void onPlayerLoggedIn(PlayerLoggedInEvent event) {
                ServerPlayer player = event.player();
                Network.sendToClient(player, new S2CSyncNutritionLimit(((ServerPlayerAPI) player).getNutritionLimit()));
                if (StringUtil.getCurrentLanguage().equals("en_US")) return;
                player.addChatMessage("ric.statement");
                if (event.firstLogin()) {
                    Network.sendToClient(player, new S2COpenGuiTips());
                }
            }
        });
        Handlers.Tick.register(new ITickListener() {
            @Override
            public void onClientTick(Minecraft client) {
                if (StringUtil.getCurrentLanguage().equals("en_US")) return;
                changeTitleCounter++;
                if (changeTitleCounter >= changeTitlePeriod) {
                    if (isNowRICTitle) {
                        if (titleCache == null) return;
                        GuiUtil.setWindowTitle(titleCache);
                        isNowRICTitle = false;
                    } else {
                        titleCache = GuiUtil.getWindowTitle();
                        String translate = StringUtil.translate("ric.gui.title");
                        if (translate.equals("ric.gui.title")) return;
                        GuiUtil.setWindowTitle(translate);
                        isNowRICTitle = true;
                    }
                    changeTitleCounter = 0;
                }
//                Handlers.TimedTask.onTick();
            }
        });
    }

    public static int tipsGuiCounter = 0;
    public static boolean displayedTipsGui = false;
    public static int changeTitleCounter = 0;
    public static int changeTitlePeriod = 100;
    public static boolean isNowRICTitle = false;
    public static String titleCache;
}
