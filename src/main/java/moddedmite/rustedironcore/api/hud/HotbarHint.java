package moddedmite.rustedironcore.api.hud;

import moddedmite.rustedironcore.api.chat.TextComponent;
import net.minecraft.EntityPlayer;
import net.minecraft.ResourceLocation;

import java.util.Objects;

/**
 * A mod-owned hint channel, shown above the hotbar by default or at a registered position.
 * Register once during common initialization on both the client and server.
 * Call from either side, but only once for an interaction handled on both sides.
 */
public final class HotbarHint {

    private static final int DEFAULT_REPEAT_DELAY_TICKS = 10;

    private final HudChannel channel;
    private final HintPosition position;
    private final int repeatDelayTicks;

    private HotbarHint(HudChannel channel, HintPosition position, int repeatDelayTicks) {
        this.channel = channel;
        this.position = position;
        this.repeatDelayTicks = repeatDelayTicks;
    }

    public static HotbarHint register(ResourceLocation channel) {
        return register(channel, DEFAULT_REPEAT_DELAY_TICKS);
    }

    /**
     * Registers the packet reader for this channel. A delay of zero allows every repeat.
     * Like other custom payload channels, use a short namespaced ID (at most 20 characters).
     */
    public static HotbarHint register(ResourceLocation channel, int repeatDelayTicks) {
        return register(channel, HintPosition.HOTBAR, repeatDelayTicks);
    }

    public static HotbarHint register(ResourceLocation channel, HintPosition position) {
        return register(channel, position, DEFAULT_REPEAT_DELAY_TICKS);
    }

    /** Registers a fixed anchor and offset for this mod-owned hint channel. */
    public static HotbarHint register(ResourceLocation channel, HintPosition position, int repeatDelayTicks) {
        Objects.requireNonNull(position, "position");
        if (repeatDelayTicks < 0) throw new IllegalArgumentException("Repeat delay must not be negative");
        return new HotbarHint(HudChannel.register(channel), position, repeatDelayTicks);
    }

    public ResourceLocation getChannel() {
        return channel.getId();
    }

    public int getRepeatDelayTicks() {
        return repeatDelayTicks;
    }

    public HintPosition getPosition() {
        return position;
    }

    /** Displays literal text locally, or sends it to the specified server player. */
    public void show(EntityPlayer player, String message) {
        show(player, TextComponent.literal(message));
    }

    public void show(EntityPlayer player, TextComponent message) {
        channel.showHint(player, message, position, repeatDelayTicks);
    }

    /**
     * Translates on the receiving client, preserving its language and numeric formatting.
     * Arguments may be strings, booleans, or boxed primitive numbers.
     */
    public void showTranslated(EntityPlayer player, String translationKey, Object... arguments) {
        show(player, TextComponent.translatable(translationKey, arguments));
    }

    public void clear(EntityPlayer player) {
        channel.clearHint(player, position);
    }
}
