package moddedmite.rustedironcore.internal.client.hud;

import moddedmite.rustedironcore.api.chat.ChatComponents;
import moddedmite.rustedironcore.api.chat.TextComponent;
import moddedmite.rustedironcore.api.hud.BossBarColor;
import moddedmite.rustedironcore.api.hud.BossBarOverlay;
import moddedmite.rustedironcore.api.hud.HudChannel;
import moddedmite.rustedironcore.api.hud.HudToasts;
import moddedmite.rustedironcore.client.api.hud.BossOverlay;
import moddedmite.rustedironcore.client.api.hud.ClientBossBar;
import moddedmite.rustedironcore.client.api.hud.HudGraphics;
import moddedmite.rustedironcore.client.api.hud.SystemToast;
import moddedmite.rustedironcore.client.api.hud.TabListOverlay;
import moddedmite.rustedironcore.client.api.hud.TitleOverlay;
import moddedmite.rustedironcore.client.api.hud.ToastManager;
import moddedmite.rustedironcore.internal.client.chat.ChatGuiAccess;
import moddedmite.rustedironcore.internal.hud.HudOperation;
import moddedmite.rustedironcore.internal.hud.HudPacketCodec;
import moddedmite.rustedironcore.network.PacketByteBuf;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.EntityPlayer;
import net.minecraft.Minecraft;
import net.minecraft.ResourceLocation;

import java.util.UUID;

@Environment(EnvType.CLIENT)
public final class ClientHud {

    private static final TitleOverlay TITLES = new TitleOverlay();
    private static final BossOverlay BOSSES = new BossOverlay();
    private static final TabListOverlay TAB_LIST = new TabListOverlay();
    private static final ToastManager TOASTS = new ToastManager();
    private static final HintOverlay HINTS = new HintOverlay();
    private static HudGraphics graphics;
    private static HudChannel lastActionChannel;
    private static String lastActionMessage;
    private static boolean lastActionAnimated;
    private static int lastActionTick;

    static {
        TOASTS.registerType(HudToasts.SYSTEM, SystemToast::new);
    }

    public static HudGraphics getGraphics() {
        if (graphics == null) graphics = new HudGraphics(Minecraft.getMinecraft());
        return graphics;
    }

    public static TitleOverlay getTitles() {
        return TITLES;
    }

    public static BossOverlay getBosses() {
        return BOSSES;
    }

    public static TabListOverlay getTabList() {
        return TAB_LIST;
    }

    public static ToastManager getToasts() {
        return TOASTS;
    }

    public static void receive(HudChannel channel, EntityPlayer player, PacketByteBuf buffer) {
        Minecraft client = Minecraft.getMinecraft();
        if (client.thePlayer != player) return;
        HudOperation operation = HudOperation.values()[buffer.readUnsignedByte()];
        switch (operation) {
            case SYSTEM_MESSAGE -> ((ChatGuiAccess) client.ingameGUI.getChatGUI())
                    .ric$printChatMessage(ChatComponents.create(HudPacketCodec.readText(buffer)));
            case ACTION_BAR -> showActionBar(channel, HudPacketCodec.readText(buffer), buffer.readBoolean(), buffer.readVarInt());
            case HINT -> HINTS.show(channel, HudPacketCodec.readText(buffer), HudPacketCodec.readHintPosition(buffer), buffer.readVarInt());
            case TITLE -> TITLES.setTitle(HudPacketCodec.readText(buffer));
            case SUBTITLE -> TITLES.setSubtitle(HudPacketCodec.readText(buffer));
            case TITLE_TIMES -> TITLES.setTimes(buffer.readInt(), buffer.readInt(), buffer.readInt());
            case CLEAR_TITLES -> {
                TITLES.clear();
                if (buffer.readBoolean()) TITLES.resetTimes();
            }
            case TAB_HEADER -> TAB_LIST.setHeader(HudPacketCodec.readText(buffer));
            case TAB_FOOTER -> TAB_LIST.setFooter(HudPacketCodec.readText(buffer));
            case TOAST -> TOASTS.receive(new ResourceLocation(buffer.readString(), false), buffer);
            case REMOVE_TOAST -> TOASTS.removeToast(new ResourceLocation(buffer.readString(), false));
            default -> updateBoss(operation, buffer);
        }
    }

    private static void showActionBar(HudChannel channel, TextComponent text, boolean animated, int repeatDelayTicks) {
        Minecraft client = Minecraft.getMinecraft();
        String formatted = getGraphics().format(text);
        int tick = client.ingameGUI.getUpdateCounter();
        int elapsed = tick - lastActionTick;
        boolean empty = "".equals(text.tryCollapseToString());
        if (!empty && channel == lastActionChannel && formatted.equals(lastActionMessage)
                && animated == lastActionAnimated && elapsed >= 0 && elapsed < repeatDelayTicks) return;
        // MITE already implements the same 60-tick lifetime, fade and rainbow animation as modern Gui.
        client.ingameGUI.func_110326_a(empty ? "" : formatted, animated);
        lastActionChannel = channel;
        lastActionMessage = formatted;
        lastActionAnimated = animated;
        lastActionTick = tick;
    }

    private static void updateBoss(HudOperation operation, PacketByteBuf buffer) {
        UUID id = HudPacketCodec.readUuid(buffer);
        if (operation == HudOperation.BOSS_REMOVE) {
            BOSSES.remove(id);
            return;
        }
        if (operation == HudOperation.BOSS_ADD) {
            ClientBossBar bar = new ClientBossBar(id, HudPacketCodec.readText(buffer), buffer.readFloat(),
                    BossBarColor.values()[buffer.readUnsignedByte()], BossBarOverlay.values()[buffer.readUnsignedByte()]);
            readProperties(bar, buffer);
            BOSSES.add(bar);
            return;
        }
        ClientBossBar bar = BOSSES.get(id);
        if (bar == null) return;
        switch (operation) {
            case BOSS_PROGRESS -> bar.setProgress(buffer.readFloat());
            case BOSS_NAME -> bar.setName(HudPacketCodec.readText(buffer));
            case BOSS_STYLE -> {
                bar.setColor(BossBarColor.values()[buffer.readUnsignedByte()]);
                bar.setOverlay(BossBarOverlay.values()[buffer.readUnsignedByte()]);
            }
            case BOSS_PROPERTIES -> readProperties(bar, buffer);
            default -> throw new IllegalArgumentException("Unexpected boss operation: " + operation);
        }
    }

    private static void readProperties(ClientBossBar bar, PacketByteBuf buffer) {
        bar.setDarkenScreen(buffer.readBoolean());
        bar.setPlayBossMusic(buffer.readBoolean());
        bar.setCreateWorldFog(buffer.readBoolean());
    }

    public static void tick() {
        TITLES.tick();
        HINTS.tick();
    }

    public static void renderHints(float partialTick) {
        HINTS.render(getGraphics(), partialTick);
    }

    public static void reset() {
        TITLES.clear();
        TITLES.resetTimes();
        BOSSES.clear();
        TAB_LIST.clear();
        TOASTS.clear();
        HINTS.clear();
        lastActionChannel = null;
        lastActionMessage = null;
        lastActionTick = 0;
    }

    public static void renderToasts() {
        Minecraft client = Minecraft.getMinecraft();
        if (TOASTS.isEmpty() || client.gameSettings.gui_mode == 2) return;
        client.entityRenderer.setupOverlayRendering();
        TOASTS.render(getGraphics());
    }
}
