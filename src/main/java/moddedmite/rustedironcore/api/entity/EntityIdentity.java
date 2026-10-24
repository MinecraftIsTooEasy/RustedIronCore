package moddedmite.rustedironcore.api.entity;

import net.minecraft.Entity;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * Common compatibility identity for MITE entities.
 *
 * MITE deliberately leaves some client-only entities without
 * {@code entityUniqueID}. Consumers that need a stable cache key may use this
 * value instead of reading that nullable field themselves. A fallback UUID is
 * derived for the live compatibility identity; it is never assigned back to
 * the entity and is never written to NBT.
 */
public final class EntityIdentity {

    public static UUID uuid(Entity entity) {
        if (entity == null) return null;
        UUID persistent = entity.getUniqueIDSilent();
        if (persistent != null) return persistent;
        String key = "rusted_iron_core:entity:" + entity.dimension + ":" + entity.entityId + ":" + entity.getClass().getName();
        return UUID.nameUUIDFromBytes(key.getBytes(StandardCharsets.UTF_8));
    }
}
