package moddedmite.rustedironcore.api.chat;

import javax.annotation.Nullable;
import java.util.Objects;

public final class TextStyle {

    public static final TextStyle EMPTY = new TextStyle(null, null, null, null, null, null, null, null, null, null);

    @Nullable private final TextColor color;
    @Nullable private final Boolean bold;
    @Nullable private final Boolean italic;
    @Nullable private final Boolean underlined;
    @Nullable private final Boolean strikethrough;
    @Nullable private final Boolean obfuscated;
    @Nullable private final ChatClickEvent clickEvent;
    @Nullable private final TextHoverEvent hoverEvent;
    @Nullable private final String insertion;
    @Nullable private final String font;

    public TextStyle(@Nullable TextColor color, @Nullable Boolean bold, @Nullable Boolean italic,
                     @Nullable Boolean underlined, @Nullable Boolean strikethrough,
                     @Nullable Boolean obfuscated, @Nullable ChatClickEvent clickEvent,
                     @Nullable TextHoverEvent hoverEvent, @Nullable String insertion, @Nullable String font) {
        this.color = color;
        this.bold = bold;
        this.italic = italic;
        this.underlined = underlined;
        this.strikethrough = strikethrough;
        this.obfuscated = obfuscated;
        this.clickEvent = clickEvent;
        this.hoverEvent = hoverEvent;
        this.insertion = insertion;
        this.font = TextFonts.normalize(font);
    }

    @Nullable public TextColor getColor() { return this.color; }
    @Nullable public Boolean getBold() { return this.bold; }
    @Nullable public Boolean getItalic() { return this.italic; }
    @Nullable public Boolean getUnderlined() { return this.underlined; }
    @Nullable public Boolean getStrikethrough() { return this.strikethrough; }
    @Nullable public Boolean getObfuscated() { return this.obfuscated; }
    @Nullable public ChatClickEvent getClickEvent() { return this.clickEvent; }
    @Nullable public TextHoverEvent getHoverEvent() { return this.hoverEvent; }
    @Nullable public String getInsertion() { return this.insertion; }
    @Nullable public String getFont() { return this.font; }

    public TextStyle withColor(@Nullable TextColor value) { return this.copy(value, this.bold, this.italic, this.underlined, this.strikethrough, this.obfuscated, this.clickEvent, this.hoverEvent, this.insertion, this.font); }
    public TextStyle withBold(@Nullable Boolean value) { return this.copy(this.color, value, this.italic, this.underlined, this.strikethrough, this.obfuscated, this.clickEvent, this.hoverEvent, this.insertion, this.font); }
    public TextStyle withItalic(@Nullable Boolean value) { return this.copy(this.color, this.bold, value, this.underlined, this.strikethrough, this.obfuscated, this.clickEvent, this.hoverEvent, this.insertion, this.font); }
    public TextStyle withUnderlined(@Nullable Boolean value) { return this.copy(this.color, this.bold, this.italic, value, this.strikethrough, this.obfuscated, this.clickEvent, this.hoverEvent, this.insertion, this.font); }
    public TextStyle withStrikethrough(@Nullable Boolean value) { return this.copy(this.color, this.bold, this.italic, this.underlined, value, this.obfuscated, this.clickEvent, this.hoverEvent, this.insertion, this.font); }
    public TextStyle withObfuscated(@Nullable Boolean value) { return this.copy(this.color, this.bold, this.italic, this.underlined, this.strikethrough, value, this.clickEvent, this.hoverEvent, this.insertion, this.font); }
    public TextStyle withClickEvent(@Nullable ChatClickEvent value) { return this.copy(this.color, this.bold, this.italic, this.underlined, this.strikethrough, this.obfuscated, value, this.hoverEvent, this.insertion, this.font); }
    public TextStyle withHoverEvent(@Nullable TextHoverEvent value) { return this.copy(this.color, this.bold, this.italic, this.underlined, this.strikethrough, this.obfuscated, this.clickEvent, value, this.insertion, this.font); }
    public TextStyle withInsertion(@Nullable String value) { return this.copy(this.color, this.bold, this.italic, this.underlined, this.strikethrough, this.obfuscated, this.clickEvent, this.hoverEvent, value, this.font); }
    public TextStyle withFont(@Nullable String value) { return this.copy(this.color, this.bold, this.italic, this.underlined, this.strikethrough, this.obfuscated, this.clickEvent, this.hoverEvent, this.insertion, value); }

    public TextStyle applyTo(TextStyle inherited) {
        Objects.requireNonNull(inherited, "inherited");
        return new TextStyle(
                this.color != null ? this.color : inherited.color,
                this.bold != null ? this.bold : inherited.bold,
                this.italic != null ? this.italic : inherited.italic,
                this.underlined != null ? this.underlined : inherited.underlined,
                this.strikethrough != null ? this.strikethrough : inherited.strikethrough,
                this.obfuscated != null ? this.obfuscated : inherited.obfuscated,
                this.clickEvent != null ? this.clickEvent : inherited.clickEvent,
                this.hoverEvent != null ? this.hoverEvent : inherited.hoverEvent,
                this.insertion != null ? this.insertion : inherited.insertion,
                this.font != null ? this.font : inherited.font
        );
    }

    public boolean isEmpty() {
        return this == EMPTY || (this.color == null && this.bold == null && this.italic == null
                && this.underlined == null && this.strikethrough == null && this.obfuscated == null
                && this.clickEvent == null && this.hoverEvent == null && this.insertion == null && this.font == null);
    }

    private TextStyle copy(@Nullable TextColor color, @Nullable Boolean bold, @Nullable Boolean italic,
                           @Nullable Boolean underlined, @Nullable Boolean strikethrough,
                           @Nullable Boolean obfuscated, @Nullable ChatClickEvent clickEvent,
                           @Nullable TextHoverEvent hoverEvent, @Nullable String insertion, @Nullable String font) {
        return new TextStyle(color, bold, italic, underlined, strikethrough, obfuscated,
                clickEvent, hoverEvent, insertion, font);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof TextStyle that)) return false;
        return Objects.equals(this.color, that.color) && Objects.equals(this.bold, that.bold)
                && Objects.equals(this.italic, that.italic) && Objects.equals(this.underlined, that.underlined)
                && Objects.equals(this.strikethrough, that.strikethrough) && Objects.equals(this.obfuscated, that.obfuscated)
                && Objects.equals(this.clickEvent, that.clickEvent) && Objects.equals(this.hoverEvent, that.hoverEvent)
                && Objects.equals(this.insertion, that.insertion) && Objects.equals(this.font, that.font);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.color, this.bold, this.italic, this.underlined, this.strikethrough,
                this.obfuscated, this.clickEvent, this.hoverEvent, this.insertion, this.font);
    }
}
