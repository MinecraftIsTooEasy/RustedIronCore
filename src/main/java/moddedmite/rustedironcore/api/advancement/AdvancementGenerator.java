package moddedmite.rustedironcore.api.advancement;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

/** Writes a provider's JSON to a 1.20 or 1.21 data pack root. */
public class AdvancementGenerator {

    public static void generate(Path output, boolean singularDirectory, AdvancementProvider... providers) throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
        Set<String> ids = new HashSet<>();
        Path root = output.toAbsolutePath().normalize();
        try {
            for (AdvancementProvider provider : providers) provider.generate(advancement -> {
                if (!ids.add(advancement.id().toString())) throw new IllegalArgumentException("Duplicate generated advancement: " + advancement.id());
                Path target = root.resolve("data").resolve(advancement.id().getResourceDomain())
                        .resolve(singularDirectory ? "advancement" : "advancements")
                        .resolve(advancement.id().getResourcePath() + ".json").normalize();
                if (!target.startsWith(root)) throw new IllegalArgumentException("Invalid advancement output path: " + advancement.id());
                try {
                    Files.createDirectories(target.getParent());
                    Files.writeString(target, gson.toJson(advancement.json()) + "\n", StandardCharsets.UTF_8);
                } catch (IOException exception) {
                    throw new UncheckedIOException(exception);
                }
            });
        } catch (UncheckedIOException exception) {
            throw exception.getCause();
        }
    }
}
