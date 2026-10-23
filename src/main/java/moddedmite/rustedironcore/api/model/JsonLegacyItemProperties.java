package moddedmite.rustedironcore.api.model;

import com.google.gson.JsonObject;
import net.minecraft.EntityLivingBase;
import net.minecraft.EntityPlayer;
import net.minecraft.ItemStack;

/** Modern properties whose state exists in legacy MITE. Unsupported properties keep their fallback. */
final class JsonLegacyItemProperties implements JsonItemDefinition.Properties {

    private final ItemStack stack;
    private final EntityLivingBase entity;
    private final JsonModelDisplayContext context;

    JsonLegacyItemProperties(ItemStack stack, EntityLivingBase entity, JsonModelDisplayContext context) {
        this.stack = stack; this.entity = entity; this.context = context;
    }

    private boolean using() {
        return entity instanceof EntityPlayer player && player.isUsingItem()
                && ItemStack.areItemStacksEqual(player.getItemInUse(), stack, true);
    }

    @Override
    public String selected(String property, JsonObject options) {
        return switch (property) {
            case "minecraft:display_context", "display_context" -> context.getJsonName();
            case "minecraft:main_hand", "main_hand" -> "right";
            default -> "";
        };
    }

    @Override
    public boolean condition(String property, JsonObject options) {
        return switch (property) {
            case "minecraft:using_item", "using_item" -> using();
            case "minecraft:damaged", "damaged" -> stack.isItemDamaged();
            case "minecraft:broken", "broken" -> stack.isItemStackDamageable() && stack.getItemDamage() >= stack.getMaxDamage() - 1;
            default -> false;
        };
    }

    @Override
    public float number(String property, JsonObject options) {
        return switch (property) {
            case "minecraft:use_duration", "use_duration" -> {
                if (!using()) yield 0;
                EntityPlayer player = (EntityPlayer) entity;
                float remaining = player.getItemInUseCount() - JsonModelRenderContext.partialTick();
                yield options.has("remaining") && options.get("remaining").getAsBoolean() ? remaining
                        : stack.getMaxItemUseDuration() - remaining;
            }
            case "minecraft:damage", "damage" -> options.has("normalize") && !options.get("normalize").getAsBoolean()
                    ? stack.getItemDamage() : stack.getItemDamage() / (float) Math.max(1, stack.getMaxDamage());
            case "minecraft:count", "count" -> options.has("normalize") && !options.get("normalize").getAsBoolean()
                    ? stack.stackSize : stack.stackSize / (float) Math.max(1, stack.getMaxStackSize());
            default -> Float.NaN;
        };
    }
}
