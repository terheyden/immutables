package com.terheyden;

import static com.google.common.base.Predicates.in;
import static com.google.common.base.Predicates.not;
import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.SetMultimap;

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
    @Nonnull
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

    @Nonnull
    public static <K, V> ImmutableMap<K, V> removeFromMap(
        ImmutableMap<? extends K, ? extends V> sourceMap,
        K key)
    {
        ImmutableMap.Builder<K, V> builder = ImmutableMap.<K, V>builder();

        sourceMap.entrySet().stream()
            .filter(entry -> !entry.getKey().equals(key))
            .forEach(entry -> builder.put(entry.getKey(), entry.getValue()));

        return builder.build();
    }

    @Nonnull
    public static <K, V> Map<K, V> toHashMap(ImmutableMap<? extends K, ? extends V> sourceMap)
    {
        return new HashMap<>(sourceMap);
    }

    @Nonnull
    public static <K, V> ImmutableMap<K, V> toImmutableMap(Map<? extends K, ? extends V> sourceMap)
    {
        return ImmutableMap.copyOf(sourceMap);
    }


    ////////////////////////////////////////
    // SORTEDMAP<K, V>

    @Nonnull
    public static <K extends Comparable<?>, V> ImmutableSortedMap<K, V> addToSortedMap(
        ImmutableSortedMap<? extends K, ? extends V> sourceMap,
        K key,
        V val)
    {
        return ImmutableSortedMap.<K, V>naturalOrder()
            .putAll(sourceMap)
            .put(key, val)
            .build();
    }

    @Nonnull
    public static <K extends Comparable<?>, V> ImmutableSortedMap<K, V> removeFromSortedMap(
        ImmutableSortedMap<? extends K, ? extends V> sourceMap,
        K key)
    {
        ImmutableSortedMap.Builder<K, V> builder = ImmutableSortedMap.<K, V>naturalOrder();

        sourceMap.entrySet().stream()
            .filter(entry -> !entry.getKey().equals(key))
            .forEach(entry -> builder.put(entry.getKey(), entry.getValue()));

        return builder.build();
    }

    @Nonnull
    public static <K extends Comparable<?>, V> SortedMap<K, V> toSortedTreeMap(ImmutableSortedMap<? extends K, ? extends V> sourceMap)
    {
        return new TreeMap<K, V>(sourceMap);
    }

    @Nonnull
    public static <K extends Comparable<?>, V> ImmutableSortedMap<K, V> toImmutableSortedMap(
        Map<? extends K, ? extends V> sourceMap)
    {
        return ImmutableSortedMap.copyOf(sourceMap);
    }


    ////////////////////////////////////////
    // SETMULTIMAP<K, V>

    @Nonnull
    public static <K extends Comparable<?>, V> ImmutableSetMultimap<K, V> addToSetMultimap(
        ImmutableSetMultimap<? extends K, ? extends V> sourceMap,
        K newKey,
        V newVal)
    {
        return new ImmutableSetMultimap.Builder<K, V>()
            .putAll(sourceMap)
            .put(newKey, newVal)
            .build();
    }

    @Nonnull
    public static <K extends Comparable<?>, V> ImmutableSetMultimap<K, V> removeFromSetMultimap(
        ImmutableSetMultimap<? extends K, ? extends V> sourceMap,
        MultimapBuilder.SetMultimapBuilder<? super K, ? super V> builder,
        K oldKey,
        V oldVal)
    {
        SetMultimap<? extends K, ? extends V> tmpMap = builder.build(sourceMap);

        tmpMap.remove(oldKey, oldVal);

        return new ImmutableSetMultimap.Builder<K, V>()
            .putAll(tmpMap)
            .build();
    }

    @Nonnull
    public static <K, V> ImmutableSetMultimap<K, V> toImmutableSetMultimap(Multimap<? extends K, ? extends V> sourceMap)
    {
        return ImmutableSetMultimap.copyOf(sourceMap);
    }

    @Nonnull
    public static <K, V> SetMultimap<K, V> toSetMultimap(
        ImmutableSetMultimap<K, V> sourceMap,
        MultimapBuilder.SetMultimapBuilder<K, V> builder)
    {
        return builder.build(sourceMap);
    }

    /**
     * When making a general multimap builder, it must always be
     * of type Comparable and Object. Only when build() gets called
     * does it get cast to the final types.
     */
    @Nonnull
    private static MultimapBuilder.SetMultimapBuilder<Comparable, Object> newMapBuilder()
    {
        return MultimapBuilder.SetMultimapBuilder
            .treeKeys()
            .hashSetValues();
    }

    ////////////////////////////////////////

    @Nonnull
    public static ImmutableBuilder builder()
    {
        return new ImmutableBuilder();
    }

    @Nonnull
    public static ImmutableBuilder from(Multimap srcMap)
    {
        return new ImmutableBuilder(srcMap);
    }
}
