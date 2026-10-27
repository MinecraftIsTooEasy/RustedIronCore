package moddedmite.rustedironcore.mixin.client.audio;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import moddedmite.rustedironcore.api.sound.ResourceSoundPlayer;
import moddedmite.rustedironcore.internal.client.hud.ClientHud;
import net.minecraft.GameSettings;
import net.minecraft.Minecraft;
import net.minecraft.ResourceLocation;
import net.minecraft.SoundManager;
import net.minecraft.SoundPool;
import net.minecraft.SoundPoolEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import paulscode.sound.SoundSystem;

@Mixin(SoundManager.class)
public class SoundManagerMixin {

    @Shadow
    private boolean loaded;

    @Shadow
    private SoundSystem sndSystem;

    @Shadow
    @Final
    private GameSettings options;

    @Shadow
    private int ticksBeforeMusic;

    @Unique
    private static final ResourceLocation ric$bossMusic = new ResourceLocation("ric", "sounds/music/game/end/boss.ogg", false);

    @Unique
    private boolean ric$playingBossMusic;

    @WrapOperation(method = "playSoundFX(Ljava/lang/String;FF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/SoundPool;getRandomSoundFromSoundPool(Ljava/lang/String;)Lnet/minecraft/SoundPoolEntry;"))
    private SoundPoolEntry selectResourceSound(SoundPool pool, String name, Operation<SoundPoolEntry> original) {
        SoundPoolEntry resource = ResourceSoundPlayer.lookup(name);
        return resource == null ? original.call(pool, name) : resource;
    }

    @Inject(method = "playRandomMusicIfReady", at = @At("HEAD"), cancellable = true)
    private void selectBossMusic(CallbackInfo ci) {
        Minecraft client = Minecraft.getMinecraft();
        // Vanilla getSituationalMusic selects END_BOSS only while in the End.
        boolean requested = client.theWorld != null && client.theWorld.provider.dimensionId == 1
                && ClientHud.getBosses().shouldPlayMusic();
        if (!requested) {
            if (ric$playingBossMusic && loaded) {
                sndSystem.stop("BgMusic");
                ticksBeforeMusic = 100;
            }
            ric$playingBossMusic = false;
            return;
        }
        ci.cancel();
        if (!loaded || SoundManager.muted || options.musicVolume == 0) return;
        if (!ric$playingBossMusic || !sndSystem.playing("BgMusic")) {
            SoundPoolEntry entry = ResourceSoundPlayer.resolve(ric$bossMusic);
            if (entry == null) return;
            sndSystem.stop("BgMusic");
            sndSystem.backgroundMusic("BgMusic", entry.getSoundUrl(), entry.getSoundName(), false);
            sndSystem.setVolume("BgMusic", options.musicVolume);
            sndSystem.play("BgMusic");
            ric$playingBossMusic = true;
        }
    }
}
