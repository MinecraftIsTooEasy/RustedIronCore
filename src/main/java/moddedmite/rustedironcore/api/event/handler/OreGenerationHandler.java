package moddedmite.rustedironcore.api.event.handler;

import moddedmite.rustedironcore.api.event.EventHandler;
import moddedmite.rustedironcore.api.event.events.OreGenerationRegisterEvent;
import moddedmite.rustedironcore.api.world.Dimension;
import net.minecraft.BiomeDecorator;
import net.minecraft.World;
import net.minecraft.WorldGenMinable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class OreGenerationHandler extends EventHandler<OreGenerationRegisterEvent> {
    public final Map<Dimension, List<Setting>> ORE_MAP = new HashMap<>();

    public void onOresGeneration(Context context) {
        World world = context.world;
        Optional<Dimension> optional = ORE_MAP.keySet().stream().filter(x -> x.isOf(world)).findFirst();
        if (optional.isEmpty()) return;
        List<Setting> ores = ORE_MAP.get(optional.get());
        BiomeDecorator biomeDecorator = context.biomeDecorator;
        ores.forEach(x -> x.generate(biomeDecorator));
    }

    public static Context context(BiomeDecorator biomeDecorator, World world) {
        return new Context(biomeDecorator, world);
    }

    public record Context(BiomeDecorator biomeDecorator, World world) {
    }

    public static Setting setting(WorldGenMinable ore, int frequency, boolean increasesWithDepth) {
        return new Setting(ore, frequency, increasesWithDepth);
    }

    public record Setting(WorldGenMinable ore, int frequency, boolean increasesWithDepth) {
        public void generate(BiomeDecorator biomeDecorator) {
            biomeDecorator.genMinable(frequency, ore, increasesWithDepth);
        }
    }
}
