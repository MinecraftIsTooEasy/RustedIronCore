package moddedmite.rustedironcore.api.resource;

import net.minecraft.Resource;
import net.minecraft.ResourceLocation;
import net.minecraft.ResourceManager;
import net.minecraft.ResourcePack;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/** Registration aliases for legacy atlas names; source pack files remain unchanged. */
public final class LegacyTextureResources {

    public static List<ResourceLocation> candidates(ResourceLocation original) {
        return LegacyResourcePaths.candidates(original.getResourceDomain(), original.getResourcePath()).stream()
                .map(path -> new ResourceLocation(original.getResourceDomain(), path, false)).toList();
    }

    public static Resource getResource(ResourceManager manager, ResourceLocation original) throws IOException {
        List<ResourceLocation> candidates = candidates(original);
        ResourceLocation selected = ResourcePackOrder.highest(manager, candidates);
        if (selected != null) return read(manager, selected);
        // Resource managers outside the game's reload lifecycle retain ordinary lookup semantics.
        FileNotFoundException missing = new FileNotFoundException(original.toString());
        for (ResourceLocation candidate : candidates) {
            try {
                return read(manager, candidate);
            } catch (IOException failure) {
                missing.addSuppressed(failure);
            }
        }
        throw missing;
    }

    /**
     * Selects a resource that satisfies a model's authored texture contract.
     *
     * Legacy CEM renderers address one texture name while modern packs often
     * move the image into a directory and change its atlas dimensions. A path
     * alias alone cannot make the UV contract correct: this method considers
     * every legacy/modern candidate, reads the image dimensions, and chooses
     * the highest-priority candidate with the model's atlas proportions. HD
     * resolutions are allowed. If a higher pack changes the atlas layout,
     * a pack-specific location keeps the matching lower image addressable.
     *
     * The returned location addresses the selected resource. Callers that
     * render custom geometry should bind this location, rather than keeping the
     * old request as a cache key for a differently sized image.
     */
    public static ResourceLocation findMatching(ResourceManager manager, ResourceLocation original,
                                                int expectedWidth, int expectedHeight) {
        if (manager == null || original == null || expectedWidth <= 0 || expectedHeight <= 0) return null;
        List<ResourceLocation> candidates = LegacyResourcePaths.textureContractCandidates(
                original.getResourceDomain(), original.getResourcePath()).stream()
                .map(path -> new ResourceLocation(original.getResourceDomain(), path, false)).toList();
        List<ResourcePack> packs = ResourcePackOrder.packs(manager);
        for (int rank = packs.size() - 1; rank >= 0; rank--) {
            ResourcePack pack = packs.get(rank);
            for (ResourceLocation candidate : candidates) {
                if (!pack.resourceExists(candidate)) continue;
                try (InputStream stream = pack.getInputStream(candidate)) {
                    if (matches(ImageIO.read(stream), expectedWidth, expectedHeight)) {
                        return rank == ResourcePackOrder.rank(manager, candidate) ? candidate
                                : PackTextureResources.location(rank, candidate);
                    }
                } catch (IOException | RuntimeException ignored) {
                    // Continue through the selected stack when an image is unreadable.
                }
            }
        }
        if (!packs.isEmpty()) return null;
        // Resource managers used by audits and third-party loaders may not have
        // gone through the normal reload hooks that populate ResourcePackOrder.
        // Preserve deterministic candidate order for that case.
        for (ResourceLocation candidate : candidates) {
            if (matches(manager, candidate, expectedWidth, expectedHeight)) return candidate;
        }
        return null;
    }

    private static boolean matches(ResourceManager manager, ResourceLocation location,
                                   int expectedWidth, int expectedHeight) {
        try {
            Resource resource = manager.getResource(location);
            try (InputStream stream = resource.getInputStream()) {
                BufferedImage image = ImageIO.read(stream);
                // CEM UVs are normalized using textureSize. HD copies preserve
                // that layout at another resolution; absolute size is not a mismatch.
                return matches(image, expectedWidth, expectedHeight);
            }
        } catch (IOException | RuntimeException ignored) {
            return false;
        }
    }

    private static boolean matches(BufferedImage image, int expectedWidth, int expectedHeight) {
        return image != null && (long) image.getWidth() * expectedHeight
                == (long) image.getHeight() * expectedWidth;
    }

    private static Resource read(ResourceManager manager, ResourceLocation location) throws IOException {
        try {
            return manager.getResource(location);
        } catch (Exception failure) {
            if (failure instanceof IOException io) throw io;
            throw new IOException("Unable to read " + location, failure);
        }
    }
}
