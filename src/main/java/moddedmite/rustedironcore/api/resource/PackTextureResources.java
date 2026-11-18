package moddedmite.rustedironcore.api.resource;

import net.minecraft.MetadataSerializer;
import net.minecraft.Resource;
import net.minecraft.ResourceLocation;
import net.minecraft.ResourceManager;
import net.minecraft.ResourcePack;
import net.minecraft.SimpleResource;

import java.util.List;

/** Addresses a texture in one pack when a higher pack supplies an incompatible UV atlas. */
public final class PackTextureResources {

    private static final String PREFIX = "textures/pack_contract/";

    public static ResourceLocation location(int packIndex, ResourceLocation original) {
        return new ResourceLocation("rustedironcore", PREFIX + packIndex + "/"
                + original.getResourceDomain() + "/" + original.getResourcePath(), false);
    }

    /** Resolved against the current stack, so reloads never retain an old ResourcePack or image. */
    public static Resource open(ResourceManager manager, ResourceLocation location, MetadataSerializer serializer) {
        if (!location.getResourceDomain().equals("rustedironcore") || !location.getResourcePath().startsWith(PREFIX)) return null;
        String[] path = location.getResourcePath().substring(PREFIX.length()).split("/", 3);
        if (path.length != 3) return null;
        int index;
        try { index = Integer.parseInt(path[0]); }
        catch (NumberFormatException exception) { return null; }
        List<ResourcePack> packs = ResourcePackOrder.packs(manager);
        if (index < 0 || index >= packs.size()) return null;
        ResourcePack pack = packs.get(index);
        ResourceLocation original = new ResourceLocation(path[1], path[2], false);
        if (!pack.resourceExists(original)) return null;
        ResourceLocation metadata = new ResourceLocation(path[1], path[2] + ".mcmeta", false);
        return new SimpleResource(location, pack.getInputStream(original),
                pack.resourceExists(metadata) ? pack.getInputStream(metadata) : null, serializer);
    }
}
