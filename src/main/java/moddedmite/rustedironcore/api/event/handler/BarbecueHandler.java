package moddedmite.rustedironcore.api.event.handler;

import moddedmite.rustedironcore.api.event.AbstractHandler;
import moddedmite.rustedironcore.api.event.listener.IBarbecueListener;
import net.minecraft.ItemStack;

import java.util.Objects;
import java.util.Optional;

public class BarbecueHandler extends AbstractHandler<IBarbecueListener> {

    public Optional<ItemStack> getCookResult(ItemStack input) {
        return this.listeners.stream().map(x -> x.getCookResult(input))
                .filter(Objects::nonNull)
                .findFirst();
    }


    public boolean isCookResult(ItemStack itemStack) {
        return this.listeners.stream().anyMatch(x -> x.isCookResult(itemStack));
    }
}
