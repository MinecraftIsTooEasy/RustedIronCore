package moddedmite.rustedironcore.internal.client.chat;

import moddedmite.rustedironcore.api.chat.ChatClickEvent;
import moddedmite.rustedironcore.api.chat.TextComponent;
import moddedmite.rustedironcore.api.chat.TextComponentSerializer;
import moddedmite.rustedironcore.api.chat.TextStyle;
import moddedmite.rustedironcore.internal.chat.ChatClickComponentAccess;
import moddedmite.rustedironcore.internal.chat.LegacyTextFormatting;
import moddedmite.rustedironcore.internal.chat.TranslationTemplate;
import net.minecraft.ChatMessageComponent;
import net.minecraft.EnumChatFormatting;
import net.minecraft.GameSettings;
import net.minecraft.I18n;
import net.minecraft.KeyBinding;
import net.minecraft.Minecraft;
import net.minecraft.StatCollector;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;

public final class MiteTextComponentAdapter {

    public static ChatMessageComponent toMiteComponent(TextComponent component) {
        return toMiteComponent(component, TextStyle.EMPTY, new HashSet<>());
    }

    private static ChatMessageComponent toMiteComponent(TextComponent component, TextStyle inherited,
                                                        Set<String> translationStack) {
        TextStyle style = component.getStyle().applyTo(inherited);
        ChatMessageComponent result = createContent(component.getContent(), style, translationStack);
        applyStyle(result, style);
        for (TextComponent sibling : component.getSiblings()) {
            result.appendComponent(toMiteComponent(sibling, style, translationStack));
        }
        return result;
    }

    public static String toFormattedString(TextComponent component) {
        StringBuilder output = new StringBuilder();
        visit(component, (style, text) -> {
            appendStyleFormatting(style, output);
            output.append(text);
        });
        return output.toString();
    }

    public static String format(String text, TextStyle style) {
        StringBuilder output = new StringBuilder();
        appendStyleFormatting(style, output);
        return output.append(text).toString();
    }

    /** Visits resolved text with its inherited style without flattening away font and interaction data. */
    public static void visit(TextComponent component, BiConsumer<TextStyle, String> visitor) {
        visit(component, TextStyle.EMPTY, visitor, new HashSet<>());
    }

    private static void visit(TextComponent component, TextStyle inherited, BiConsumer<TextStyle, String> visitor,
                              Set<String> translationStack) {
        TextStyle style = component.getStyle().applyTo(inherited);
        visitContent(component.getContent(), style, visitor, translationStack);
        for (TextComponent sibling : component.getSiblings()) visit(sibling, style, visitor, translationStack);
    }

    private static void appendStyleFormatting(TextStyle style, StringBuilder output) {
        output.append('\u00a7').append('r');
        if (style.getColor() != null) {
            String name = style.getColor().legacyName();
            if (name == null) {
                output.append(LegacyTextFormatting.rgbCode(style.getColor().rgb()));
            } else {
                EnumChatFormatting color = EnumChatFormatting.func_96300_b(name);
                if (color == null || !color.isColor()) throw new UnsupportedOperationException("Unsupported legacy text color: " + name);
                output.append(color);
            }
        }
        if (Boolean.TRUE.equals(style.getBold())) output.append(EnumChatFormatting.BOLD);
        if (Boolean.TRUE.equals(style.getItalic())) output.append(EnumChatFormatting.ITALIC);
        if (Boolean.TRUE.equals(style.getUnderlined())) output.append(EnumChatFormatting.UNDERLINE);
        if (Boolean.TRUE.equals(style.getStrikethrough())) output.append(EnumChatFormatting.STRIKETHROUGH);
        if (Boolean.TRUE.equals(style.getObfuscated())) output.append(EnumChatFormatting.OBFUSCATED);
    }

    private static void visitContent(TextComponent.Content content, TextStyle inherited, BiConsumer<TextStyle, String> visitor,
                                     Set<String> translationStack) {
        if (content instanceof TextComponent.Literal literal) {
            visitor.accept(inherited, literal.text());
        } else if (content instanceof TextComponent.Translatable translatable) {
            if (!translationStack.add(translatable.key())) return;
            try {
                Optional<TextComponent> componentTranslation = ComponentTranslationManager.get(translatable.key());
                if (componentTranslation.isPresent()) {
                    visit(componentTranslation.get(), inherited, visitor, translationStack);
                    return;
                }

                String template = getTranslationTemplate(translatable.key(), translatable.fallback());
                try {
                    for (Object part : TranslationTemplate.decompose(template, translatable.arguments())) {
                        if (part instanceof TextComponent argument) {
                            visit(argument, inherited, visitor, translationStack);
                        } else {
                            visitor.accept(inherited, part.toString());
                        }
                    }
                } catch (IllegalArgumentException exception) {
                    visitor.accept(inherited, template);
                }
            } finally {
                translationStack.remove(translatable.key());
            }
        } else {
            visitor.accept(inherited, createContent(content, inherited, translationStack).toStringWithFormatting(false));
        }
    }

    private static ChatMessageComponent createContent(TextComponent.Content content, TextStyle inherited,
                                                       Set<String> translationStack) {
        if (content instanceof TextComponent.Literal literal) {
            return ChatMessageComponent.createFromText(literal.text());
        }
        if (content instanceof TextComponent.Translatable translatable) {
            if (!translationStack.add(translatable.key())) {
                return ChatMessageComponent.createFromText("");
            }
            try {
                Optional<TextComponent> componentTranslation = ComponentTranslationManager.get(translatable.key());
                if (componentTranslation.isPresent()) {
                    ChatMessageComponent translated = new ChatMessageComponent();
                    translated.appendComponent(toMiteComponent(componentTranslation.get(), inherited,
                            translationStack));
                    return translated;
                }

                String template = getTranslationTemplate(translatable.key(), translatable.fallback());
                List<Object> parts;
                try {
                    parts = TranslationTemplate.decompose(template, translatable.arguments());
                } catch (IllegalArgumentException exception) {
                    return ChatMessageComponent.createFromText(template);
                }
                ChatMessageComponent translated = new ChatMessageComponent();
                for (Object part : parts) {
                    if (part instanceof TextComponent child) {
                        translated.appendComponent(toMiteComponent(child, inherited, translationStack));
                    } else {
                        translated.appendComponent(ChatMessageComponent.createFromText(part.toString()));
                    }
                }
                return translated;
            } finally {
                translationStack.remove(translatable.key());
            }
        }
        if (content instanceof TextComponent.Keybind keybind) {
            return ChatMessageComponent.createFromText(resolveKeybind(keybind.name()));
        }
        if (content instanceof TextComponent.Score) {
            return createUnresolvedDynamicContent();
        }
        if (content instanceof TextComponent.Selector) {
            return createUnresolvedDynamicContent();
        }
        if (content instanceof TextComponent.Nbt) {
            return createUnresolvedDynamicContent();
        }
        throw new IllegalArgumentException("Unknown text component content: " + content.getClass().getName());
    }

    private static ChatMessageComponent createUnresolvedDynamicContent() {
        // Server-context contents are empty without the original command source.
        return new ChatMessageComponent();
    }

    private static void applyStyle(ChatMessageComponent component, TextStyle style) {
        if (style.getColor() != null) {
            String legacyName = style.getColor().legacyName();
            if (legacyName != null) {
                EnumChatFormatting color = EnumChatFormatting.func_96300_b(legacyName);
                if (color == null || !color.isColor()) {
                    throw new UnsupportedOperationException("Unsupported legacy text color: " + legacyName);
                }
                component.setColor(color);
            }
        }
        if (style.getBold() != null) component.setBold(style.getBold());
        if (style.getItalic() != null) component.setItalic(style.getItalic());
        if (style.getUnderlined() != null) component.setUnderline(style.getUnderlined());
        ChatClickComponentAccess access = (ChatClickComponentAccess) component;
        ChatClickEvent clickEvent = style.getClickEvent();
        if (clickEvent != null) access.ric$setClickEvent(clickEvent);
        if (style.getInsertion() != null) access.ric$setInsertion(style.getInsertion());
        if (style.getHoverEvent() != null) access.ric$setHoverEvent(style.getHoverEvent());
        if (style.getFont() != null) access.ric$setFont(style.getFont());
    }

    private static String getTranslationTemplate(String key, String fallback) {
        if (StatCollector.func_94522_b(key)) {
            return StatCollector.translateToLocal(key);
        }
        return fallback == null ? key : fallback;
    }

    private static String resolveKeybind(String name) {
        Minecraft minecraft = Minecraft.getMinecraft();
        if (minecraft != null && minecraft.gameSettings != null && minecraft.gameSettings.keyBindings != null) {
            for (KeyBinding keyBinding : minecraft.gameSettings.keyBindings) {
                if (name.equals(keyBinding.keyDescription)) {
                    return GameSettings.getKeyDisplayString(keyBinding.keyCode);
                }
            }
        }
        return I18n.getString(name);
    }

}
