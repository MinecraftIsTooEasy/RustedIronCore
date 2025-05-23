package moddedmite.rustedironcore.api.util;

import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.VersionParsingException;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.fabricmc.loader.impl.util.version.SemanticVersionImpl;
import net.xiaoyu233.fml.FishModLoader;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;
import java.util.Optional;

public class FabricUtil {
    private static final Logger LOGGER = LogUtil.getLogger();
    private static final Path GAME_DIR = Path.of("");

    public static Optional<ModContainer> getModContainer(String modid) {
        return FishModLoader.getModContainer(modid);
    }

    public static Optional<ModMetadata> getModMetadata(String modid) {
        return getModContainer(modid).map(ModContainer::getMetadata);
    }

    public static Optional<Version> getModVersion(String modid) {
        return getModMetadata(modid).map(ModMetadata::getVersion);
    }

    // -2 means not loaded, -3 means invalid semantic version
    public static int compareModVersion(String modid, String version) {
        Optional<Version> modVersion = getModVersion(modid);
        if (modVersion.isEmpty()) {
            return -2;
        }
        Version presentVersion = modVersion.get();
        try {
            Version compareTo = new SemanticVersionImpl(version, false);
            return presentVersion.compareTo(compareTo);
        } catch (VersionParsingException e) {
            LOGGER.warn("invalid semantic version {}", version);
            e.printStackTrace();
        }
        return -3;
    }

    public static boolean isModLoaded(String modid) {
        return FishModLoader.hasMod(modid);
    }

    public static boolean isServer() {
        return FishModLoader.isServer();
    }

    public static Path getGameDirectory() {
        return GAME_DIR;
    }

    public static Path getModsDirectory() {
        return GAME_DIR.resolve("mods");
    }

    public static Path getConfigDirectory() {
        return GAME_DIR.resolve("config");
    }

    public static boolean isDevelopmentEnvironment() {
        return FishModLoader.isDevelopmentEnvironment();
    }
}
