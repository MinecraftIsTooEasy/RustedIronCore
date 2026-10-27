package moddedmite.rustedironcore.mixin.accessor;

import net.minecraft.Score;
import net.minecraft.ScoreObjective;
import net.minecraft.Scoreboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(Scoreboard.class)
public interface ScoreboardScoresAccessor {

    @Accessor("field_96544_c")
    Map<String, Map<ScoreObjective, Score>> ric$getScores();
}
