package moddedmite.rustedironcore.api.interfaces;

import moddedmite.rustedironcore.api.event.events.CraftingRecipeRegisterEvent;

import javax.annotation.Nullable;
import java.util.List;

public interface IRecipeExtend {
    void ric$SetAllowDamaged(boolean b);

    boolean ric$AllowDamaged();

    void ric$SetConsumeRules(List<CraftingRecipeRegisterEvent.ConsumeRule> list);

    @Nullable
    List<CraftingRecipeRegisterEvent.ConsumeRule> ric$GetConsumeRules();

    void ric$SetKeepQuality();
}
