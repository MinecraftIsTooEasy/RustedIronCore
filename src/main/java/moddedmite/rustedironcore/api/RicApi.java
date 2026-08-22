package moddedmite.rustedironcore.api;

import moddedmite.rustedironcore.registry.Registries;
import net.minecraft.Entity;
import net.minecraft.EntityList;
import net.minecraft.ResourceLocation;

import javax.annotation.Nullable;

public interface RicApi {
    @Nullable
    static ResourceLocation getEntityType(Class<? extends Entity> clazz) {
        ResourceLocation key = Registries.MOD_ENTITY.getKey(clazz);
        if (key != null) return key;
        String name = EntityList.getStringFromID(EntityList.getEntityID(clazz));
        if (name != null) return new ResourceLocation("minecraft", name);
        return null;
    }
}
