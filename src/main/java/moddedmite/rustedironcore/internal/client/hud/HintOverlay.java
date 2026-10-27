package moddedmite.rustedironcore.internal.client.hud;

import moddedmite.rustedironcore.api.chat.TextComponent;
import moddedmite.rustedironcore.api.hud.HintPosition;
import moddedmite.rustedironcore.api.hud.HudChannel;
import moddedmite.rustedironcore.client.api.hud.HudGraphics;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.LinkedHashMap;
import java.util.Map;

@Environment(EnvType.CLIENT)
public final class HintOverlay {

    private final Map<HintPosition, HintMessage> hints = new LinkedHashMap<>();
    private int tick;

    public void show(HudChannel channel, TextComponent text, HintPosition position, int repeatDelayTicks) {
        HintMessage existing = hints.get(position);
        if ("".equals(text.tryCollapseToString())) {
            if (existing != null && existing.channel() == channel) hints.remove(position);
            return;
        }
        String formatted = ClientHud.getGraphics().format(text);
        if (existing != null && existing.shouldSuppress(channel, formatted, tick, repeatDelayTicks)) return;
        hints.put(position, new HintMessage(channel, formatted, tick, repeatDelayTicks));
    }

    public void tick() {
        ++tick;
        hints.values().removeIf(hint -> hint.hasExpired(tick));
    }

    public void clear() {
        hints.clear();
        tick = 0;
    }

    public void render(HudGraphics graphics, float partialTick) {
        if (hints.isEmpty()) return;
        int width = graphics.guiWidth();
        int height = graphics.guiHeight();
        graphics.begin();
        try {
            for (Map.Entry<HintPosition, HintMessage> entry : hints.entrySet()) {
                HintMessage hint = entry.getValue();
                int alpha = hint.alpha(tick, partialTick);
                if (alpha <= 8) continue;
                HintPosition position = entry.getKey();
                int x = position.x(width, graphics.getFont().getStringWidth(hint.text()));
                int y = position.y(height, graphics.getFont().FONT_HEIGHT);
                graphics.drawStringWithBackdrop(hint.text(), x, y, 0xFFFFFF | alpha << 24);
            }
        } finally {
            graphics.end();
        }
    }
}
