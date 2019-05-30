package com.terheyden;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSetMultimap;
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
        ImmutableMap<String, User> map5 = Immutables.removeFromMap(map4, "Cora");
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
        ImmutableSortedMap<String, User> map4 = Immutables.removeFromSortedMap(map3, "Cora");
        assertEquals(2, map4.size());

        // Adding to an immutable map:
        ImmutableSortedMap<String, User> map5 = Immutables.addToSortedMap(map4, "Cora", user1);
        assertEquals(3, map5.size());
    }

    @Test
    public void testSetMultimap()
    {
        User user1 = new User("Tashi", 11);
        User user2 = new User("Cora", 8);
        User user3 = new User("Mika", 12);

        // These are not sorted.
        ImmutableSetMultimap<String, User> map1 = ImmutableSetMultimap.of(
            "Tashi", user1,
            "Cora", user2);

        ImmutableSetMultimap<String, User> map2 = Immutables.addToSetMultimap(map1, "Mika", user3);
        assertEquals(3, map2.entries().size());
    }

}
