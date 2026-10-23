package moddedmite.rustedironcore.api.model;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import huix.glacier.util.Identifier;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/** Standard modern item selection tree; renderer state is supplied separately from resource parsing. */
public final class JsonItemDefinition {

    public interface Properties {

        String selected(String property, JsonObject options);

        boolean condition(String property, JsonObject options);

        float number(String property, JsonObject options);
    }

    public record Selection(Identifier model, List<JsonObject> tints) {}

    private interface Node {

        Selection resolve(Properties properties);

        void collect(Set<Identifier> models);
    }

    private final Node root;
    private final Set<Identifier> models;

    private JsonItemDefinition(Node root) {
        this.root = root;
        Set<Identifier> ids = new LinkedHashSet<>();
        root.collect(ids);
        models = Set.copyOf(ids);
    }

    public static JsonItemDefinition read(JsonObject json) {
        return new JsonItemDefinition(node(json.getAsJsonObject("model")));
    }

    public Selection resolve(Properties properties) { return root.resolve(properties); }

    public Set<Identifier> modelIds() { return models; }

    private static Node node(JsonObject json) {
        if (json == null || !json.has("type")) throw new IllegalArgumentException("Item definition has no model type");
        String type = path(json.get("type").getAsString());
        return switch (type) {
            case "model" -> {
                Identifier model = new Identifier(json.get("model").getAsString());
                List<JsonObject> tints = new ArrayList<>();
                if (json.has("tints")) for (JsonElement value : json.getAsJsonArray("tints")) tints.add(value.getAsJsonObject());
                yield new Leaf(new Selection(model, List.copyOf(tints)));
            }
            case "condition" -> new Conditional(json, node(json.getAsJsonObject("on_true")), node(json.getAsJsonObject("on_false")));
            case "select" -> {
                List<Case> cases = new ArrayList<>();
                for (JsonElement value : json.getAsJsonArray("cases")) {
                    JsonObject selected = value.getAsJsonObject();
                    Set<String> names = new LinkedHashSet<>();
                    JsonElement when = selected.get("when");
                    if (when.isJsonArray()) for (JsonElement name : when.getAsJsonArray()) names.add(name.getAsString());
                    else names.add(when.getAsString());
                    cases.add(new Case(Set.copyOf(names), node(selected.getAsJsonObject("model"))));
                }
                yield new Select(json, List.copyOf(cases), fallback(json));
            }
            case "range_dispatch" -> {
                List<Range> ranges = new ArrayList<>();
                for (JsonElement value : json.getAsJsonArray("entries")) {
                    JsonObject entry = value.getAsJsonObject();
                    ranges.add(new Range(entry.get("threshold").getAsFloat(), node(entry.getAsJsonObject("model"))));
                }
                ranges.sort(java.util.Comparator.comparingDouble(Range::threshold));
                yield new Dispatch(json, List.copyOf(ranges), fallback(json));
            }
            default -> throw new IllegalArgumentException("Unsupported modern item model type: " + json.get("type"));
        };
    }

    private static Node fallback(JsonObject json) {
        return json.has("fallback") ? node(json.getAsJsonObject("fallback")) : new Leaf(null);
    }

    private static String path(String id) { return id.indexOf(':') < 0 ? id : id.substring(id.indexOf(':') + 1); }

    private record Leaf(Selection selection) implements Node {

        @Override
        public Selection resolve(Properties properties) { return selection; }

        @Override
        public void collect(Set<Identifier> models) { if (selection != null) models.add(selection.model); }
    }

    private record Conditional(JsonObject options, Node yes, Node no) implements Node {

        @Override
        public Selection resolve(Properties properties) {
            return (properties.condition(options.get("property").getAsString(), options) ? yes : no).resolve(properties);
        }

        @Override
        public void collect(Set<Identifier> models) { yes.collect(models); no.collect(models); }
    }

    private record Case(Set<String> names, Node model) {}

    private record Select(JsonObject options, List<Case> cases, Node fallback) implements Node {

        @Override
        public Selection resolve(Properties properties) {
            String value = properties.selected(options.get("property").getAsString(), options);
            for (Case entry : cases) if (entry.names.contains(value)) return entry.model.resolve(properties);
            return fallback.resolve(properties);
        }

        @Override
        public void collect(Set<Identifier> models) { for (Case entry : cases) entry.model.collect(models); fallback.collect(models); }
    }

    private record Range(float threshold, Node model) {}

    private record Dispatch(JsonObject options, List<Range> ranges, Node fallback) implements Node {

        @Override
        public Selection resolve(Properties properties) {
            float value = properties.number(options.get("property").getAsString(), options)
                    * (options.has("scale") ? options.get("scale").getAsFloat() : 1);
            if (!Float.isNaN(value)) for (int i = ranges.size() - 1; i >= 0; i--)
                if (value >= ranges.get(i).threshold) return ranges.get(i).model.resolve(properties);
            return fallback.resolve(properties);
        }

        @Override
        public void collect(Set<Identifier> models) { for (Range entry : ranges) entry.model.collect(models); fallback.collect(models); }
    }
}
