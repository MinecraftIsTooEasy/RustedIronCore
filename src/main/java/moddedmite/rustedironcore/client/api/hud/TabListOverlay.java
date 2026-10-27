package moddedmite.rustedironcore.client.api.hud;

import moddedmite.rustedironcore.api.chat.TextComponent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.Minecraft;

import java.util.List;

@Environment(EnvType.CLIENT)
public final class TabListOverlay {

    private TextComponent header;
    private TextComponent footer;
    private int backgroundWidth;

    public void setHeader(TextComponent header) {
        this.header = header;
    }

    public void setFooter(TextComponent footer) {
        this.footer = footer;
    }

    public void clear() {
        header = null;
        footer = null;
    }

    public int getBackgroundWidth() {
        return backgroundWidth;
    }

    /** Renders the decorations, then offsets MITE's existing player rows without replacing their contents. */
    public boolean beginPlayerList(HudGraphics graphics) {
        List<String> headerLines = lines(graphics, header);
        List<String> footerLines = lines(graphics, footer);
        backgroundWidth = 0;
        if (headerLines.isEmpty() && footerLines.isEmpty()) return false;

        Minecraft client = graphics.getMinecraft();
        int playerCount = client.thePlayer.sendQueue.currentServerMaxPlayers;
        int rows = playerCount;
        int columns = 1;
        while (rows > 20) rows = (playerCount + ++columns - 1) / columns;
        int width = Math.min(150, 300 / columns) * columns;
        for (String line : headerLines) width = Math.max(width, graphics.getFont().getStringWidth(line));
        for (String line : footerLines) width = Math.max(width, graphics.getFont().getStringWidth(line));
        backgroundWidth = width;
        int center = graphics.guiWidth() / 2;
        int offset = headerLines.isEmpty() ? 0 : headerLines.size() * 9 + 1;
        graphics.begin();
        try {
            if (!headerLines.isEmpty()) {
                graphics.fill(center - width / 2 - 1, 9, center + width / 2 + 1, 10 + headerLines.size() * 9, 0x80000000);
            }
            int y = 10;
            for (String line : headerLines) {
                graphics.drawString(line, center - graphics.getFont().getStringWidth(line) / 2, y, 0xFFFFFF, true);
                y += 9;
            }
            y = 10 + offset + rows * 9 + 1;
            if (!footerLines.isEmpty()) {
                graphics.fill(center - width / 2 - 1, y - 1, center + width / 2 + 1,
                        y + footerLines.size() * 9, 0x80000000);
            }
            for (String line : footerLines) {
                graphics.drawString(line, center - graphics.getFont().getStringWidth(line) / 2, y, 0xFFFFFF, true);
                y += 9;
            }
        } finally {
            graphics.end();
        }
        graphics.pushPose();
        graphics.translate(0, offset, 0);
        return true;
    }

    private static List<String> lines(HudGraphics graphics, TextComponent text) {
        if (text == null || graphics.format(text).replace("\u00a7r", "").isEmpty()) return List.of();
        return graphics.split(text, graphics.guiWidth() - 50);
    }
}
