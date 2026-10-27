package moddedmite.rustedironcore.internal.chat;

import net.minecraft.NBTBase;
import net.minecraft.NBTTagByte;
import net.minecraft.NBTTagByteArray;
import net.minecraft.NBTTagCompound;
import net.minecraft.NBTTagDouble;
import net.minecraft.NBTTagFloat;
import net.minecraft.NBTTagInt;
import net.minecraft.NBTTagIntArray;
import net.minecraft.NBTTagList;
import net.minecraft.NBTTagLong;
import net.minecraft.NBTTagShort;
import net.minecraft.NBTTagString;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class NbtPathEvaluator {

    public static List<NBTBase> get(NBTBase root, String path) {
        try {
            List<NBTBase> current = List.of(root);
            for (PathNode node : new PathParser(path).parse()) {
                List<NBTBase> next = new ArrayList<>();
                for (NBTBase value : current) {
                    node.select(value, next);
                }
                current = next;
                if (current.isEmpty()) break;
            }
            return current;
        } catch (IllegalArgumentException exception) {
            return List.of();
        }
    }

    public static String toSnbt(NBTBase tag) {
        if (tag instanceof NBTTagCompound compound) {
            StringBuilder output = new StringBuilder("{");
            boolean first = true;
            for (Object value : compound.getTags()) {
                NBTBase child = (NBTBase) value;
                if (!first) output.append(',');
                output.append(quoteKey(child.getName())).append(':').append(toSnbt(child));
                first = false;
            }
            return output.append('}').toString();
        }
        if (tag instanceof NBTTagList list) {
            StringBuilder output = new StringBuilder("[");
            for (int i = 0; i < list.tagCount(); i++) {
                if (i > 0) output.append(',');
                output.append(toSnbt(list.tagAt(i)));
            }
            return output.append(']').toString();
        }
        if (tag instanceof NBTTagByteArray array) {
            StringBuilder output = new StringBuilder("[B;");
            for (int i = 0; i < array.byteArray.length; i++) {
                if (i > 0) output.append(',');
                output.append(array.byteArray[i]).append('B');
            }
            return output.append(']').toString();
        }
        if (tag instanceof NBTTagIntArray array) {
            StringBuilder output = new StringBuilder("[I;");
            for (int i = 0; i < array.intArray.length; i++) {
                if (i > 0) output.append(',');
                output.append(array.intArray[i]);
            }
            return output.append(']').toString();
        }
        if (tag instanceof NBTTagString string) return quote(string.data);
        if (tag instanceof NBTTagByte value) return value.data + "b";
        if (tag instanceof NBTTagShort value) return value.data + "s";
        if (tag instanceof NBTTagLong value) return value.data + "L";
        if (tag instanceof NBTTagFloat value) return value.data + "f";
        if (tag instanceof NBTTagDouble value) return value.data + "d";
        if (tag instanceof NBTTagInt value) return Integer.toString(value.data);
        return tag.toString();
    }

    public static String getAsString(NBTBase tag) {
        return tag instanceof NBTTagString string ? string.data : toSnbt(tag);
    }

    private static boolean matches(Object pattern, NBTBase actual) {
        return matchesValue(pattern, toValue(actual));
    }

    private static boolean matchesValue(Object pattern, Object value) {
        if (pattern instanceof Map<?, ?> patternMap && value instanceof Map<?, ?> valueMap) {
            for (Map.Entry<?, ?> entry : patternMap.entrySet()) {
                if (!valueMap.containsKey(entry.getKey()) || !matchesValue(entry.getValue(), valueMap.get(entry.getKey()))) {
                    return false;
                }
            }
            return true;
        }
        if (pattern instanceof List<?> patternList && value instanceof List<?> valueList) {
            if (patternList.isEmpty()) return valueList.isEmpty();
            for (Object expected : patternList) {
                boolean found = false;
                for (Object candidate : valueList) {
                    if (matchesValue(expected, candidate)) {
                        found = true;
                        break;
                    }
                }
                if (!found) return false;
            }
            return true;
        }
        return valueEquals(pattern, value);
    }

    private static boolean valueEquals(Object first, Object second) {
        if (first instanceof Map<?, ?> firstMap && second instanceof Map<?, ?> secondMap) {
            if (firstMap.size() != secondMap.size()) return false;
            for (Map.Entry<?, ?> entry : firstMap.entrySet()) {
                if (!secondMap.containsKey(entry.getKey()) || !valueEquals(entry.getValue(), secondMap.get(entry.getKey()))) return false;
            }
            return true;
        }
        return first == null ? second == null : first.equals(second);
    }

    private static Object toValue(NBTBase tag) {
        if (tag instanceof NBTTagCompound compound) {
            Map<String, Object> values = new LinkedHashMap<>();
            for (Object value : compound.getTags()) {
                NBTBase child = (NBTBase) value;
                values.put(child.getName(), toValue(child));
            }
            return values;
        }
        if (tag instanceof NBTTagList list) {
            List<Object> values = new ArrayList<>(list.tagCount());
            for (int i = 0; i < list.tagCount(); i++) values.add(toValue(list.tagAt(i)));
            return values;
        }
        if (tag instanceof NBTTagByteArray array) return new TypedArray('B', byteArrayValues(array.byteArray));
        if (tag instanceof NBTTagIntArray array) return new TypedArray('I', intArrayValues(array.intArray));
        if (tag instanceof NBTTagString value) return value.data;
        if (tag instanceof NBTTagByte value) return new NumberValue('b', value.data);
        if (tag instanceof NBTTagShort value) return new NumberValue('s', value.data);
        if (tag instanceof NBTTagInt value) return new NumberValue('i', value.data);
        if (tag instanceof NBTTagLong value) return new NumberValue('l', value.data);
        if (tag instanceof NBTTagFloat value) return new NumberValue('f', value.data);
        if (tag instanceof NBTTagDouble value) return new NumberValue('d', value.data);
        return tag.toString();
    }

    private static List<Object> byteArrayValues(byte[] values) {
        List<Object> result = new ArrayList<>(values.length);
        for (byte value : values) result.add(new NumberValue('b', value));
        return result;
    }

    private static List<Object> intArrayValues(int[] values) {
        List<Object> result = new ArrayList<>(values.length);
        for (int value : values) result.add(new NumberValue('i', value));
        return result;
    }

    private static String quoteKey(String value) {
        return value.matches("[A-Za-z0-9._+-]+") ? value : quote(value);
    }

    private static String quote(String value) {
        return "\"" + value.replace("\\", "\\\\").replace("\"", "\\\"")
                .replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t") + "\"";
    }

    private interface PathNode {
        void select(NBTBase parent, List<NBTBase> output);
    }

    private static final class PathParser {
        private final String input;
        private int index;

        private PathParser(String input) {
            this.input = input;
        }

        private List<PathNode> parse() {
            List<PathNode> nodes = new ArrayList<>();
            boolean first = true;
            boolean expectKey = true;
            while (this.index < this.input.length()) {
                char current = this.input.charAt(this.index);
                if (current == '.') {
                    if (expectKey) throw new IllegalArgumentException("Unexpected path separator");
                    this.index++;
                    if (this.index == this.input.length()) throw new IllegalArgumentException("Trailing path separator");
                    expectKey = true;
                    continue;
                }
                if (!expectKey && current != '[' && current != '{') {
                    throw new IllegalArgumentException("Missing path separator");
                }
                if (current == '[') {
                    nodes.add(this.parseIndexOrListNode());
                } else if (current == '{' && first) {
                    Object pattern = this.parseCompound();
                    nodes.add((parent, output) -> {
                        if (parent instanceof NBTTagCompound && matches(pattern, parent)) output.add(parent);
                    });
                } else {
                    String key = this.parseKey();
                    if (key.isEmpty()) throw new IllegalArgumentException("Empty path key");
                    if (this.index < this.input.length() && this.input.charAt(this.index) == '{') {
                        Object pattern = this.parseCompound();
                        nodes.add((parent, output) -> {
                            if (parent instanceof NBTTagCompound compound) {
                                NBTBase child = compound.getTag(key);
                                if (child != null && matches(pattern, child)) output.add(child);
                            }
                        });
                    } else {
                        nodes.add((parent, output) -> {
                            if (parent instanceof NBTTagCompound compound) {
                                NBTBase child = compound.getTag(key);
                                if (child != null) output.add(child);
                            }
                        });
                    }
                }
                first = false;
                expectKey = false;
            }
            return nodes;
        }

        private PathNode parseIndexOrListNode() {
            this.index++;
            if (this.index >= this.input.length()) throw new IllegalArgumentException("Unclosed list path");
            if (this.input.charAt(this.index) == ']') {
                this.index++;
                return (parent, output) -> {
                    if (parent instanceof NBTTagList list) {
                        for (int i = 0; i < list.tagCount(); i++) output.add(list.tagAt(i));
                    } else if (parent instanceof NBTTagByteArray array) {
                        for (byte value : array.byteArray) output.add(new NBTTagByte("", value));
                    } else if (parent instanceof NBTTagIntArray array) {
                        for (int value : array.intArray) output.add(new NBTTagInt("", value));
                    }
                };
            }
            if (this.input.charAt(this.index) == '{') {
                Object pattern = this.parseCompound();
                this.expect(']');
                return (parent, output) -> {
                    if (parent instanceof NBTTagList list) {
                        for (int i = 0; i < list.tagCount(); i++) {
                            NBTBase child = list.tagAt(i);
                            if (matches(pattern, child)) output.add(child);
                        }
                    }
                };
            }
            int start = this.index;
            while (this.index < this.input.length() && this.input.charAt(this.index) != ']') this.index++;
            if (this.index == this.input.length()) throw new IllegalArgumentException("Unclosed list index");
            int elementIndex = Integer.parseInt(this.input.substring(start, this.index).trim());
            this.index++;
            return (parent, output) -> {
                if (parent instanceof NBTTagList list) {
                    int resolvedIndex = elementIndex < 0 ? list.tagCount() + elementIndex : elementIndex;
                    if (resolvedIndex >= 0 && resolvedIndex < list.tagCount()) output.add(list.tagAt(resolvedIndex));
                } else if (parent instanceof NBTTagByteArray array) {
                    int resolvedIndex = elementIndex < 0 ? array.byteArray.length + elementIndex : elementIndex;
                    if (resolvedIndex >= 0 && resolvedIndex < array.byteArray.length) output.add(new NBTTagByte("", array.byteArray[resolvedIndex]));
                } else if (parent instanceof NBTTagIntArray array) {
                    int resolvedIndex = elementIndex < 0 ? array.intArray.length + elementIndex : elementIndex;
                    if (resolvedIndex >= 0 && resolvedIndex < array.intArray.length) output.add(new NBTTagInt("", array.intArray[resolvedIndex]));
                }
            };
        }

        private String parseKey() {
            if (this.index >= this.input.length()) return "";
            char current = this.input.charAt(this.index);
            if (current == '\'' || current == '"') return this.parseQuoted();
            int start = this.index;
            while (this.index < this.input.length()) {
                current = this.input.charAt(this.index);
                if (current == '.' || current == '[' || current == ']' || current == '{' || current == '}' || Character.isWhitespace(current)) break;
                this.index++;
            }
            return this.input.substring(start, this.index);
        }

        private String parseQuoted() {
            char quote = this.input.charAt(this.index++);
            StringBuilder value = new StringBuilder();
            boolean escaped = false;
            while (this.index < this.input.length()) {
                char current = this.input.charAt(this.index++);
                if (escaped) {
                    value.append(switch (current) {
                        case 'n' -> '\n';
                        case 'r' -> '\r';
                        case 't' -> '\t';
                        default -> current;
                    });
                    escaped = false;
                } else if (current == '\\') {
                    escaped = true;
                } else if (current == quote) {
                    return value.toString();
                } else {
                    value.append(current);
                }
            }
            throw new IllegalArgumentException("Unclosed quoted value");
        }

        private Object parseCompound() {
            int start = this.index;
            int depth = 0;
            char quote = 0;
            boolean escaped = false;
            do {
                char current = this.input.charAt(this.index++);
                if (quote != 0) {
                    if (escaped) escaped = false;
                    else if (current == '\\') escaped = true;
                    else if (current == quote) quote = 0;
                } else if (current == '\'' || current == '"') {
                    quote = current;
                } else if (current == '{') {
                    depth++;
                } else if (current == '}') {
                    depth--;
                }
            } while (this.index < this.input.length() && depth > 0);
            if (depth != 0 || quote != 0) throw new IllegalArgumentException("Unclosed compound matcher");
            Object result = new SnbtParser(this.input.substring(start, this.index)).parse();
            if (!(result instanceof Map<?, ?>)) throw new IllegalArgumentException("Expected compound matcher");
            return result;
        }

        private void expect(char expected) {
            if (this.index >= this.input.length() || this.input.charAt(this.index) != expected) {
                throw new IllegalArgumentException("Expected '" + expected + "'");
            }
            this.index++;
        }
    }

    private static final class SnbtParser {
        private final String input;
        private int index;

        private SnbtParser(String input) {
            this.input = input;
        }

        private Object parse() {
            Object value = this.parseValue();
            this.skipWhitespace();
            if (this.index != this.input.length()) throw new IllegalArgumentException("Trailing SNBT data");
            return value;
        }

        private Object parseValue() {
            this.skipWhitespace();
            if (this.index >= this.input.length()) throw new IllegalArgumentException("Missing SNBT value");
            return switch (this.input.charAt(this.index)) {
                case '{' -> this.parseCompound();
                case '[' -> this.parseListOrArray();
                case '\'', '"' -> this.parseQuoted();
                default -> this.parseAtom();
            };
        }

        private Map<String, Object> parseCompound() {
            Map<String, Object> result = new LinkedHashMap<>();
            this.index++;
            this.skipWhitespace();
            while (this.index < this.input.length() && this.input.charAt(this.index) != '}') {
                String key = this.parseKey();
                this.skipWhitespace();
                this.expect(':');
                result.put(key, this.parseValue());
                this.skipWhitespace();
                if (this.input.charAt(this.index) == '}') break;
                this.expect(',');
                this.skipWhitespace();
            }
            this.expect('}');
            return result;
        }

        private Object parseListOrArray() {
            this.index++;
            this.skipWhitespace();
            if (this.index + 1 < this.input.length()
                    && "BILbil".indexOf(this.input.charAt(this.index)) >= 0
                    && this.input.charAt(this.index + 1) == ';') {
                char kind = Character.toUpperCase(this.input.charAt(this.index));
                this.index += 2;
                List<Object> values = this.parseValues(']');
                return new TypedArray(kind, values);
            }
            return this.parseValues(']');
        }

        private List<Object> parseValues(char closing) {
            List<Object> result = new ArrayList<>();
            this.skipWhitespace();
            while (this.index < this.input.length() && this.input.charAt(this.index) != closing) {
                result.add(this.parseValue());
                this.skipWhitespace();
                if (this.input.charAt(this.index) == closing) break;
                this.expect(',');
                this.skipWhitespace();
            }
            this.expect(closing);
            return result;
        }

        private String parseKey() {
            this.skipWhitespace();
            if (this.index < this.input.length() && (this.input.charAt(this.index) == '\'' || this.input.charAt(this.index) == '"')) {
                return this.parseQuoted();
            }
            int start = this.index;
            while (this.index < this.input.length()) {
                char current = this.input.charAt(this.index);
                if (current == ':' || Character.isWhitespace(current)) break;
                this.index++;
            }
            if (start == this.index) throw new IllegalArgumentException("Missing SNBT key");
            return this.input.substring(start, this.index);
        }

        private String parseQuoted() {
            char quote = this.input.charAt(this.index++);
            StringBuilder result = new StringBuilder();
            boolean escaped = false;
            while (this.index < this.input.length()) {
                char current = this.input.charAt(this.index++);
                if (escaped) {
                    result.append(switch (current) {
                        case 'n' -> '\n';
                        case 'r' -> '\r';
                        case 't' -> '\t';
                        default -> current;
                    });
                    escaped = false;
                } else if (current == '\\') {
                    escaped = true;
                } else if (current == quote) {
                    return result.toString();
                } else {
                    result.append(current);
                }
            }
            throw new IllegalArgumentException("Unclosed SNBT string");
        }

        private Object parseAtom() {
            int start = this.index;
            while (this.index < this.input.length()) {
                char current = this.input.charAt(this.index);
                if (current == ',' || current == ']' || current == '}' || Character.isWhitespace(current)) break;
                this.index++;
            }
            String atom = this.input.substring(start, this.index);
            if (atom.isEmpty()) throw new IllegalArgumentException("Missing SNBT atom");
            if (atom.equalsIgnoreCase("true")) return new NumberValue('b', (byte) 1);
            if (atom.equalsIgnoreCase("false")) return new NumberValue('b', (byte) 0);
            try {
                char suffix = Character.toLowerCase(atom.charAt(atom.length() - 1));
                String number = "bslfd".indexOf(suffix) >= 0 ? atom.substring(0, atom.length() - 1) : atom;
                return switch (suffix) {
                    case 'b' -> new NumberValue('b', Byte.parseByte(number));
                    case 's' -> new NumberValue('s', Short.parseShort(number));
                    case 'l' -> new NumberValue('l', Long.parseLong(number));
                    case 'f' -> new NumberValue('f', Float.parseFloat(number));
                    case 'd' -> new NumberValue('d', Double.parseDouble(number));
                    default -> number.contains(".") || number.contains("e") || number.contains("E")
                            ? new NumberValue('d', Double.parseDouble(number))
                            : new NumberValue('i', Integer.parseInt(number));
                };
            } catch (NumberFormatException ignored) {
                return atom;
            }
        }

        private void skipWhitespace() {
            while (this.index < this.input.length() && Character.isWhitespace(this.input.charAt(this.index))) this.index++;
        }

        private void expect(char expected) {
            this.skipWhitespace();
            if (this.index >= this.input.length() || this.input.charAt(this.index) != expected) {
                throw new IllegalArgumentException("Expected '" + expected + "'");
            }
            this.index++;
        }
    }

    private record NumberValue(char type, Object value) {}

    private record TypedArray(char type, List<Object> values) {}
}
