package huix.glacier.api.registry.util;

import com.google.common.base.Predicates;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import huix.glacier.api.registry.sync.compat.IdListCompat;
import huix.glacier.api.registry.sync.compat.SimpleRegistryCompat;
import huix.glacier.registry.util.ObjectIdIterable;
import org.jetbrains.annotations.Nullable;

import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;

public class IdList<T> implements IdListCompat<T> {
    private IdentityHashMap<T, Integer> idMap = new IdentityHashMap(512);
    private List<T> list = Lists.newArrayList();

    public void set(T value, int index) {
        this.idMap.put(value, index);

        while(this.list.size() <= index) {
            this.list.add(null);
        }

        this.list.set(index, value);
    }

    public int getId(Object value) {
        Integer var2 = (Integer)this.idMap.get(value);
        return var2 == null ? -1 : var2;
    }

    public Object fromId(int index) {
        return index >= 0 && index < this.list.size() ? this.list.get(index) : null;
    }

    public Iterator iterator() {
        return Iterators.filter(this.list.iterator(), Predicates.notNull());
    }

    public boolean hasId(int index) {
        return this.fromId(index) != null;
    }

    @Override
    public <K> IdentityHashMap<T, Integer> getIdMap(SimpleRegistryCompat<K, T> simpleRegistry) {
        return this.idMap;
    }

    @Override
    public List<T> getList() {
        return this.list;
    }

    @Override
    public Object fromInt(int index) {
        return this.fromId(index);
    }

    @Override
    public void setValue(T value, int index) {
        this.set(value, index);
    }

    @Override
    public int getInt(T value) {
        return this.getId(value);
    }

    @Override
    public IdListCompat<T> createIdList() {
        return new IdList<T>();
    }
}
