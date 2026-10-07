package moddedmite.rustedironcore.api.item;

import huix.glacier.api.extension.material.IArrowMaterial;
import net.minecraft.Material;

/**
 * Register custom arrow materials, which must implement {@link IArrowMaterial}.
 * A registered material is added to the arrow and bow material tables as well as
 * the recovery chance and texture-domain registries.
 * <br>
 * The resource domain is used for entity and unqualified bow pulling textures.
 * Item textures are still handled by item registration.
 */
public interface ArrowRegistry {

    /**
     * Register with the {@code minecraft} resource domain.
     */
    void register(Material material);

    /**
     * Register with the specified texture resource domain.
     */
    void register(Material material, String resourceDomain);
}
