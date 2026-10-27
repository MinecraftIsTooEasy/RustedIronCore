package moddedmite.rustedironcore.client.api.hud;

import moddedmite.rustedironcore.api.hud.SystemToastData;
import moddedmite.rustedironcore.internal.client.hud.ClientHud;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ResourceLocation;

import java.util.List;

@Environment(EnvType.CLIENT)
public final class SystemToast implements Toast {

    private static final ResourceLocation BACKGROUND = new ResourceLocation("ric", "textures/gui/sprites/toast/system.png", false);
    private SystemToastData data;
    private List<String> messageLines;
    private final int width;
    private long lastChanged;
    private boolean changed;
    private boolean forceHide;

    public SystemToast(SystemToastData data) {
        this.data = data;
        HudGraphics graphics = ClientHud.getGraphics();
        messageLines = messageLines(graphics);
        if (data.multiline()) {
            width = Math.max(200, messageLines.stream().mapToInt(graphics.getFont()::getStringWidth).max().orElse(200)) + 30;
        } else {
            width = Math.max(160, 30 + Math.max(graphics.width(data.title()),
                    data.message() == null ? 0 : graphics.width(data.message())));
        }
    }

    private List<String> messageLines(HudGraphics graphics) {
        if (data.message() == null) return List.of();
        return data.multiline() ? graphics.split(data.message(), 200) : List.of(graphics.format(data.message()));
    }

    @Override
    public Object getToken() {
        return data.token();
    }

    @Override
    public int width() {
        return width;
    }

    @Override
    public int height() {
        return 20 + Math.max(messageLines.size(), 1) * 12;
    }

    public void forceHide() {
        forceHide = true;
    }

    @Override
    public boolean update(Toast incoming) {
        if (!(incoming instanceof SystemToast notification)) return false;
        data = notification.data;
        messageLines = notification.messageLines;
        changed = true;
        return true;
    }

    @Override
    public ToastVisibility render(HudGraphics graphics, ToastManager manager, long visibleMillis) {
        if (changed) {
            lastChanged = visibleMillis;
            changed = false;
        }
        if (width == 160 && messageLines.size() <= 1) {
            graphics.blit(BACKGROUND, 0, 0, 0, 0, width, height(), 160, 32);
        } else {
            int bottom = Math.min(4, height() - 28);
            backgroundRow(graphics, 0, 0, 28);
            for (int y = 28; y < height() - bottom; y += 10) {
                backgroundRow(graphics, 16, y, Math.min(16, height() - y - bottom));
            }
            backgroundRow(graphics, 32 - bottom, height() - bottom, bottom);
        }
        // 1.21.1 centers a title-only notification vertically.
        graphics.drawString(data.title(), 18, messageLines.isEmpty() ? 12 : 7, 0xFFFFFF00, false);
        for (int index = 0; index < messageLines.size(); index++) {
            graphics.drawString(messageLines.get(index), 18, 18 + index * 12, 0xFFFFFFFF, false);
        }
        return !forceHide && visibleMillis - lastChanged < data.displayTimeMillis() * manager.getNotificationDisplayTimeMultiplier()
                ? ToastVisibility.SHOW : ToastVisibility.HIDE;
    }

    private void backgroundRow(HudGraphics graphics, int v, int y, int height) {
        int left = v == 0 ? 20 : 5;
        int right = Math.min(60, width - left);
        graphics.blit(BACKGROUND, 0, y, 0, v, left, height, 160, 32);
        for (int x = left; x < width - right; x += 64) {
            graphics.blit(BACKGROUND, x, y, 32, v, Math.min(64, width - x - right), height, 160, 32);
        }
        graphics.blit(BACKGROUND, width - right, y, 160 - right, v, right, height, 160, 32);
    }
}
