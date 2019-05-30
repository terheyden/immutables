package com.terheyden;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.SetMultimap;

@SuppressWarnings("unchecked")
@ParametersAreNonnullByDefault
public class ImmutableBuilder
{
    @Nullable private final Multimap<Object, Object> srcMultimap;
    @Nullable private final Map<Object, Object> srcMap;
    @Nullable private final Collection srcList;

    private LinkedList mapKeysToRemove;
    private Multimap mapValuesToRemove;
    private Multimap mapValuesToAdd;

    private LinkedList listValuesToAdd;
    private LinkedList listValuesToRemove;

    boolean sortKeys;
    boolean sortVals;

    ImmutableBuilder()
    {
        this.srcMultimap = null;
        this.srcMap = null;
        this.srcList = null;
    }

    ImmutableBuilder(Multimap<Object, Object> srcMap)
    {
        this.srcMultimap = srcMap;
        this.srcMap = null;
        this.srcList = null;
    }

    ////////////////////////////////////////
    // LIST BUILDER:

    public ImmutableBuilder addListValue(Object val)
    {
        if (listValuesToAdd == null)
        {
            listValuesToAdd = new LinkedList();
        }

        listValuesToAdd.add(val);
        return this;
    }

    public ImmutableBuilder addListValues(Collection vals)
    {
        if (listValuesToAdd == null)
        {
            listValuesToAdd = new LinkedList();
        }

        listValuesToAdd.addAll(vals);
        return this;
    }

    public ImmutableBuilder removeListValue(Object val)
    {
        if (listValuesToRemove == null)
        {
            listValuesToRemove = new LinkedList();
        }

        listValuesToRemove.add(val);
        return this;
    }

    public ImmutableBuilder removeListValues(Collection vals)
    {
        if (listValuesToRemove == null)
        {
            listValuesToRemove = new LinkedList();
        }

        listValuesToRemove.addAll(vals);
        return this;
    }

    // All Collections - Lists, Sets, etc. go to the same builder vars.

    public ImmutableBuilder addSetValue(Object val)
    {
        return addListValue(val);
    }

    public ImmutableBuilder addSetValues(Collection vals)
    {
        return addListValues(vals);
    }

    public ImmutableBuilder removeSetValue(Object val)
    {
        return removeListValue(val);
    }

    public ImmutableBuilder removeSetValues(Collection vals)
    {
        return removeListValues(vals);
    }

    public ImmutableBuilder sortList()
    {
        return sortMapKeys();
    }

    public ImmutableBuilder sortSet()
    {
        return sortMapKeys();
    }

    ////////////////////////////////////////
    // MAP BUILDER:

    public ImmutableBuilder addMapValue(Object key, Object val)
    {
        if (mapValuesToAdd == null)
        {
            mapValuesToAdd = MultimapBuilder.hashKeys().hashSetValues().build();
        }

        mapValuesToAdd.put(key, val);
        return this;
    }

    public ImmutableBuilder removeMapValue(Object key, Object val)
    {
        if (mapValuesToRemove == null)
        {
            mapValuesToRemove = MultimapBuilder.hashKeys().hashSetValues().build();
        }

        mapValuesToRemove.put(key, val);
        return this;
    }

    public ImmutableBuilder removeMapKey(Object key)
    {
        if (mapKeysToRemove == null)
        {
            mapKeysToRemove = new LinkedList();
        }

        mapKeysToRemove.add(key);
        return this;
    }

    // Map and multimap builders are the same:

    public ImmutableBuilder addMultimapValue(Object key, Object val)
    {
        return addMapValue(key, val);
    }

    public ImmutableBuilder removeMultimapValue(Object key, Object val)
    {
        return removeMapValue(key, val);
    }

    public ImmutableBuilder removeMultimapKey(Object key)
    {
        return removeMapKey(key);
    }

    public ImmutableBuilder sortMapKeys()
    {
        sortKeys = true;
        return this;
    }

    ////////////////////////////////////////
    // BUILDING:

    ////////////////////////////////////////
    // TO LISTS:

    public <K> List<K> toList()
    {
        ArrayList newList = new ArrayList();

        addSourceListValues(newList, srcList);
        addNewListValues(newList, listValuesToAdd);
        removeListValues(newList, listValuesToRemove);

        if (sortKeys)
        {
            Collections.sort(newList);
        }

        return newList;
    }

    public <K> List<K> toSortedList()
    {
        sortKeys = true;
        return toList();
    }

    public <K> ImmutableList<K> toImmutableList()
    {
        return ImmutableList.copyOf(toList());
    }

    public <K> ImmutableSortedSet<K> toImmutableSortedSet()
    {
        return ImmutableSortedSet.copyOf(toList());
    }

    ////////////////////////////////////////
    // TO MAPS:

    public <K, V> SetMultimap<K, V> toSetMultimap()
    {
        SetMultimap newMap = sortKeys
            ? MultimapBuilder.treeKeys().hashSetValues().build()
            : MultimapBuilder.hashKeys().hashSetValues().build();

        addSourceMapValues(newMap, srcMultimap, srcMap);
        addNewMapValues(newMap, mapValuesToAdd);
        removeMapValues(newMap, mapValuesToRemove);
        removeMapKeys(newMap, mapKeysToRemove);

        return newMap;
    }

    public <K, V> ImmutableSetMultimap<K, V> toImmutableSetMultimap()
    {
        return ImmutableSetMultimap.<K, V>copyOf(toSetMultimap());
    }

    public <K, V> ListMultimap<K, V> toListMultimap()
    {
        ListMultimap newMap = sortKeys
            ? MultimapBuilder.treeKeys().arrayListValues().build()
            : MultimapBuilder.hashKeys().arrayListValues().build();

        addSourceMapValues(newMap, srcMultimap, srcMap);
        addNewMapValues(newMap, mapValuesToAdd);
        removeMapValues(newMap, mapValuesToRemove);
        removeMapKeys(newMap, mapKeysToRemove);

        return newMap;
    }

    ////////////////////////////////////////
    // MAP MODS:

    public <K, V> ImmutableListMultimap<K, V> toImmutableListMultimap()
    {
        return ImmutableListMultimap.<K, V>copyOf(toListMultimap());
    }

    private static void removeMapKeys(Multimap map, @Nullable LinkedList keysToRemove)
    {
        if (keysToRemove != null)
        {
            keysToRemove.forEach(map::removeAll);
        }
    }

    private static void removeMapValues(Multimap map, @Nullable Multimap valuesToRemove)
    {
        if (valuesToRemove != null)
        {
            valuesToRemove.forEach(map::remove);
        }
    }

    private static void addNewMapValues(Multimap map, @Nullable Multimap valuesToAdd)
    {
        if (valuesToAdd != null)
        {
            valuesToAdd.forEach(map::put);
        }
    }

    private static void addSourceMapValues(
        Multimap destMap,
        @Nullable Multimap srcMultimap,
        @Nullable Map srcMap)
    {
        if (srcMultimap != null)
        {
            destMap.putAll(srcMultimap);
        }

        if (srcMap != null)
        {
            srcMap.forEach(destMap::put);
        }
    }

    ////////////////////////////////////////
    // LIST MODS:

    private static void removeListValues(ArrayList newList, @Nullable LinkedList listValuesToRemove)
    {
        if (listValuesToRemove != null)
        {
            newList.removeAll(listValuesToRemove);
        }
    }

    private static void addNewListValues(ArrayList newList, @Nullable LinkedList listValuesToAdd)
    {
        if (listValuesToAdd != null)
        {
            newList.addAll(listValuesToAdd);
        }
    }

    private static void addSourceListValues(ArrayList newList, @Nullable Collection srcList)
    {
        if (srcList != null)
        {
            newList.addAll(srcList);
        }
    }

}
