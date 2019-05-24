package com.terheyden;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.UnmodifiableIterator;

import org.junit.jupiter.api.Test;

class ImmutablesTest
{
    @Test
    public void testList()
    {
        ImmutableList<String> list1 = ImmutableList.of("Cora");
        ImmutableList<String> list2 = Immutables.addToList(list1, "Mika");
        ImmutableList<String> list3 = Immutables.removeFromList(list2, "Cora", "fake");

        assertEquals(1, list1.size());
        assertEquals(2, list2.size());
        assertTrue(list2.contains("Cora"));
        assertTrue(list2.contains("Mika"));

        assertEquals(1, list3.size());
        assertTrue(list3.contains("Mika"));

        List<String> list4 = Immutables.toArrayList(list2);
        assertEquals(2, list4.size());
        assertTrue(list4.contains("Cora"));
        assertTrue(list4.contains("Mika"));

        list4.add("Tashi");
        ImmutableList<String> list5 = Immutables.toImmutableList(list4);

        assertEquals(3, list5.size());
        assertTrue(list5.contains("Tashi"));
    }

    @Test
    public void testMap()
    {
        User user1 = new User("Cora", 8);
        User user2 = new User("Tashi", 11);
        User user3 = new User("Mika", 12);

        ImmutableMap<String, User> map1 = ImmutableMap.of("Cora", user1);

        // Converting from immutable map to mutable:
        Map<String, User> map2 = Immutables.toHashMap(map1);
        assertEquals(1, map2.size());
        assertEquals(user1, map2.get("Cora"));
        map2.put("Mika", user3);
        assertEquals(2, map2.size());

        // Converting from map to immutable:
        ImmutableMap<String, User> map3 = Immutables.toImmutableMap(map2);
        assertEquals(2, map3.size());

        // Adding to an immutable map:
        ImmutableMap<String, User> map4 = Immutables.addToMap(map3, "Tashi", user2);
        assertEquals(3, map4.size());

        // Removing from an immutable map:
        ImmutableMap<String, User> map5 = Immutables.removeMapKey(map4, "Cora");
        assertEquals(2, map5.size());
    }

    @Test
    public void testSortedMap()
    {
        User user1 = new User("Cora", 8);
        User user2 = new User("Tashi", 11);
        User user3 = new User("Mika", 12);

        ImmutableSortedMap<String, User> map1 = ImmutableSortedMap.of(
            "Tashi", user2,
            "Cora", user1);

        // Verify sorted:
        UnmodifiableIterator<Map.Entry<String, User>> iter1 = map1.entrySet().iterator();
        assertEquals("Cora", iter1.next().getKey());
        assertEquals("Tashi", iter1.next().getKey());

        // Converting from immutable map to mutable:
        SortedMap<String, User> map2 = Immutables.toSortedTreeMap(map1);
        assertEquals(2, map2.size());
        map2.put("Mika", user3);

        // Verify sorted:
        Iterator<Map.Entry<String, User>> iter2 = map2.entrySet().iterator();
        assertEquals("Cora", iter2.next().getKey());
        assertEquals("Mika", iter2.next().getKey());
        assertEquals("Tashi", iter2.next().getKey());

        // Converting from map to immutable:
        ImmutableSortedMap<String, User> map3 = Immutables.toImmutableSortedMap(map2);
        assertEquals(3, map3.size());

        // Removing from an immutable map:
        ImmutableSortedMap<String, User> map4 = Immutables.removeSortedMapKey(map3, "Cora");
        assertEquals(2, map4.size());

        // Adding to an immutable map:
        ImmutableSortedMap<String, User> map5 = Immutables.addToSortedMap(map4, "Cora", user1);
        assertEquals(3, map5.size());
    }

    @Test
    public void testMapList()
    {
        User user1 = new User("Cora", 8);
        User user2 = new User("Tashi", 11);
        User user3 = new User("Mika", 12);

        ImmutableMap<String, ImmutableList<User>> maplist1 = ImmutableMap.of(
            "Mika",
            ImmutableList.of(user3));

        // From immutable to mutable:
        Map<String, List<User>> maplist2 = Immutables.toHashMapArrayList(maplist1);
        maplist2.put("Cora", Collections.singletonList(user1));
        assertEquals(2, maplist2.size());
        assertTrue(maplist2.containsKey("Mika"));
        assertTrue(maplist2.containsKey("Cora"));

        maplist2.put("cats", Arrays.asList(user1, user2));

        // From mutable to immutable:
        ImmutableMap<String, ImmutableList<User>> cats1 = Immutables.toImmutableMapList(maplist2);
        assertEquals(3, cats1.size());
        assertEquals(2, cats1.get("cats").size());

        // Add in-place:
        ImmutableMap<String, ImmutableList<User>> cats2 = Immutables.addToMapList(cats1, "cats", user3);
        assertEquals(3, cats2.size());
        assertEquals(3, cats2.get("cats").size());

        // Remove in-place:
        ImmutableMap<String, ImmutableList<User>> cats3 = Immutables.removeMapListValue(cats2, "cats", user1);
        assertEquals(3, cats3.size());
        assertEquals(2, cats3.get("cats").size());

        // Removing all items from a list just leaves an empty list behind:
        ImmutableMap<String, ImmutableList<User>> cats4 = Immutables.removeMapListValue(cats3, "Mika", user3);
        assertEquals(3, cats4.size());
        assertTrue(cats4.get("Mika").isEmpty());

        // Remove an entire key:
        ImmutableMap<String, ImmutableList<User>> cats5 = Immutables.removeMapKey(cats3, "Mika");
        assertEquals(2, cats5.size());
        assertFalse(cats5.containsKey("Mika"));
    }

    @Test
    public void testSortedMapList()
    {
        User user1 = new User("Cora", 8);
        User user2 = new User("Tashi", 11);
        User user3 = new User("Mika", 12);

        ImmutableSortedMap<String, ImmutableList<User>> maplist1 = ImmutableSortedMap.of(
            "Tashi", ImmutableList.of(user2),
        "Mika", ImmutableList.of(user3));

        // From immutable to mutable:
        Map<String, List<User>> maplist2 = Immutables.toSortedTreeMapArrayList(maplist1);

        assertEquals(2, maplist2.size());

        // Verify sorting:
        Iterator<Map.Entry<String, List<User>>> iter2 = maplist2.entrySet().iterator();
        assertEquals("Mika", iter2.next().getKey());
        assertEquals("Tashi", iter2.next().getKey());

        maplist2.put("cats", Arrays.asList(user1, user2));

        // From mutable to immutable:
        ImmutableSortedMap<String, ImmutableList<User>> cats1 = Immutables.toImmutableSortedMapList(maplist2);
        assertEquals(3, cats1.size());
        assertEquals(2, cats1.get("cats").size());

        // Add in-place:
        ImmutableSortedMap<String, ImmutableList<User>> cats2 = Immutables.addToSortedMapList(cats1, "cats", user3);
        assertEquals(3, cats2.size());
        assertEquals(3, cats2.get("cats").size());

        // Remove in-place:
        ImmutableSortedMap<String, ImmutableList<User>> cats3 = Immutables.removeSortedMapListValue(cats2, "cats", user1);
        assertEquals(3, cats3.size());
        assertEquals(2, cats3.get("cats").size());

        // Removing all items from a list just leaves an empty list behind:
        ImmutableSortedMap<String, ImmutableList<User>> cats4 = Immutables.removeSortedMapListValue(cats3, "Mika", user3);
        assertEquals(3, cats4.size());
        assertTrue(cats4.get("Mika").isEmpty());

        // Verify sorting:
        // Uppercase is naturally higher than lower.
        UnmodifiableIterator<Map.Entry<String, ImmutableList<User>>> iter4 = cats4.entrySet().iterator();
        assertEquals("Mika", iter4.next().getKey());
        assertEquals("Tashi", iter4.next().getKey());
        assertEquals("cats", iter4.next().getKey());

        // Remove an entire key:
        ImmutableSortedMap<String, ImmutableList<User>> cats5 = Immutables.removeSortedMapKey(cats3, "Mika");
        assertEquals(2, cats5.size());
        assertFalse(cats5.containsKey("Mika"));
    }
}
