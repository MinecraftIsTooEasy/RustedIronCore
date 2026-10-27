package moddedmite.rustedironcore.internal.command;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

import java.util.List;

final class MiteIntegerArgumentType implements ArgumentType<MiteIntegerValue> {

    private static final SimpleCommandExceptionType INVALID_INTEGER = new SimpleCommandExceptionType(new LiteralMessage("Invalid integer"));

    static MiteIntegerArgumentType integerWithBang() {
        return new MiteIntegerArgumentType();
    }

    @Override
    public MiteIntegerValue parse(StringReader reader) throws CommandSyntaxException {
        int start = reader.getCursor();
        while (reader.canRead() && !Character.isWhitespace(reader.peek())) reader.skip();
        String token = reader.getString().substring(start, reader.getCursor());
        boolean bang = token.endsWith("!");
        String number = bang ? token.substring(0, token.length() - 1) : token;
        try {
            return new MiteIntegerValue(Integer.parseInt(number), bang);
        } catch (NumberFormatException exception) {
            reader.setCursor(start);
            throw INVALID_INTEGER.createWithContext(reader);
        }
    }

    @Override
    public List<String> getExamples() {
        return List.of("0", "16", "16!");
    }
}
