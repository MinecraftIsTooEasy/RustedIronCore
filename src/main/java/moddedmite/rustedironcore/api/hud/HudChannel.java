package moddedmite.rustedironcore.api.hud;

import moddedmite.rustedironcore.api.chat.ChatComponents;
import moddedmite.rustedironcore.api.chat.TextComponent;
import moddedmite.rustedironcore.internal.hud.HudOperation;
import moddedmite.rustedironcore.internal.hud.HudPacketCodec;
import moddedmite.rustedironcore.internal.network.packets.S2CHudPacket;
import moddedmite.rustedironcore.network.Network;
import moddedmite.rustedironcore.network.PacketByteBuf;
import moddedmite.rustedironcore.network.PacketReader;
import net.minecraft.EntityPlayer;
import net.minecraft.ResourceLocation;
import net.minecraft.ServerPlayer;

import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Register one short channel per mod in common initialization. Text remains structured
 * until the receiving client renders it, including translations, colors and keybinds.
 * Invoke from one side of an interaction: server calls send to that player, client calls display locally.
 * Vanilla display slots are shared by all channels; positioned hints share slots by HintPosition.
 */
public final class HudChannel {

    private final ResourceLocation id;

    private HudChannel(ResourceLocation id) {
        this.id = Objects.requireNonNull(id, "id");
        if (id.toString().length() > 20) {
            throw new IllegalArgumentException("HUD channel exceeds the custom-payload limit of 20 characters: " + id);
        }
    }

    public static HudChannel register(ResourceLocation id) {
        HudChannel channel = new HudChannel(id);
        if (PacketReader.clientReaders.containsKey(id.toString())) {
            throw new IllegalArgumentException("Packet channel is already registered: " + id);
        }
        PacketReader.registerClientPacketReader(id, buffer -> new S2CHudPacket(channel, buffer));
        return channel;
    }

    public ResourceLocation getId() {
        return id;
    }

    /** Equivalent to vanilla displayClientMessage(text, overlay). */
    public void displayMessage(EntityPlayer player, TextComponent text, boolean overlay) {
        if (overlay) showActionBar(player, text);
        else showSystemMessage(player, text);
    }

    public void showSystemMessage(EntityPlayer player, TextComponent text) {
        send(player, HudOperation.SYSTEM_MESSAGE, buffer -> HudPacketCodec.writeText(buffer, text, player));
    }

    public void showActionBar(EntityPlayer player, TextComponent text) {
        showActionBar(player, text, false, 0);
    }

    public void showActionBar(EntityPlayer player, TextComponent text, boolean animateColor) {
        showActionBar(player, text, animateColor, 0);
    }

    /** Vanilla's 60-tick overlay, with optional animated color and optional repeat suppression. */
    public void showActionBar(EntityPlayer player, TextComponent text, boolean animateColor, int repeatDelayTicks) {
        if (repeatDelayTicks < 0) throw new IllegalArgumentException("Repeat delay must not be negative");
        send(player, HudOperation.ACTION_BAR, buffer -> {
            HudPacketCodec.writeText(buffer, text, player);
            buffer.writeBoolean(animateColor);
            buffer.writeVarInt(repeatDelayTicks);
        });
    }

    public void clearActionBar(EntityPlayer player) {
        showActionBar(player, TextComponent.literal(""));
    }

    public void showHint(EntityPlayer player, TextComponent text, HintPosition position) {
        showHint(player, text, position, 0);
    }

    /** Uses the native Action Bar at HOTBAR, or an independent slot at the specified position. */
    public void showHint(EntityPlayer player, TextComponent text, HintPosition position, int repeatDelayTicks) {
        Objects.requireNonNull(position, "position");
        if (repeatDelayTicks < 0) throw new IllegalArgumentException("Repeat delay must not be negative");
        if (position.equals(HintPosition.HOTBAR)) {
            showActionBar(player, text, false, repeatDelayTicks);
            return;
        }
        send(player, HudOperation.HINT, buffer -> {
            HudPacketCodec.writeText(buffer, text, player);
            HudPacketCodec.writeHintPosition(buffer, position);
            buffer.writeVarInt(repeatDelayTicks);
        });
    }

    /** Clears this channel's positioned hint; HOTBAR retains the native shared-slot behavior. */
    public void clearHint(EntityPlayer player, HintPosition position) {
        showHint(player, TextComponent.literal(""), position);
    }

    public void setTitle(EntityPlayer player, TextComponent title) {
        send(player, HudOperation.TITLE, buffer -> HudPacketCodec.writeText(buffer, title, player));
    }

    public void setSubtitle(EntityPlayer player, TextComponent subtitle) {
        send(player, HudOperation.SUBTITLE, buffer -> HudPacketCodec.writeText(buffer, subtitle, player));
    }

    /** Negative values preserve the corresponding time, as in vanilla /title times. */
    public void setTitleTimes(EntityPlayer player, int fadeInTicks, int stayTicks, int fadeOutTicks) {
        send(player, HudOperation.TITLE_TIMES, buffer -> {
            buffer.writeInt(fadeInTicks);
            buffer.writeInt(stayTicks);
            buffer.writeInt(fadeOutTicks);
        });
    }

    public void showTitle(EntityPlayer player, TextComponent title, TextComponent subtitle,
                          int fadeInTicks, int stayTicks, int fadeOutTicks) {
        setTitleTimes(player, fadeInTicks, stayTicks, fadeOutTicks);
        setSubtitle(player, subtitle);
        setTitle(player, title);
    }

    /** Clear only removes text; reset also restores vanilla's 10/70/20 tick times. */
    public void clearTitles(EntityPlayer player, boolean resetTimes) {
        send(player, HudOperation.CLEAR_TITLES, buffer -> buffer.writeBoolean(resetTimes));
    }

    public void setTabHeader(EntityPlayer player, TextComponent header) {
        send(player, HudOperation.TAB_HEADER, buffer -> HudPacketCodec.writeText(buffer, header, player));
    }

    public void setTabFooter(EntityPlayer player, TextComponent footer) {
        send(player, HudOperation.TAB_FOOTER, buffer -> HudPacketCodec.writeText(buffer, footer, player));
    }

    public void setTabHeaderAndFooter(EntityPlayer player, TextComponent header, TextComponent footer) {
        setTabHeader(player, header);
        setTabFooter(player, footer);
    }

    public void clearTabList(EntityPlayer player) {
        setTabHeaderAndFooter(player, TextComponent.literal(""), TextComponent.literal(""));
    }

    /** Custom types define their common codec and register a renderer on the client. */
    public <T> void showToast(EntityPlayer player, ToastType<T> type, T data) {
        send(player, HudOperation.TOAST, buffer -> {
            buffer.writeString(type.getId().toString());
            type.write(buffer, data);
        });
    }

    public void showToast(EntityPlayer player, SystemToastData notification) {
        if (player instanceof ServerPlayer) {
            notification = new SystemToastData(notification.token(),
                    ChatComponents.resolveFor(notification.title(), player, player),
                    notification.message() == null ? null : ChatComponents.resolveFor(notification.message(), player, player),
                    notification.displayTimeMillis(), notification.multiline());
        }
        showToast(player, HudToasts.SYSTEM, notification);
    }

    public void removeToast(EntityPlayer player, ResourceLocation token) {
        send(player, HudOperation.REMOVE_TOAST, buffer -> buffer.writeString(token.toString()));
    }

    public ServerBossBar createBossBar(TextComponent name, BossBarColor color, BossBarOverlay overlay) {
        return new ServerBossBar(this, name, color, overlay);
    }

    void sendBossBar(ServerPlayer player, BossBar bar, HudOperation operation) {
        send(player, operation, buffer -> {
            HudPacketCodec.writeUuid(buffer, bar.getId());
            if (operation == HudOperation.BOSS_ADD || operation == HudOperation.BOSS_NAME) {
                HudPacketCodec.writeText(buffer, bar.getName(), player);
            }
            if (operation == HudOperation.BOSS_ADD || operation == HudOperation.BOSS_PROGRESS) {
                buffer.writeFloat(bar.getProgress());
            }
            if (operation == HudOperation.BOSS_ADD || operation == HudOperation.BOSS_STYLE) {
                buffer.writeByte(bar.getColor().ordinal());
                buffer.writeByte(bar.getOverlay().ordinal());
            }
            if (operation == HudOperation.BOSS_ADD || operation == HudOperation.BOSS_PROPERTIES) {
                buffer.writeBoolean(bar.shouldDarkenScreen());
                buffer.writeBoolean(bar.shouldPlayBossMusic());
                buffer.writeBoolean(bar.shouldCreateWorldFog());
            }
        });
    }

    void removeBossBar(ServerPlayer player, UUID id) {
        send(player, HudOperation.BOSS_REMOVE, buffer -> HudPacketCodec.writeUuid(buffer, id));
    }

    private void send(EntityPlayer player, HudOperation operation, Consumer<PacketByteBuf> writer) {
        S2CHudPacket packet = new S2CHudPacket(this, operation, writer);
        if (player instanceof ServerPlayer serverPlayer) {
            Network.sendToClient(serverPlayer, packet);
        } else if (player.onClient()) {
            packet.apply(player);
        }
    }
}
