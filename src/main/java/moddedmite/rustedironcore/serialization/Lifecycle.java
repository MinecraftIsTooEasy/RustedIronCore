//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package moddedmite.rustedironcore.serialization;

public class Lifecycle {
    private static final Lifecycle STABLE = new Lifecycle() {
        public String toString() {
            return "Stable";
        }
    };
    private static final Lifecycle EXPERIMENTAL = new Lifecycle() {
        public String toString() {
            return "Experimental";
        }
    };

    private Lifecycle() {
    }

    public static Lifecycle experimental() {
        return EXPERIMENTAL;
    }

    public static Lifecycle stable() {
        return STABLE;
    }

    public static Lifecycle deprecated(int since) {
        return new Deprecated(since);
    }

    public Lifecycle add(Lifecycle other) {
        if (this != EXPERIMENTAL && other != EXPERIMENTAL) {
            if (this instanceof Deprecated) {
                return other instanceof Deprecated && ((Deprecated)other).since < ((Deprecated)this).since ? other : this;
            } else {
                return other instanceof Deprecated ? other : STABLE;
            }
        } else {
            return EXPERIMENTAL;
        }
    }

    public static final class Deprecated extends Lifecycle {
        private final int since;

        public Deprecated(int since) {
            this.since = since;
        }

        public int since() {
            return this.since;
        }
    }
}
