package moddedmite.rustedironcore.internal.advancement;

import java.util.Random;

/** A saved Java Random stream with the same transition as MITE's native generator. */
class AdvancementRandomSequence extends Random {

    private static final long MULTIPLIER = 0x5DEECE66DL;
    private static final long MASK = (1L << 48) - 1;
    private long state;
    private final Runnable changed;

    AdvancementRandomSequence(long state, Runnable changed) {
        this.state = state & MASK;
        this.changed = changed;
    }

    long state() {
        return state;
    }

    @Override
    public synchronized void setSeed(long seed) {
        super.setSeed(seed);
        state = (seed ^ MULTIPLIER) & MASK;
        if (changed != null) changed.run();
    }

    @Override
    protected synchronized int next(int bits) {
        state = (state * MULTIPLIER + 0xBL) & MASK;
        changed.run();
        return (int) (state >>> (48 - bits));
    }

    @Override
    public synchronized double nextGaussian() {
        double x, y, square;
        do {
            x = 2 * nextDouble() - 1;
            y = 2 * nextDouble() - 1;
            square = x * x + y * y;
        } while (square >= 1 || square == 0);
        return x * StrictMath.sqrt(-2 * StrictMath.log(square) / square);
    }
}
