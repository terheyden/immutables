package com.terheyden;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.Iterator;

import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.UnmodifiableIterator;

import org.junit.jupiter.api.Test;

class ImmutableBuilderTest
{
    @Test
    public void test()
    {
        User user1 = new User("Cora", 8);
        User user2 = new User("Tashi", 11);
        User user3 = new User("Mika", 12);

        ImmutableSetMultimap<String, String> empty = Immutables
            .builder()
            .toImmutableSetMultimap();

        ImmutableSetMultimap<String, User> map2 = Immutables
            .from(empty)
            .addMapValue("Mika", user3)
            .addMapValue("Cora", user1)
            .sortMapKeys()
            .toImmutableSetMultimap();

        // Should be sorted.
        UnmodifiableIterator<String> iter2 = map2.keys().iterator();
        assertEquals("Cora", iter2.next());
        assertEquals("Mika", iter2.next());
        assertFalse(iter2.hasNext());

        SetMultimap<String, User> map3 = Immutables.from(map2).toSetMultimap();
        map3.put("Fgh", user2);

        ImmutableListMultimap<String, User> sortedMap4 = Immutables
            .from(map3)
            .sortMapKeys()
            .toImmutableListMultimap();

        // Should be sorted.
        Iterator<String> iter4 = sortedMap4.keys().iterator();
        assertEquals("Cora", iter4.next());
        assertEquals("Fgh", iter4.next());
        assertEquals("Mika", iter4.next());
        assertFalse(iter4.hasNext());

    }
}