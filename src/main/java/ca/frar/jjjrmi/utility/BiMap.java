package ca.frar.jjjrmi.utility;
/* http://stackoverflow.com/questions/3430170/how-to-create-a-2-way-map-in-java */

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A mapping from K-&gt;V which also maps V-&gt;K. For each mapping there is a unique
 * reverse mapping. Thus, each key k is unique in K and each value v is unique
 * in V. The BiMap is appropriate when a 1:1 relationship exists between a key
 * (K) and value (V); it permits constant time lookup of values as well as keys.
 *
 * @author edward
 * @param <K>
 * @param <V>
 */
public class BiMap<K extends Object, V extends Object> extends HashMap<K, V> {
    private static final long serialVersionUID = 1L;
    
    /**
     * Backwards map is a map of System.identityHash to the key.
     */
    private Map<Integer, K> backwards = new HashMap<>();

    public K getKey(V v) {
        return backwards.get(System.identityHashCode(v));
    }

    /**
     * Add a k-&gt;v mapping. If either k or v is already mapped, the previous
     * mapping is destroyed. Returns the previous value V if a mapping existed
     * otherwise returns the passed in value v.
     *
     */
    @Override
    public V put(K k, V v) {
        V rvalue = v;
        int id = System.identityHashCode(v);

        if (this.containsKey(k)) {
            rvalue = this.get(k);
            backwards.remove(this.get(k));
            this.remove(k);
        }
        if (backwards.containsKey(id)) {
            this.remove(backwards.get(id));
            backwards.remove(id);
        }

        super.put(k, v);
        backwards.put(id, k);

        return rvalue;
    }

    @Override
    public V remove(Object k) {
        V v = super.remove(k);
        backwards.remove(System.identityHashCode(v));
        return v;
    }

    public void putAll(BiMap<? extends K, ? extends V> that) {
        for (K k : that.keySet()) this.put(k, that.get(k));
    }

     @Override
    public void putAll(Map<? extends K, ? extends V> map) {
        throw new UnsupportedOperationException("This method would delete duplicate values, a dangerous proposition, thus it is not implemented.");
    }

    @Override
    public void clear() {
        super.clear();
        backwards.clear();
    }

    @Override
    public Set<K> keySet() {
        return super.keySet();
    }

    @Override
    public Collection<V> values() {
        return super.values();
    }
}
