package moddedmite.rustedironcore.internal.advancement;

import com.google.gson.JsonObject;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import moddedmite.rustedironcore.api.util.LogUtil;
import net.minecraft.ResourceLocation;
import net.minecraft.AnvilSaveHandler;
import net.minecraft.World;
import net.minecraft.server.MinecraftServer;
import net.xiaoyu233.fml.FishModLoader;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

public class AdvancementResources {

    private static final List<Path> PACKS = new ArrayList<>();

    /** Additional pack roots, in increasing priority order. */
    public static void registerPack(Path root) {
        Path path = root.toAbsolutePath().normalize();
        if (!PACKS.contains(path)) PACKS.add(path);
    }

    public static Map<ResourceLocation, JsonObject> load() {
        return loadResources("assets/advancements", "data/advancements", "data/advancement");
    }

    public static Map<ResourceLocation, JsonObject> loadResources(String... folders) {
        Map<ResourceLocation, JsonObject> result = new LinkedHashMap<>();
        loadFiles(folders, ".json", (id, path) -> {
            try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
                JsonObject json = new JsonParser().parse(reader).getAsJsonObject();
                JsonObject previous = result.get(id);
                boolean tag = Stream.of(folders).anyMatch(folder -> folder.contains("/tags/"));
                if (tag && previous != null && !AdvancementJson.flag(json, "replace", false)) previous.getAsJsonArray("values").addAll(json.getAsJsonArray("values"));
                else result.put(id, json);
            } catch (IOException | RuntimeException exception) {
                LogUtil.getLogger().warn("Unable to read advancement resource {}: {}", path, exception.getMessage());
            }
        });
        return result;
    }

    public static Map<ResourceLocation, List<String>> loadFunctions() {
        Map<ResourceLocation, List<String>> result = new LinkedHashMap<>();
        loadFiles(new String[]{"data/functions", "data/function"}, ".mcfunction", (id, path) -> {
            try {
                result.put(id, Files.readAllLines(path, StandardCharsets.UTF_8));
            } catch (IOException exception) {
                LogUtil.getLogger().warn("Unable to read advancement function {}", path, exception);
            }
        });
        return result;
    }

    public static Map<ResourceLocation, JsonElement> loadPredicates() {
        Map<ResourceLocation, JsonElement> result = new LinkedHashMap<>();
        loadFiles(new String[]{"data/predicates", "data/predicate"}, ".json", (id, path) -> {
            try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
                result.put(id, new JsonParser().parse(reader));
            } catch (IOException | RuntimeException exception) {
                LogUtil.getLogger().warn("Unable to read advancement predicate {}", path, exception);
            }
        });
        return result;
    }

    private static void loadFiles(String[] folders, String suffix, BiConsumer<ResourceLocation, Path> consumer) {
        FishModLoader.getModsMap().values().stream().sorted((a, b) -> a.getMetadata().getId().compareTo(b.getMetadata().getId()))
                .forEach(mod -> {
                    for (Path root : mod.getRootPaths()) loadRoot(root, folders, suffix, consumer);
                });
        for (Path pack : PACKS) loadPack(pack, folders, suffix, consumer);
        MinecraftServer server = MinecraftServer.getServer();
        World world = server == null ? null : server.worldServerForDimension(0);
        if (world != null && world.getSaveHandler() instanceof AnvilSaveHandler handler) {
            Path directory = handler.getWorldDirectory().toPath().resolve("datapacks");
            if (Files.isDirectory(directory)) try (Stream<Path> packs = Files.list(directory)) {
                packs.sorted().filter(path -> Files.isDirectory(path) || path.toString().endsWith(".zip"))
                        .forEach(pack -> loadPack(pack, folders, suffix, consumer));
            } catch (IOException exception) {
                LogUtil.getLogger().warn("Unable to list advancement data packs in {}", directory, exception);
            }
        }
    }

    private static void loadRoot(Path root, String[] folders, String suffix, BiConsumer<ResourceLocation, Path> consumer) {
        for (String folder : folders) {
            int slash = folder.indexOf('/');
            loadNamespaces(root.resolve(folder.substring(0, slash)), folder.substring(slash + 1), suffix, consumer);
        }
    }

    private static void loadPack(Path pack, String[] folders, String suffix, BiConsumer<ResourceLocation, Path> consumer) {
        if (Files.isDirectory(pack)) {
            loadRoot(pack, folders, suffix, consumer);
        } else if (Files.isRegularFile(pack)) {
            try (FileSystem zip = FileSystems.newFileSystem(pack, (ClassLoader) null)) {
                loadRoot(zip.getPath("/"), folders, suffix, consumer);
            } catch (IOException | RuntimeException exception) {
                LogUtil.getLogger().warn("Unable to read advancement data pack {}", pack, exception);
            }
        }
    }

    private static void loadNamespaces(Path root, String folder, String suffix, BiConsumer<ResourceLocation, Path> consumer) {
        if (!Files.isDirectory(root)) return;
        try (Stream<Path> namespaces = Files.list(root)) {
            namespaces.filter(Files::isDirectory).sorted().forEach(namespace -> {
                Path directory = namespace.resolve(folder);
                if (!Files.isDirectory(directory)) return;
                try (Stream<Path> files = Files.walk(directory)) {
                    files.filter(Files::isRegularFile).filter(path -> path.toString().endsWith(suffix)).sorted().forEach(path -> {
                        String relative = directory.relativize(path).toString().replace('\\', '/');
                        try {
                            ResourceLocation id = AdvancementJson.id(namespace.getFileName() + ":" + relative.substring(0, relative.length() - suffix.length()));
                            consumer.accept(id, path);
                        } catch (RuntimeException exception) {
                            LogUtil.getLogger().warn("Unable to read advancement resource {}: {}", path, exception.getMessage());
                        }
                    });
                } catch (IOException exception) {
                    LogUtil.getLogger().warn("Unable to list advancements in {}", directory, exception);
                }
            });
        } catch (IOException exception) {
            LogUtil.getLogger().warn("Unable to list advancement namespaces in {}", root, exception);
        }
    }
}
