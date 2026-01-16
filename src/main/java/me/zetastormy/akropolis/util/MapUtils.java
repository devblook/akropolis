package me.zetastormy.akropolis.util;

import java.util.LinkedHashMap;
import java.util.Map;

public class MapUtils {
    private MapUtils() {
        throw new UnsupportedOperationException();
    }

    @SafeVarargs
    public static <K, V> Map<K, V> linkedHashMapOfEntries(Map.Entry<K, V>... entries) {
        final LinkedHashMap<K, V> map = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : entries) {
            map.put(entry.getKey(), entry.getValue());
        }
        return map;
    }
}
