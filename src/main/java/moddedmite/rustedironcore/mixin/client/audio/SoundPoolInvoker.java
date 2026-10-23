package moddedmite.rustedironcore.mixin.client.audio;

import net.minecraft.SoundPool;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.net.MalformedURLException;
import java.net.URL;

@Mixin(SoundPool.class)
public interface SoundPoolInvoker {

    @Invoker("func_110654_c")
    URL ric$getSoundUrl(String sound) throws MalformedURLException;
}
