package huix.glacier.registry.util;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import huix.glacier.api.registry.sync.compat.IdListCompat;
import huix.glacier.api.registry.sync.compat.SimpleRegistryCompat;
import huix.glacier.api.registry.util.IdList;
import huix.glacier.api.registry.util.RegistryEventsHolder;
import huix.glacier.util.Identifier;

import java.util.Iterator;
import java.util.Map;

public class SimpleRegistry<K, V> extends MutableRegistry implements ObjectIdIterable<V>, SimpleRegistryCompat<K, V> {
    protected IdList ids = new IdList();
    protected final Map objects;

    public SimpleRegistry() {
        this.objects = ((BiMap)this.map).inverse();
    }

    public void add(int id, Object identifier, Object object) {
        this.ids.set(object, id);
        this.put(identifier, object);
    }

    protected Map createMap() {
        return HashBiMap.create();
    }

    public Object get(Object key) {
        return super.get(key);
    }

    public Object getIdentifier(Object id) {
        return this.objects.get(id);
    }

    public boolean containsKey(Object key) {
        return super.containsKey(key);
    }

    public int getRawId(Object object) {
        return this.ids.getId(object);
    }

    public Object getByRawId(int index) {
        return this.ids.fromId(index);
    }

    public Iterator iterator() {
        return this.ids.iterator();
    }


    private RegistryEventsHolder<V> registryEventsHolder;

    @Override
    public IdListCompat<V> getIds() {
        return (IdListCompat<V>) this.ids;
    }

    @Override
    public Map<V, K> getObjects() {
        return this.objects;
    }

    @Override
    public void setIds(IdListCompat<V> idList) {
        this.ids = (IdList) idList;
    }

    @Override
    public IdListCompat<V> createIdList() {
        return (IdListCompat<V>) new IdList();
    }

    @Override
    public int getRawID(V object) {
        return this.getRawId(object);
    }

    @Override
    public K getKey(V object) {
        return (K) this.getIdentifier(object);
    }

    @Override
    public V getValue(Object key) {
        return (V) this.get(key);
    }

    @Override
    public V register(int i, Object key, V value) {
        this.add(i, this.toKeyType(key), value);
        this.getEventHolder().getAddEvent().invoker().onEntryAdded(i, new Identifier(key), value);
        return value;
    }

    @Override
    public RegistryEventsHolder<V> getEventHolder() {
        return this.registryEventsHolder;
    }

    @Override
    public void setEventHolder(RegistryEventsHolder<V> registryEventsHolder) {
        this.registryEventsHolder = registryEventsHolder;
    }
}

