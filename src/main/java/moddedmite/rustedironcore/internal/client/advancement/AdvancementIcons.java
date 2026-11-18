package moddedmite.rustedironcore.internal.client.advancement;

import com.google.gson.JsonObject;
import moddedmite.rustedironcore.internal.advancement.AdvancementStacks;
import moddedmite.rustedironcore.api.util.LogUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.Achievement;
import net.minecraft.Item;
import net.minecraft.ItemStack;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

@Environment(EnvType.CLIENT)
public class AdvancementIcons {

    private static final Set<Integer> INVALID_NATIVE_ICONS = new HashSet<>();

    public static ItemStack nativeIcon(Achievement achievement, @Nullable ItemStack icon) {
        if (icon != null && icon.getItem() != null) return icon;
        if (INVALID_NATIVE_ICONS.add(achievement.statId)) {
            LogUtil.getLogger().warn("Invalid item {} in native achievement {} ({}); using paper as its icon",
                    icon == null ? null : icon.itemID, achievement.statId, achievement.getName());
        }
        return new ItemStack(Item.paper);
    }

    public static ItemStack icon(JsonObject json) {
        try {
            return AdvancementStacks.create(json);
        } catch (RuntimeException exception) {
            LogUtil.getLogger().warn("Unable to create advancement icon {}: {}", json, exception.getMessage());
            return new ItemStack(Item.paper);
        }
    }
}
