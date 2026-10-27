package moddedmite.rustedironcore.client.api.hud;

import moddedmite.rustedironcore.api.hud.ToastType;
import moddedmite.rustedironcore.internal.client.hud.ToastInstance;
import moddedmite.rustedironcore.network.PacketByteBuf;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ResourceLocation;

import javax.annotation.Nullable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Environment(EnvType.CLIENT)
public final class ToastManager {

    private final List<ToastInstance> visible = new ArrayList<>();
    private final BitSet occupiedSlots = new BitSet(5);
    private final Deque<Toast> queued = new ArrayDeque<>();
    private final Map<String, Function<PacketByteBuf, Toast>> renderers = new HashMap<>();
    private double displayTimeMultiplier = 1;

    public <T> void registerType(ToastType<T> type, Function<T, Toast> factory) {
        if (renderers.putIfAbsent(type.getId().toString(), buffer -> factory.apply(type.read(buffer))) != null) {
            throw new IllegalArgumentException("Toast renderer already registered: " + type.getId());
        }
    }

    public void receive(ResourceLocation type, PacketByteBuf buffer) {
        Function<PacketByteBuf, Toast> renderer = renderers.get(type.toString());
        if (renderer == null) throw new IllegalArgumentException("Toast renderer is not registered: " + type);
        addOrUpdateToast(renderer.apply(buffer));
    }

    public void addToast(Toast toast) {
        queued.add(toast);
    }

    public void addOrUpdateToast(Toast toast) {
        Toast existing = getToast(toast.getClass(), toast.getToken());
        if (existing == null || !existing.update(toast)) addToast(toast);
    }

    @Nullable
    public <T extends Toast> T getToast(Class<T> type, Object token) {
        for (ToastInstance instance : visible) {
            if (type.isInstance(instance.getToast()) && instance.getToast().getToken().equals(token)) {
                return type.cast(instance.getToast());
            }
        }
        for (Toast toast : queued) {
            if (type.isInstance(toast) && toast.getToken().equals(token)) return type.cast(toast);
        }
        return null;
    }

    public void removeToast(Object token) {
        queued.removeIf(toast -> toast.getToken().equals(token));
        for (ToastInstance instance : visible) {
            if (instance.getToast().getToken().equals(token)) instance.hide();
        }
    }

    public double getNotificationDisplayTimeMultiplier() {
        return displayTimeMultiplier;
    }

    public void setNotificationDisplayTimeMultiplier(double multiplier) {
        if (!Double.isFinite(multiplier) || multiplier <= 0) throw new IllegalArgumentException("Invalid display time multiplier");
        displayTimeMultiplier = multiplier;
    }

    public void clear() {
        occupiedSlots.clear();
        visible.clear();
        queued.clear();
    }

    public boolean isEmpty() {
        return visible.isEmpty() && queued.isEmpty();
    }

    public void render(HudGraphics graphics) {
        if (isEmpty()) return;
        graphics.begin();
        try {
            visible.removeIf(instance -> {
                if (!instance.render(graphics, this)) return false;
                occupiedSlots.clear(instance.getIndex(), instance.getIndex() + instance.getSlots());
                return true;
            });
            if (!queued.isEmpty() && occupiedSlots.cardinality() < 5) {
                queued.removeIf(toast -> {
                    int slots = toast.slotCount();
                    int first = findFreeIndex(slots);
                    if (first < 0) return false;
                    visible.add(new ToastInstance(toast, first, slots));
                    occupiedSlots.set(first, first + slots);
                    return true;
                });
            }
        } finally {
            graphics.end();
        }
    }

    private int findFreeIndex(int slots) {
        if (5 - occupiedSlots.cardinality() < slots) return -1;
        int consecutive = 0;
        for (int index = 0; index < 5; index++) {
            if (occupiedSlots.get(index)) consecutive = 0;
            else if (++consecutive == slots) return index + 1 - slots;
        }
        return -1;
    }
}
