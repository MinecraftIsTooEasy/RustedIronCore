package moddedmite.rustedironcore.data;

import java.util.concurrent.CompletableFuture;

public interface DataProvider {
    CompletableFuture<?> run(CachedOutput cache);
}
