package moddedmite.rustedironcore.internal.advancement;

import com.google.gson.JsonObject;
import moddedmite.rustedironcore.api.chat.TextColor;
import moddedmite.rustedironcore.api.chat.TextComponent;
import moddedmite.rustedironcore.api.chat.TextComponentSerializer;
import moddedmite.rustedironcore.api.chat.TextHoverEvent;
import moddedmite.rustedironcore.api.chat.TextStyle;

import java.util.List;

public class AdvancementChat {

    public static TextComponent announcement(String playerName, JsonObject display) {
        String frame = AdvancementJson.string(display, "frame", "task");
        TextStyle frameStyle = TextStyle.EMPTY.withColor(TextColor.parse(frame.equals("challenge") ? "dark_purple" : "green"));
        TextComponent title = TextComponentSerializer.fromJson(display.get("title"));
        TextComponent description = TextComponentSerializer.fromJson(display.get("description"));
        TextComponent tooltip = title.copy().withStyle(style -> style.applyTo(frameStyle))
                .append(TextComponent.literal("\n")).append(description);
        TextHoverEvent hover = new TextHoverEvent(TextHoverEvent.Action.SHOW_TEXT, TextComponentSerializer.toJsonElement(tooltip));
        TextComponent hoveredTitle = title.copy().withStyle(style -> style.withHoverEvent(hover));
        TextComponent name = TextComponent.translatable("chat.square_brackets", "[%s]", List.of(hoveredTitle))
                .setStyle(frameStyle);
        return TextComponent.translatable("chat.type.advancement." + frame, playerName, name);
    }
}
