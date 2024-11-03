package huix.glacier.mixin;

import huix.glacier.api.registry.sync.ServerRegistryRemapper;
import huix.glacier.util.GlacierLog;
import net.minecraft.CompressedStreamTools;
import net.minecraft.NBTTagCompound;
import net.minecraft.SaveHandler;
import net.minecraft.WorldInfo;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.*;

@Mixin( SaveHandler.class )
public class SaveHandlerMixin {
    @Unique
    private NBTTagCompound fabric_lastSavedIdMap = null;
    @Unique
    private static final int FABRIC_ID_REGISTRY_BACKUPS = 3;
    @Shadow
    @Final
    private File worldDirectory;

    @Unique
    private boolean fabric_readIdMapFile(File file) throws IOException {
        if (file.exists()) {
            NBTTagCompound nbt;

            try (FileInputStream fileInputStream = new FileInputStream(file)) {
                nbt = CompressedStreamTools.readCompressed(fileInputStream);
            }

            if (nbt != null) {
                ServerRegistryRemapper.getInstance().readAndRemap(nbt);
                return true;
            }
        }

        return false;
    }

    @Unique
    private File fabric_getWorldIdMapFile(int i) {
        return new File(new File(this.worldDirectory, "data"), "fabricRegistry" + ".dat" + (i == 0 ? "" : ("." + i)));
    }

    @Unique
    private void fabric_saveRegistryData() {
        NBTTagCompound newIdMap = ServerRegistryRemapper.getInstance().toNbtCompound();

        if (!newIdMap.equals(this.fabric_lastSavedIdMap)) {
            for (int i = FABRIC_ID_REGISTRY_BACKUPS - 1; i >= 0; i--) {
                File file = fabric_getWorldIdMapFile(i);

                if (file.exists()) {
                    if (i == FABRIC_ID_REGISTRY_BACKUPS - 1) {
                        file.delete();
                    } else {
                        File target = fabric_getWorldIdMapFile(i + 1);
                        file.renameTo(target);
                    }
                }
            }

            try {
                File file = fabric_getWorldIdMapFile(0);
                File parentFile = file.getParentFile();

                if (!parentFile.exists()) {
                    if (!parentFile.mkdirs()) {
                        GlacierLog.warn("[legacy-fabric-registry-sync-api-v1] Could not create directory " + parentFile + "!");
                    }
                }

                FileOutputStream fileOutputStream = new FileOutputStream(file);
                CompressedStreamTools.writeCompressed(newIdMap, fileOutputStream);
                fileOutputStream.close();
            } catch (IOException e) {
                GlacierLog.warn("[legacy-fabric-registry-sync-api-v1] Failed to save registry file!", e);
            }

            fabric_lastSavedIdMap = newIdMap;
        }
    }

    @Inject(method = "saveWorldInfoWithPlayer", at = @At("HEAD"))
    public void saveWorld(WorldInfo par1WorldInfo, NBTTagCompound par2NBTTagCompound, CallbackInfo ci) {
        if (!worldDirectory.exists()) {
            return;
        }

        fabric_saveRegistryData();
    }

    @Inject(method = "loadWorldInfo", at = @At("HEAD"))
    public void readWorldProperties(CallbackInfoReturnable<WorldInfo> cir) {
        // Load
        for (int i = 0; i < FABRIC_ID_REGISTRY_BACKUPS; i++) {
            GlacierLog.trace("[glacier-registry-api] Loading Glacier registry [file " + (i + 1) + "/" + (FABRIC_ID_REGISTRY_BACKUPS + 1) + "]");

            try {
                if (fabric_readIdMapFile(fabric_getWorldIdMapFile(i))) {
                    GlacierLog.info("[glacier-registry-api] Loaded registry data [file " + (i + 1) + "/" + (FABRIC_ID_REGISTRY_BACKUPS + 1) + "]");
                    return;
                }
            } catch (FileNotFoundException e) {
                // pass
            } catch (IOException e) {
                if (i >= FABRIC_ID_REGISTRY_BACKUPS - 1) {
                    throw new RuntimeException(e);
                } else {
                    GlacierLog.warn("Reading registry file failed!", e);
                }
            } catch (RuntimeException e) {
                throw new RuntimeException("Remapping world failed!", e);
            }
        }

        // If not returned (not present), try saving the registry data
        fabric_saveRegistryData();
    }


}
