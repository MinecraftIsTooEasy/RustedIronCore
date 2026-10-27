package moddedmite.rustedironcore.api.chat;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.UnaryOperator;

public final class TextComponent {

    private final Content content;
    private final List<TextComponent> siblings = new ArrayList<>();
    private TextStyle style = TextStyle.EMPTY;

    public TextComponent(Content content) {
        this.content = Objects.requireNonNull(content, "content");
    }

    public static TextComponent literal(String text) { return new TextComponent(new Literal(text)); }
    public static TextComponent translatable(String key, Object... arguments) {
        return new TextComponent(new Translatable(key, null, Arrays.asList(arguments)));
    }
    public static TextComponent translatable(String key, @Nullable String fallback, List<?> arguments) {
        return new TextComponent(new Translatable(key, fallback, new ArrayList<Object>(arguments)));
    }
    public static TextComponent keybind(String name) { return new TextComponent(new Keybind(name)); }
    public static TextComponent score(String name, String objective) { return new TextComponent(new Score(name, objective)); }
    public static TextComponent selector(String pattern) { return new TextComponent(new Selector(pattern, Optional.empty())); }
    public static TextComponent nbt(String path, boolean interpret, NbtSource source) {
        return new TextComponent(new Nbt(path, interpret, Optional.empty(), source));
    }

    public Content getContent() { return this.content; }
    public List<TextComponent> getSiblings() { return Collections.unmodifiableList(this.siblings); }
    public TextStyle getStyle() { return this.style; }

    public TextComponent append(TextComponent sibling) {
        this.siblings.add(Objects.requireNonNull(sibling, "sibling"));
        return this;
    }

    public TextComponent setStyle(TextStyle style) {
        this.style = Objects.requireNonNull(style, "style");
        return this;
    }

    public TextComponent withStyle(UnaryOperator<TextStyle> modifier) {
        return this.setStyle(Objects.requireNonNull(modifier.apply(this.style), "modified style"));
    }

    public TextComponent copy() {
        TextComponent copy = new TextComponent(copyContent(this.content)).setStyle(this.style);
        for (TextComponent sibling : this.siblings) copy.append(sibling.copy());
        return copy;
    }

    private static Content copyContent(Content content) {
        if (content instanceof Translatable translatable) {
            List<Object> arguments = new ArrayList<>(translatable.arguments().size());
            for (Object argument : translatable.arguments()) {
                arguments.add(argument instanceof TextComponent component ? component.copy() : argument);
            }
            return new Translatable(translatable.key(), translatable.fallback(), arguments);
        }
        if (content instanceof Selector selector) {
            return new Selector(selector.pattern(), selector.separator().map(TextComponent::copy));
        }
        if (content instanceof Nbt nbt) {
            return new Nbt(nbt.path(), nbt.interpret(), nbt.separator().map(TextComponent::copy), nbt.source());
        }
        return content;
    }

    @Nullable
    public String tryCollapseToString() {
        if (!(this.content instanceof Literal literal) || !this.style.isEmpty() || !this.siblings.isEmpty()) return null;
        return literal.text();
    }

    public sealed interface Content permits Literal, Translatable, Keybind, Score, Selector, Nbt {}

    public record Literal(String text) implements Content {
        public Literal { Objects.requireNonNull(text, "text"); }
    }

    public record Translatable(String key, @Nullable String fallback, List<Object> arguments) implements Content {
        public Translatable {
            Objects.requireNonNull(key, "key");
            arguments = Collections.unmodifiableList(new ArrayList<>(arguments));
            for (Object argument : arguments) {
                if (!(argument instanceof String || argument instanceof Number || argument instanceof Boolean || argument instanceof TextComponent)) {
                    throw new IllegalArgumentException("Unsupported translation argument: " + argument);
                }
            }
        }
    }

    public record Keybind(String name) implements Content {
        public Keybind { Objects.requireNonNull(name, "name"); }
    }

    public record Score(String name, String objective) implements Content {
        public Score {
            Objects.requireNonNull(name, "name");
            Objects.requireNonNull(objective, "objective");
        }
    }

    public record Selector(String pattern, Optional<TextComponent> separator) implements Content {
        public Selector {
            Objects.requireNonNull(pattern, "pattern");
            separator = Objects.requireNonNull(separator, "separator");
        }
    }

    public record Nbt(String path, boolean interpret, Optional<TextComponent> separator, NbtSource source) implements Content {
        public Nbt {
            Objects.requireNonNull(path, "path");
            separator = Objects.requireNonNull(separator, "separator");
            Objects.requireNonNull(source, "source");
        }
    }

    public sealed interface NbtSource permits BlockData, EntityData, StorageData {}

    public record BlockData(String position) implements NbtSource {
        public BlockData { Objects.requireNonNull(position, "position"); }
    }

    public record EntityData(String selector) implements NbtSource {
        public EntityData { Objects.requireNonNull(selector, "selector"); }
    }

    public record StorageData(String id) implements NbtSource {
        public StorageData { Objects.requireNonNull(id, "id"); }
    }
}
