package com.terheyden;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Stream;

import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

import org.junit.jupiter.api.Test;

@ParametersAreNonnullByDefault
public class Experimental
{
    /**
     * https://stackoverflow.com/questions/35486826/transform-and-filter-a-java-map-with-streams
     */
    @Test
    public void neat()
    {
        User user1 = new User("Cora", 8);
        User user2 = new User("Tashi", 11);
        User user3 = new User("Mika", 12);

        ImmutableMap<String, User> users = ImmutableMap.of(
            "Mika", user3,
            "Tashi", user2);

        // Can transform the entire value type - let's change it from one user to a list:
        Map<String, ImmutableList<User>> usersList = Maps.transformValues(users, ImmutableList::of);

        assertEquals(2, usersList.size());

        // Can transform the value based on both the key and value.
        // Let's change the User value to a String.

        Maps.EntryTransformer<String, User, String> userToString =
            (key, value) -> String.format("%s, age %d", key, value.getAge());

        Map<String, String> userStrings = Maps.transformEntries(users, userToString);

        assertTrue(userStrings.containsValue("Mika, age 12"));

        // This will set to null, but will not remove:

        Maps.EntryTransformer<String, User, User> deleteMika =
            (key, value) -> "Mika".equals(key) ? null : value;

        Map<String, User> users2 = Maps.transformEntries(users, deleteMika);

        assertEquals(2, users2.size());

        // We can make null and filter nulls:

        Map<String, User> trimmed = Maps.filterEntries(
            Maps.transformEntries(users, deleteMika),
            e -> e.getValue() != null);

        assertEquals(1, trimmed.size());
    }

    /**
     * https://www.baeldung.com/java-8-collectors
     */
    @Test
    public void testStreams()
    {
        // I want to stream a map and collect it.

        User user1 = new User("Cora", 8);
        User user2 = new User("Tashi", 11);
        User user3 = new User("Mika", 12);

        ImmutableMap<String, User> users = ImmutableMap.of(
            "Mika", user3,
            "Tashi", user2);

        users.entrySet().stream()
            .collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, Map.Entry::getValue));

        // Collectors.collectingAndThen() - collect and then make immutable.

        ImmutableMap<String, User> map2 = users.entrySet().stream()
            .collect(ImmutableCollectors.toThing());
    }

    @SafeVarargs
    private static <R> Stream<? extends R> addItems(R... items)
    {
        return null;
    }

    public static class MyCollector<K, V> implements Collector<Map.Entry<K, V>, ImmutableMap.Builder<K, V>, ImmutableMap<K, V>>
    {
        /**
         * A function that creates and returns a new mutable result container.
         *
         * @return a function which returns a new, mutable result container
         */
        @Override
        public Supplier<ImmutableMap.Builder<K, V>> supplier()
        {
            return ImmutableMap::builder;
        }

        /**
         * A function that folds a value into a mutable result container.
         *
         * @return a function which folds a value into a mutable result container
         */
        @Override
        public BiConsumer<ImmutableMap.Builder<K, V>, Map.Entry<K, V>> accumulator()
        {
            return ImmutableMap.Builder::put;
        }

        /**
         * A function that accepts two partial results and merges them.  The combiner function may
         * fold state from one argument into the other and return that, or may return a new result
         * container.
         *
         * @return a function which combines two partial results into a combined result
         */
        @Override
        public BinaryOperator<ImmutableMap.Builder<K, V>> combiner()
        {
            return (build1, build2) -> build1.putAll(build2.build());
        }

        /**
         * Perform the final transformation from the intermediate accumulation type {@code A} to the
         * final result type {@code R}.
         *
         * <p>If the characteristic {@code IDENTITY_FINISH} is
         * set, this function may be presumed to be an identity transform with an unchecked cast
         * from {@code A} to {@code R}.
         *
         * @return a function which transforms the intermediate result to the final result
         */
        @Override
        public Function<ImmutableMap.Builder<K, V>, ImmutableMap<K, V>> finisher()
        {
            return ImmutableMap.Builder::build;
        }

        /**
         * Returns a {@code Set} of {@code Collector.Characteristics} indicating the characteristics
         * of this Collector.  This set should be immutable.
         *
         * @return an immutable set of collector characteristics
         */
        @Override
        public Set<Characteristics> characteristics()
        {
            return ImmutableSet.of();
        }
    }
}
