package com.terheyden;

import java.util.Map;
import java.util.stream.Collector;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Ordering;

@ParametersAreNonnullByDefault
public enum ImmutableCollectors
{
    ;

    @Nonnull
    public static <K extends Comparable<?>, V> Collector<Map.Entry<K, V>, ?, ImmutableSortedMap<K, V>> toImmutableSortedMap()
    {
        return ImmutableSortedMap.<Map.Entry<K, V>, K, V>toImmutableSortedMap(
            Ordering.<K>natural(),
            Map.Entry::<K, V>getKey,
            Map.Entry::<K, V>getValue);
    }

    @Nonnull
    public static Collector<Map.Entry<String, User>, ?, ImmutableSortedMap<String, User>> toThing()
    {
        return ImmutableSortedMap.<Map.Entry<String, User>, String, User>toImmutableSortedMap(
            Ordering.<String>natural(),
            Map.Entry::<String, User>getKey,
            Map.Entry::<String, User>getValue);
    }
}
