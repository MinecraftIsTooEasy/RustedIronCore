package moddedmite.rustedironcore.internal.client.advancement;

import moddedmite.rustedironcore.api.resource.LegacyTextureResources;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ResourceLocation;
import net.minecraft.ResourceManager;

import java.util.Map;

@Environment(EnvType.CLIENT)
public class AdvancementBackgrounds {

    private static final Map<String, String> LEGACY_NAMES = Map.of(
            "bricks", "brick", "stone_bricks", "stonebricksmooth", "nether_bricks", "netherBrick",
            "oak_log", "log_oak", "oak_planks", "planks_oak");

    public static ResourceLocation resolve(ResourceManager resources, ResourceLocation requested) {
        ResourceLocation texture = LegacyTextureResources.findMatching(resources, requested, 16, 16);
        if (texture != null) return texture;
        if (requested.getResourceDomain().equals("minecraft")) {
            String path = requested.getResourcePath();
            String name = path.substring(path.lastIndexOf('/') + 1).replace(".png", "");
            texture = LegacyTextureResources.findMatching(resources,
                    new ResourceLocation("minecraft", "textures/blocks/" + LEGACY_NAMES.getOrDefault(name, name) + ".png"), 16, 16);
        }
        return texture == null ? new ResourceLocation("minecraft", "textures/blocks/stone.png") : texture;
    }
}
