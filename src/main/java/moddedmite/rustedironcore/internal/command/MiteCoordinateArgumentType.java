package moddedmite.rustedironcore.internal.command;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

import java.util.List;

final class MiteCoordinateArgumentType implements ArgumentType<MiteCoordinate> {

    private static final SimpleCommandExceptionType INVALID_COORDINATE = new SimpleCommandExceptionType(new LiteralMessage("Invalid coordinate"));

    static MiteCoordinateArgumentType coordinate() {
        return new MiteCoordinateArgumentType();
    }

    @Override
    public MiteCoordinate parse(StringReader reader) throws CommandSyntaxException {
        int start = reader.getCursor();
        while (reader.canRead() && !Character.isWhitespace(reader.peek())) reader.skip();
        String token = reader.getString().substring(start, reader.getCursor());
        boolean relative = token.startsWith("~");
        String number = relative ? token.substring(1) : token;
        if (relative && number.isEmpty()) return new MiteCoordinate(true, 0.0D);
        if (number.isEmpty()) {
            reader.setCursor(start);
            throw INVALID_COORDINATE.createWithContext(reader);
        }

        double value;
        try {
            value = Double.parseDouble(number);
        } catch (NumberFormatException exception) {
            reader.setCursor(start);
            throw INVALID_COORDINATE.createWithContext(reader);
        }
        if (!Double.isFinite(value)) {
            reader.setCursor(start);
            throw INVALID_COORDINATE.createWithContext(reader);
        }
        return new MiteCoordinate(relative, value);
    }

    @Override
    public List<String> getExamples() {
        return List.of("0", "~", "~1.5");
    }
}
