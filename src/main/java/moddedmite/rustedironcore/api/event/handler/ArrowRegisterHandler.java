package moddedmite.rustedironcore.api.event.handler;

import huix.glacier.api.extension.material.IArrowMaterial;
import moddedmite.rustedironcore.api.event.AbstractHandler;
import moddedmite.rustedironcore.api.event.listener.IArrowRegisterListener;
import moddedmite.rustedironcore.api.item.ArrowRegistry;
import moddedmite.rustedironcore.property.FloatProperty;
import net.minecraft.Material;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class ArrowRegisterHandler extends AbstractHandler<IArrowRegisterListener> implements ArrowRegistry {

    private static final String DEFAULT_RESOURCE_DOMAIN = "minecraft";

    private final FloatProperty<Material> ArrowRecoveryChance = FloatProperty.of("ArrowRecoveryChance", 0.0F);
    private final Map<Material, String> textureDomains = new HashMap<>();

    private boolean registered;

    public void onRegister() {
        if (this.registered) return;
        this.listeners.forEach(x -> {
            x.onRegister(this.ArrowRecoveryChance);
            x.onRegister(this);
        });
        this.registered = true;
    }

    @Override
    public void register(Material material) {
        this.register(material, DEFAULT_RESOURCE_DOMAIN);
    }

    @Override
    public void register(Material material, String resourceDomain) {
        if (!(material instanceof IArrowMaterial arrowMaterial)) {
            throw new IllegalArgumentException("Arrow material must implement IArrowMaterial: " + material);
        }

        String domain = Objects.requireNonNull(resourceDomain, "resourceDomain").toLowerCase(Locale.ROOT);
        if (!domain.matches("[a-z0-9_.-]+")) {
            throw new IllegalArgumentException("Invalid arrow resource domain: " + resourceDomain);
        }

        this.ArrowRecoveryChance.register(material, arrowMaterial.getChanceOfRecovery());
        this.textureDomains.put(material, domain);
    }

    public Set<Material> keySet() {
        return this.ArrowRecoveryChance.keySet();
    }

    public Optional<Float> match(Material material) {
        return this.ArrowRecoveryChance.getOptional(material);
    }

    public String resolveTexture(Material material, String texture) {
        if (texture.indexOf(':') >= 0) return texture;

        String domain = this.textureDomains.getOrDefault(material, DEFAULT_RESOURCE_DOMAIN);
        return domain.equals(DEFAULT_RESOURCE_DOMAIN) ? texture : domain + ':' + texture;
    }
}
