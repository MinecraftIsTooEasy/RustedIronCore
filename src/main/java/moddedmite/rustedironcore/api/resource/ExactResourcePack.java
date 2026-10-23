package moddedmite.rustedironcore.api.resource;

/** Access to a resource pack's literal ZIP/directory entry, bypassing RIC aliases. */
public interface ExactResourcePack {

    boolean rustedIronCore$hasExactResource(String resourceName);
}
