package moddedmite.rustedironcore.mixin.client;

import com.google.gson.JsonElement;
import moddedmite.rustedironcore.api.resource.ModernPackMetadata;
import net.minecraft.PackMetadataSectionSerializer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(PackMetadataSectionSerializer.class)
public abstract class PackMetadataSectionSerializerMixin {

    @ModifyVariable(method = "func_110489_a", at = @At("HEAD"), argsOnly = true)
    private JsonElement readModernPackSection(JsonElement json) {
        return ModernPackMetadata.legacySection(json);
    }
}
