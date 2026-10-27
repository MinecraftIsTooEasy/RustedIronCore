package moddedmite.rustedironcore.internal.client.chat;

import moddedmite.rustedironcore.internal.command.CommandSuggestionEntry;
import moddedmite.rustedironcore.internal.command.CommandSuggestionResult;
import moddedmite.rustedironcore.internal.command.CommandSyntaxKind;
import moddedmite.rustedironcore.internal.command.CommandSyntaxSpan;
import moddedmite.rustedironcore.internal.network.packets.C2SRequestChatSuggestions;
import moddedmite.rustedironcore.mixin.client.gui.GuiTextFieldAccessor;
import moddedmite.rustedironcore.network.Network;
import net.minecraft.FontRenderer;
import net.minecraft.Gui;
import net.minecraft.GuiChat;
import net.minecraft.GuiTextField;
import net.minecraft.MathHelper;
import net.minecraft.Minecraft;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public final class ChatCommandSuggestions {

    private static final int FILL_COLOR = 0xD0000000;
    private static final int SUGGESTION_LINE_LIMIT = 10;
    private static final int LINE_HEIGHT = 12;
    private static final AtomicInteger NEXT_REQUEST_ID = new AtomicInteger();

    private final GuiChat screen;
    private final GuiTextField input;
    private final Minecraft minecraft;
    private final String initialInput;
    private List<CommandSuggestionEntry> suggestions = List.of();
    private List<CommandSyntaxSpan> syntax = List.of();
    private List<String> usage = List.of();
    private int usageStart;
    private String errorMessage;
    private String requestedInput = "";
    private String originalContents = "";
    private String syntaxInput = "";
    private String pendingCompletionInput;
    private int requestedCursor;
    private int requestId;
    private int pendingCompletionStart;
    private int pendingCompletionEnd;
    private int offset;
    private int current;
    private int rectX;
    private int rectY;
    private int rectWidth;
    private int rectHeight;
    private int lastMouseX = Integer.MIN_VALUE;
    private int lastMouseY = Integer.MIN_VALUE;
    private boolean popupVisible;
    private boolean tabRequested;
    private boolean tabCycles;
    private boolean syntaxReady;
    private boolean allowSuggestions;
    private boolean requestPending;

    public ChatCommandSuggestions(GuiChat screen, GuiTextField input, Minecraft minecraft, String initialInput) {
        this.screen = screen;
        this.input = input;
        this.minecraft = minecraft;
        this.initialInput = initialInput;
    }

    public void refresh() {
        String text = this.input.getText();
        int cursor = this.input.getCursorPosition();
        this.requestedInput = text;
        this.requestedCursor = cursor;
        if (this.pendingCompletionInput != null && !this.pendingCompletionInput.equals(text)) {
            this.pendingCompletionInput = null;
        }
        this.retainMatchingSuggestions(text);
        this.requestId = NEXT_REQUEST_ID.incrementAndGet();
        this.allowSuggestions = !text.equals(this.initialInput);
        this.tabRequested = false;
        this.tabCycles = false;
        if (this.minecraft.thePlayer != null && !text.isEmpty()) {
            this.requestPending = true;
            Network.sendToServer(new C2SRequestChatSuggestions(this.requestId, text, cursor));
        } else {
            this.requestPending = false;
            this.suggestions = List.of();
            this.syntax = List.of();
            this.syntaxReady = false;
            this.usage = List.of();
            this.usageStart = 0;
            this.errorMessage = null;
            this.popupVisible = false;
            this.offset = 0;
            this.current = 0;
        }
    }

    public void receive(int id, String input, int cursor, CommandSuggestionResult result) {
        if (id != this.requestId || !input.equals(this.requestedInput) || cursor != this.requestedCursor
                || !input.equals(this.input.getText()) || cursor != this.input.getCursorPosition()
                || result.suggestions().stream().anyMatch(suggestion -> suggestion.start() < 0
                || suggestion.start() > suggestion.end() || suggestion.end() > input.length())) {
            return;
        }

        boolean popupWasVisible = this.isVisible();
        String previouslySelected = this.suggestions.isEmpty()
                ? null : this.apply(this.suggestions.get(this.current));
        this.requestPending = false;
        this.originalContents = input;
        this.syntaxInput = input;
        this.suggestions = this.sortSuggestions(result.suggestions());
        this.syntax = result.syntax();
        this.syntaxReady = true;
        if (input.equals(this.pendingCompletionInput)) this.pendingCompletionInput = null;
        this.usage = result.usage();
        this.usageStart = result.usageStart();
        this.errorMessage = result.errorMessage();
        this.current = 0;
        if (previouslySelected != null) {
            for (int index = 0; index < this.suggestions.size(); index++) {
                if (this.apply(this.suggestions.get(index)).equals(previouslySelected)) {
                    this.current = index;
                    break;
                }
            }
        }
        this.offset = 0;
        if (!popupWasVisible) {
            this.lastMouseX = Integer.MIN_VALUE;
            this.lastMouseY = Integer.MIN_VALUE;
        }
        this.popupVisible = !this.suggestions.isEmpty() && (this.allowSuggestions || this.tabRequested);
        this.updateBounds();
    }

    public boolean isVisible() {
        return this.popupVisible && !this.suggestions.isEmpty();
    }

    public void hide() {
        this.popupVisible = false;
        this.tabCycles = false;
        this.tabRequested = false;
        this.requestPending = false;
        this.requestId = NEXT_REQUEST_ID.incrementAndGet();
    }

    public void suppressSuggestionsForCurrentInput() {
        this.allowSuggestions = false;
        this.hide();
        this.suggestions = List.of();
        this.syntax = List.of();
        this.syntaxReady = false;
        this.usage = List.of();
        this.errorMessage = null;
    }

    public boolean keyPressed(int keyCode) {
        if (this.requestPending) {
            if (keyCode == Keyboard.KEY_TAB) this.tabRequested = true;
            if (keyCode == Keyboard.KEY_ESCAPE) this.hide();
            return keyCode == Keyboard.KEY_TAB || keyCode == Keyboard.KEY_UP
                    || keyCode == Keyboard.KEY_DOWN || keyCode == Keyboard.KEY_ESCAPE;
        }

        if (this.isVisible()) {
            if (keyCode == Keyboard.KEY_UP) {
                this.cycle(-1);
                this.tabCycles = false;
                return true;
            }
            if (keyCode == Keyboard.KEY_DOWN) {
                this.cycle(1);
                this.tabCycles = false;
                return true;
            }
            if (keyCode == Keyboard.KEY_TAB) {
                if (this.tabCycles) {
                    this.cycle(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)
                            || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT) ? -1 : 1);
                }
                this.useSuggestion();
                return true;
            }
            if (keyCode == Keyboard.KEY_ESCAPE) {
                this.hide();
                return true;
            }
            return false;
        }

        if (keyCode != Keyboard.KEY_TAB) return false;
        this.tabRequested = true;
        if (!this.suggestions.isEmpty()) {
            this.popupVisible = true;
            this.updateBounds();
        } else {
            this.refresh();
            this.tabRequested = true;
        }
        return true;
    }

    public boolean mouseClicked(int mouseX, int mouseY, int button) {
        if (!this.isVisible() || !this.contains(mouseX, mouseY)) return false;
        if (this.requestPending) return true;
        int index = (mouseY - this.rectY) / LINE_HEIGHT + this.offset;
        if (index >= 0 && index < this.suggestions.size()) {
            this.select(index);
            this.useSuggestion();
        }
        return true;
    }

    public boolean mouseScrolled(int delta, int mouseX, int mouseY) {
        if (!this.isVisible() || !this.contains(mouseX, mouseY)) return false;
        if (this.requestPending) return true;
        int maxOffset = Math.max(this.suggestions.size() - SUGGESTION_LINE_LIMIT, 0);
        this.offset = MathHelper.clamp_int(this.offset - Integer.signum(delta), 0, maxOffset);
        return true;
    }

    public void render(int mouseX, int mouseY) {
        if (!this.isVisible()) {
            this.renderUsage();
            return;
        }
        boolean mouseMoved = mouseX != this.lastMouseX || mouseY != this.lastMouseY;
        if (mouseMoved && !this.requestPending) {
            this.lastMouseX = mouseX;
            this.lastMouseY = mouseY;
            if (this.contains(mouseX, mouseY)) {
                int hovered = (mouseY - this.rectY) / LINE_HEIGHT + this.offset;
                if (hovered >= 0 && hovered < this.suggestions.size()) this.select(hovered);
            }
        }

        int visibleCount = Math.min(this.suggestions.size(), SUGGESTION_LINE_LIMIT);
        boolean hasPrevious = this.offset > 0;
        boolean hasNext = this.suggestions.size() > this.offset + visibleCount;
        if (hasPrevious) {
            Gui.drawRect(this.rectX, this.rectY - 1, this.rectX + this.rectWidth, this.rectY, FILL_COLOR);
            this.drawEdge(this.rectY - 1);
        }
        if (hasNext) {
            Gui.drawRect(this.rectX, this.rectY + this.rectHeight,
                    this.rectX + this.rectWidth, this.rectY + this.rectHeight + 1, FILL_COLOR);
            this.drawEdge(this.rectY + this.rectHeight);
        }

        FontRenderer font = this.minecraft.fontRenderer;
        for (int row = 0; row < visibleCount; row++) {
            int index = row + this.offset;
            int y = this.rectY + LINE_HEIGHT * row;
            Gui.drawRect(this.rectX, y, this.rectX + this.rectWidth, y + LINE_HEIGHT, FILL_COLOR);
            font.drawStringWithShadow(this.suggestions.get(index).text(), this.rectX + 1, y + 2,
                    index == this.current ? -256 : -5592406);
        }
        this.renderSelectedTooltip(mouseX, mouseY);
    }

    public int inputTextColor(int inputPosition, int defaultColor) {
        String currentInput = this.input.getText();
        if (currentInput.equals(this.pendingCompletionInput)
                && inputPosition >= this.pendingCompletionStart && inputPosition < this.pendingCompletionEnd) {
            return 0xFFAAAAAA;
        }
        if (!this.syntaxReady || !this.syntaxInput.startsWith("/") || !currentInput.startsWith("/")) {
            return defaultColor;
        }
        if (inputPosition >= sharedPrefixLength(this.syntaxInput, currentInput)) {
            if (!currentInput.startsWith(this.syntaxInput) || this.syntaxInput.isEmpty()) return defaultColor;
            inputPosition = this.syntaxInput.length() - 1;
        }
        for (CommandSyntaxSpan span : this.syntax) {
            if (inputPosition >= span.start() && inputPosition < span.end()) {
                if (span.kind() == CommandSyntaxKind.ERROR) return 0xFFFF5555;
                if (span.kind() == CommandSyntaxKind.ARGUMENT) {
                    return switch (Math.floorMod(span.styleIndex(), 5)) {
                        case 0 -> 0xFF55FFFF;
                        case 1 -> 0xFFFFFF55;
                        case 2 -> 0xFF55FF55;
                        case 3 -> 0xFFFF55FF;
                        default -> 0xFFFFAA00;
                    };
                }
            }
        }
        return 0xFFAAAAAA;
    }

    public String ghostSuffix() {
        if (!this.isVisible() || this.current < 0 || this.current >= this.suggestions.size()) return null;
        String completed = this.apply(this.suggestions.get(this.current));
        String currentText = this.input.getText();
        if (this.input.getCursorPosition() != currentText.length() || !completed.startsWith(currentText)) return null;
        String suffix = completed.substring(currentText.length());
        return suffix.isEmpty() ? null : suffix;
    }

    private List<CommandSuggestionEntry> sortSuggestions(List<CommandSuggestionEntry> entries) {
        if (entries.size() < 2) return entries;
        int cursor = Math.min(this.requestedCursor, this.requestedInput.length());
        String beforeCursor = this.requestedInput.substring(0, cursor);
        int wordStart = lastWordStart(beforeCursor);
        String prefix = beforeCursor.substring(wordStart).toLowerCase(java.util.Locale.ROOT);
        List<CommandSuggestionEntry> matching = new ArrayList<>(entries.size());
        List<CommandSuggestionEntry> namespaced = new ArrayList<>(entries.size());
        List<CommandSuggestionEntry> other = new ArrayList<>(entries.size());
        for (CommandSuggestionEntry entry : entries) {
            String candidate = entry.text().toLowerCase(java.util.Locale.ROOT);
            if (candidate.startsWith(prefix)) matching.add(entry);
            else if (candidate.startsWith("minecraft:" + prefix)) namespaced.add(entry);
            else other.add(entry);
        }
        matching.addAll(namespaced);
        matching.addAll(other);
        return List.copyOf(matching);
    }

    private void retainMatchingSuggestions(String input) {
        if (this.suggestions.isEmpty()) return;

        CommandSuggestionEntry selected = this.suggestions.get(this.current);
        List<CommandSuggestionEntry> matching = new ArrayList<>(this.suggestions.size());
        for (CommandSuggestionEntry suggestion : this.suggestions) {
            String completed = this.apply(suggestion);
            if (completed.startsWith(input) && !completed.equals(input)) matching.add(suggestion);
        }
        this.suggestions = List.copyOf(matching);
        if (matching.isEmpty()) {
            this.popupVisible = false;
            this.current = 0;
            this.offset = 0;
            return;
        }

        this.current = Math.max(0, this.suggestions.indexOf(selected));
        this.offset = Math.min(this.offset, Math.max(this.suggestions.size() - SUGGESTION_LINE_LIMIT, 0));
        this.updateBounds();
    }

    private static int lastWordStart(String text) {
        for (int index = text.length() - 1; index >= 0; index--) {
            if (Character.isWhitespace(text.charAt(index))) return index + 1;
        }
        return 0;
    }

    private static int sharedPrefixLength(String first, String second) {
        int length = Math.min(first.length(), second.length());
        int index = 0;
        while (index < length && first.charAt(index) == second.charAt(index)) index++;
        return index;
    }

    private void updateBounds() {
        if (this.suggestions.isEmpty()) return;
        FontRenderer font = this.minecraft.fontRenderer;
        int visibleCount = Math.min(this.suggestions.size(), SUGGESTION_LINE_LIMIT);
        int width = 0;
        for (CommandSuggestionEntry suggestion : this.suggestions) {
            width = Math.max(width, font.getStringWidth(suggestion.text()));
        }
        this.rectWidth = Math.max(width + 1, 1);
        this.rectHeight = visibleCount * LINE_HEIGHT;
        int rangeStart = this.suggestions.get(0).start();

        int x = this.inputScreenX(rangeStart);
        int minX = 0;
        int maxX = Math.max(minX, this.inputScreenX(0) + this.input.getWidth() - width);
        x = MathHelper.clamp_int(x, minX, maxX);
        this.rectX = x - 1;
        this.rectY = ((GuiTextFieldAccessor) this.input).ric$getYPosition() - 3 - this.rectHeight;
    }

    private void drawEdge(int y) {
        for (int x = 0; x < this.rectWidth; x += 2) {
            Gui.drawRect(this.rectX + x, y, Math.min(this.rectX + x + 1, this.rectX + this.rectWidth), y + 1, -1);
        }
    }

    private boolean contains(int x, int y) {
        return x >= this.rectX && x < this.rectX + this.rectWidth
                && y >= this.rectY && y < this.rectY + this.rectHeight;
    }

    private void cycle(int delta) {
        this.select(this.current + delta);
        int lastVisible = this.offset + SUGGESTION_LINE_LIMIT - 1;
        int maxOffset = Math.max(this.suggestions.size() - SUGGESTION_LINE_LIMIT, 0);
        if (this.current < this.offset) this.offset = MathHelper.clamp_int(this.current, 0, maxOffset);
        else if (this.current > lastVisible) {
            this.offset = MathHelper.clamp_int(this.current + 1 - SUGGESTION_LINE_LIMIT, 0, maxOffset);
        }
    }

    private void select(int index) {
        this.current = Math.floorMod(index, this.suggestions.size());
    }

    private void useSuggestion() {
        CommandSuggestionEntry suggestion = this.suggestions.get(this.current);
        String completed = this.apply(suggestion);
        this.input.setText(completed);
        int cursor = suggestion.start() + suggestion.text().length();
        this.input.setCursorPosition(cursor);
        this.pendingCompletionInput = completed;
        this.pendingCompletionStart = suggestion.start();
        this.pendingCompletionEnd = suggestion.start() + suggestion.text().length();

        // Vanilla keeps the current SuggestionsList alive after a completion so
        // repeated Tab presses cycle through the same branch candidates. A
        // normal edit will call refresh() and request suggestions for the new
        // input.
        this.tabCycles = true;
    }

    private String apply(CommandSuggestionEntry suggestion) {
        int start = Math.min(suggestion.start(), this.originalContents.length());
        int end = Math.min(suggestion.end(), this.originalContents.length());
        return this.originalContents.substring(0, start) + suggestion.text() + this.originalContents.substring(end);
    }

    private void renderSelectedTooltip(int mouseX, int mouseY) {
        if (this.suggestions.isEmpty() || !this.contains(mouseX, mouseY)) return;
        String tooltip = this.suggestions.get(this.current).tooltip();
        if (tooltip == null) return;
        FontRenderer font = this.minecraft.fontRenderer;
        List<?> wrappedLines = font.listFormattedStringToWidth(tooltip, 200);
        List<String> lines = new ArrayList<>(wrappedLines.size());
        for (Object line : wrappedLines) lines.add((String) line);
        ChatHoverTooltipRenderer.render(font, lines, mouseX, mouseY, this.screen.width, this.screen.height);
    }

    private void renderUsage() {
        if (this.requestPending) return;
        List<String> lines = this.usage.isEmpty()
                ? this.errorMessage == null ? List.of() : List.of(this.errorMessage)
                : this.usage;
        if (lines.isEmpty()) return;
        FontRenderer font = this.minecraft.fontRenderer;
        int width = 0;
        for (String line : lines) width = Math.max(width, font.getStringWidth(line));
        int start = MathHelper.clamp_int(this.usageStart, 0, this.requestedInput.length());
        int x = this.inputScreenX(start);
        x = MathHelper.clamp_int(x, 0, Math.max(0, this.screen.width - width - 2));
        for (int index = 0; index < lines.size(); index++) {
            int y = ((GuiTextFieldAccessor) this.input).ric$getYPosition() - 3 - LINE_HEIGHT * (index + 1);
            Gui.drawRect(x - 1, y, x + width + 1, y + LINE_HEIGHT, FILL_COLOR);
            font.drawStringWithShadow(lines.get(index), x, y + 2,
                    this.errorMessage != null && this.usage.isEmpty() ? 0xFFFF5555 : 0xFFFFFFFF);
        }
    }

    private int inputScreenX(int inputPosition) {
        GuiTextFieldAccessor accessor = (GuiTextFieldAccessor) this.input;
        int lineStart = accessor.ric$getLineScrollOffset();
        int x = accessor.ric$getXPosition();
        if (inputPosition > lineStart) {
            int end = Math.min(inputPosition, this.input.getText().length());
            x += this.minecraft.fontRenderer.getStringWidth(this.input.getText().substring(lineStart, end));
        }
        return x;
    }
}
