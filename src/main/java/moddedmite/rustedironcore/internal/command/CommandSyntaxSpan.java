package moddedmite.rustedironcore.internal.command;

public record CommandSyntaxSpan(int start, int end, CommandSyntaxKind kind, int styleIndex) {

    public CommandSyntaxSpan(int start, int end, CommandSyntaxKind kind) {
        this(start, end, kind, 0);
    }
}
