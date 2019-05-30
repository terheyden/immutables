# Immutables

`Immutables` is a utility class for working with Guava immutable objects.

There are many excellent reasons to use immutable objects wherever possible.
But that doesn't mean it should be a pain in the neck to work with them.

```java

    LedgerItem item2 = new LedgerItem(hourAhead, id2);

    // Multiple items can fire at the same time, so we make a list here:
    ImmutableList<LedgerItem> items2 = ImmutableList.of(item2);

    // Items are mapped to a scheduled time, so:
    ImmutableSortedMap<Instant, ImmutableList<LedgerItem>> scheduledItems2 =
        ImmutableSortedMap.<Instant, ImmutableList<LedgerItem>>naturalOrder()
            .putAll(scheduledItems1b)
            .put(hourAhead, items2)
            .build();
```

```java
    LedgerItem item2 = new LedgerItem(hourAhead, id2);

    // Items are mapped to a scheduled time, so:
    ImmutableSortedMap<Instant, ImmutableList<LedgerItem>> scheduledItems2 =
        Immutables.addToSortedMapList(scheduledItems1b, hourAhead, item2);
```
