package moddedmite.rustedironcore.api.event.handler;

import huix.glacier.api.extension.material.IArrowMaterial;
import moddedmite.rustedironcore.api.event.AbstractHandler;
import moddedmite.rustedironcore.api.event.listener.IArrowRegisterListener;
import moddedmite.rustedironcore.api.item.ArrowRegistry;
import moddedmite.rustedironcore.internal.item.ArrowMaterialSynchronizer;
import moddedmite.rustedironcore.property.FloatProperty;
import net.minecraft.Material;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    private boolean registering;

    @Override
    public void register(IArrowRegisterListener listener) {
        boolean added = !this.listeners.contains(listener);
        super.register(listener);
        if (added && (this.registered || this.registering)) {
            this.registerListener(listener);
        }
    }

    public void onRegister() {
        if (this.registered || this.registering) return;
        this.registering = true;
        try {
            List<IArrowRegisterListener> listeners = new ArrayList<>(this.listeners);
            for (IArrowRegisterListener listener : listeners) {
                this.registerListener(listener);
            }
            this.registered = true;
        } finally {
            this.registering = false;
        }
    }

    private void registerListener(IArrowRegisterListener listener) {
        listener.onRegister(this.ArrowRecoveryChance);
        listener.onRegister(this);
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
        ArrowMaterialSynchronizer.register(material);
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
