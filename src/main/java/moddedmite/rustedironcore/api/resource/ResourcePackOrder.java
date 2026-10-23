package moddedmite.rustedironcore.api.resource;

import net.minecraft.ResourceLocation;
import net.minecraft.ResourceManager;
import net.minecraft.ResourcePack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

/** The actual reload stack, in vanilla's low-to-high priority order. */
public final class ResourcePackOrder {

    private static final Map<ResourceManager, List<ResourcePack>> PACKS = new WeakHashMap<>();

    public static void clear(ResourceManager manager) {
        PACKS.remove(manager);
    }

    public static void add(ResourceManager manager, ResourcePack pack) {
        PACKS.computeIfAbsent(manager, ignored -> new ArrayList<>()).add(pack);
    }

    /** -1 means absent. This does not open a resource or leak its metadata stream. */
    public static int rank(ResourceManager manager, ResourceLocation location) {
        List<ResourcePack> packs = PACKS.getOrDefault(manager, List.of());
        for (int i = packs.size() - 1; i >= 0; i--) {
            if (packs.get(i).resourceExists(location)) return i;
        }
        return -1;
    }

    /** Highest pack containing the literal path, without any RIC path alias. */
    public static int exactRank(ResourceManager manager, ResourceLocation location) {
        List<ResourcePack> packs = PACKS.getOrDefault(manager, List.of());
        String name = "assets/" + location.getResourceDomain() + "/" + location.getResourcePath();
        for (int i = packs.size() - 1; i >= 0; i--) {
            if (packs.get(i) instanceof ExactResourcePack exact && exact.rustedIronCore$hasExactResource(name)) return i;
        }
        return -1;
    }

    /** Candidates in preference order; the first candidate wins ties within one pack. */
    public static ResourceLocation highest(ResourceManager manager, List<ResourceLocation> candidates) {
        ResourceLocation selected = null;
        int highest = -1;
        for (ResourceLocation candidate : candidates) {
            int rank = rank(manager, candidate);
            if (rank > highest) {
                selected = candidate;
                highest = rank;
            }
        }
        return selected;
    }
}
