package moddedmite.rustedironcore.internal.client.texture;

import moddedmite.rustedironcore.api.resource.LegacyTextureResources;
import net.minecraft.Resource;
import net.minecraft.ResourceLocation;
import net.minecraft.ResourceManager;

import java.io.IOException;
import java.util.List;
import java.util.Set;

/** A resource manager whose {@link #getResource} resolves legacy atlas names like {@link LegacyTextureResources#getResource}. */
public final class LegacyAliasResourceManager implements ResourceManager {
    private final ResourceManager delegate;

    public LegacyAliasResourceManager(ResourceManager delegate) {
        this.delegate = delegate;
    }

    @Override
    public Set getResourceDomains() {
        return this.delegate.getResourceDomains();
    }

    @Override
    public Resource getResource(ResourceLocation location) {
        try {
            return LegacyTextureResources.getResource(this.delegate, location);
        } catch (IOException e) {
            throw LegacyAliasResourceManager.<RuntimeException>sneakyThrow(e);
        }
    }

    @Override
    public List getAllResources(ResourceLocation location) {
        return this.delegate.getAllResources(location);
    }

    /** MITE's {@code getResource} throws {@link IOException} without declaring it; callers catch it, so it is passed on unchanged. */
    @SuppressWarnings("unchecked")
    private static <T extends Throwable> T sneakyThrow(Throwable t) throws T {
        throw (T) t;
    }
}
