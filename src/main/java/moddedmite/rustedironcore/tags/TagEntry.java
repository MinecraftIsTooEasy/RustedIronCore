package moddedmite.rustedironcore.tags;

import moddedmite.rustedironcore.resources.Identifier;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class TagEntry {
    private final Identifier id;
    private final boolean tag;
    private final boolean required;

    private TagEntry(final Identifier id, final boolean tag, final boolean required) {
        this.id = id;
        this.tag = tag;
        this.required = required;
    }

    public static TagEntry element(final Identifier id) {
        return new TagEntry(id, false, true);
    }

    public static TagEntry optionalElement(final Identifier id) {
        return new TagEntry(id, false, false);
    }

    public static TagEntry tag(final Identifier id) {
        return new TagEntry(id, true, true);
    }

    public static TagEntry optionalTag(final Identifier id) {
        return new TagEntry(id, true, false);
    }

    public <T> boolean build(final TagEntry.Lookup<T> lookup, final Consumer<T> output) {
        if (this.tag) {
            Collection<T> result = lookup.tag(this.id);
            if (result == null) {
                return !this.required;
            }

            result.forEach(output);
        } else {
            T result = lookup.element(this.id, this.required);
            if (result == null) {
                return !this.required;
            }

            output.accept(result);
        }

        return true;
    }

    public void visitRequiredDependencies(final Consumer<Identifier> output) {
        if (this.tag && this.required) {
            output.accept(this.id);
        }
    }

    public void visitOptionalDependencies(final Consumer<Identifier> output) {
        if (this.tag && !this.required) {
            output.accept(this.id);
        }
    }

    public boolean verifyIfPresent(final Predicate<Identifier> elementCheck, final Predicate<Identifier> tagCheck) {
        return !this.required || (this.tag ? tagCheck : elementCheck).test(this.id);
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        if (this.tag) {
            result.append('#');
        }

        result.append(this.id);
        if (!this.required) {
            result.append('?');
        }

        return result.toString();
    }

    public interface Lookup<T> {
        @Nullable T element(Identifier key, boolean required);

        @Nullable Collection<T> tag(Identifier key);
    }
}
