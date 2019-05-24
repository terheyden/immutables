package com.terheyden;

import static com.google.common.base.Predicates.in;
import static com.google.common.base.Predicates.not;
import static java.util.Arrays.asList;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 * Utility methods for working with immutable classes.
 */
@ParametersAreNonnullByDefault
public enum Immutables
{
    ;

    ////////////////////////////////////////
    // LIST<T>

    /**
     * https://stackoverflow.com/questions/12937938/adding-and-removing-items-to-a-guava-immutablelist
     */
    @Nonnull
    @SafeVarargs
    public static <T> ImmutableList<T> addToList(ImmutableList<? extends T> list, T... itemsToAdd)
    {
        return ImmutableList.<T>builder()
            .addAll(list)
            .addAll(asList(itemsToAdd))
            .build();
    }

    /**
     * https://stackoverflow.com/questions/12937938/adding-and-removing-items-to-a-guava-immutablelist
     */
    @Nonnull
    @SafeVarargs
    public static <T> ImmutableList<T> removeFromList(ImmutableList<? extends T> list, T... itemsToRemove)
    {
        return ImmutableList.copyOf(Collections2.filter(
            list,
            not(in(asList(itemsToRemove)))));
    }

    @Nonnull
    public static <T> List<T> toArrayList(ImmutableList<? extends T> immutableList)
    {
        return new ArrayList<>(immutableList);
    }

    @Nonnull
    public static <T> ImmutableList<T> toImmutableList(List<? extends T> list)
    {
        return ImmutableList.copyOf(list);
    }

    ////////////////////////////////////////
    // MAP<K, V>

    /**
     * https://stackoverflow.com/questions/29828829/extending-an-immutablemap-with-additional-or-new-values
     */
    public static <K, V> ImmutableMap<K, V> addToMap(
        ImmutableMap<? extends K, ? extends V> sourceMap,
        K key,
        V val)
    {
        return ImmutableMap.<K, V>builder()
            .putAll(sourceMap)
            .put(key, val)
            .build();
    }

    public static <K, V> ImmutableMap<K, V> removeMapKey(
        ImmutableMap<? extends K, ? extends V> sourceMap,
        K key)
    {
        ImmutableMap.Builder<K, V> builder = ImmutableMap.<K, V>builder();

        sourceMap.entrySet().stream()
            .filter(entry -> !entry.getKey().equals(key))
            .forEach(entry -> builder.put(entry.getKey(), entry.getValue()));

        return builder.build();
    }

    public static <K, V> Map<K, V> toHashMap(ImmutableMap<? extends K, ? extends V> sourceMap)
    {
        return new HashMap<>(sourceMap);
    }

    public static <K, V> ImmutableMap<K, V> toImmutableMap(Map<? extends K, ? extends V> sourceMap)
    {
        return ImmutableMap.copyOf(sourceMap);
    }

    ////////////////////////////////////////
    // MAP<K, LIST<V>>

    /**
     * https://stackoverflow.com/questions/29828829/extending-an-immutablemap-with-additional-or-new-values
     */
    public static <K, V> Map<K, List<V>> toHashMapArrayList(ImmutableMap<? extends K, ? extends ImmutableList<V>> sourceMap)
    {
        return sourceMap.entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> new ArrayList<V>(entry.getValue())));
    }

    public static <K, V> ImmutableMap<K, ImmutableList<V>> toImmutableMapList(Map<? extends K, ? extends List<V>> sourceMap)
    {
        ImmutableMap.Builder<K, ImmutableList<V>> builder = ImmutableMap.builder();

        sourceMap.forEach((key, listVal) -> builder.put(key, ImmutableList.copyOf(listVal)));

        return builder.build();
    }

    /**
     * https://www.baeldung.com/java-stream-append-prepend
     * https://stackoverflow.com/questions/39441096/how-to-put-an-entry-into-a-map
     */
    public static <K, V> ImmutableMap<K, ImmutableList<V>> addToMapList(
        ImmutableMap<K, ImmutableList<V>> sourceMap,
        K key,
        V val)
    {
        // Create or update the (key, val) entry.

        Stream<V> oldItems = Optional.ofNullable(sourceMap.get(key))
            .orElse(ImmutableList.of())
            .stream();

        ImmutableList<V> newList = Stream.concat(oldItems, Stream.of(val))
            .collect(ImmutableList.toImmutableList());

        // Filter out the old (key, val).
        Stream<Map.Entry<K, ImmutableList<V>>> oldMap = sourceMap.entrySet().stream()
            .filter(entry -> !entry.getKey().equals(key));

        return Stream
            .concat(oldMap, Stream.of(new SimpleImmutableEntry<>(key, newList)))
            .collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * https://www.baeldung.com/java-stream-immutable-collection
     */
    public static <K, V> ImmutableMap<K, ImmutableList<V>> removeMapListValue(
        ImmutableMap<K, ImmutableList<V>> sourceMap,
        K key,
        V val)
    {
        if (!sourceMap.containsKey(key) || !sourceMap.get(key).contains(val))
        {
            // Nothing to remove.
            return sourceMap;
        }

        ImmutableMap.Builder<K, ImmutableList<V>> mapBuilder = ImmutableMap.builder();

        // Copy all old map entries, except for (key, val), which we want to change.
        sourceMap.entrySet().stream()
            .filter(entry -> !entry.getKey().equals(key))
            .forEach(mapBuilder::put);

        // Filter and add the (key, val) entry.
        ImmutableList<V> filteredKeyList = sourceMap.get(key).stream()
            .filter(v -> !v.equals(val))
            .collect(ImmutableList.toImmutableList());

        mapBuilder.put(key, filteredKeyList);

        return mapBuilder.build();
    }
}
