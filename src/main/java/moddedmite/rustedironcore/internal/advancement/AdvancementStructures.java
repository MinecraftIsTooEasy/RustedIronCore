package moddedmite.rustedironcore.internal.advancement;

/** Native generators expose piece containment without searching for or creating new structures. */
public interface AdvancementStructures {

    boolean ric$containsStructure(String id, int x, int y, int z);
}
