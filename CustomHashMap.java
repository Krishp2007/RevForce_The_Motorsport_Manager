package ds;

public class CustomHashMap<K, V> {
    private static class Entry<K, V> {
        final K key;
        V value;
        Entry<K, V> next;

        Entry(K key, V value) {
            this.key = key;
            this.value = value;
            this.next = null;
        }
    }

    private static final int INITIAL_CAPACITY = 16;
    private static final float LOAD_FACTOR = 0.75f;

    private Entry<K, V>[] buckets;
    private int size;

    @SuppressWarnings("unchecked")
    public CustomHashMap() {
        buckets = new Entry[INITIAL_CAPACITY];
        size = 0;
    }

    private int getBucketIndex(K key) {
        return (key == null) ? 0 : Math.abs(key.hashCode() % buckets.length);
    }

    public void put(K key, V value) {
        // Check if resize needed before putting
        if (size >= buckets.length * LOAD_FACTOR) {
            resize();
        }

        int index = getBucketIndex(key);
        Entry<K, V> newEntry = new Entry<>(key, value);
        Entry<K, V> current = buckets[index];

        if (current == null) {
            buckets[index] = newEntry;
            size++;
            return;
        }

        Entry<K, V> prev = null;
        while (current != null) {
            if ((key == null && current.key == null) || (key != null && key.equals(current.key))) {
                // Key exists, update value
                current.value = value;
                return;
            }
            prev = current;
            current = current.next;
        }
        prev.next = newEntry;
        size++;
    }

    public V get(K key) {
        int index = getBucketIndex(key);
        Entry<K, V> current = buckets[index];

        while (current != null) {
            if ((key == null && current.key == null) || (key != null && key.equals(current.key))) {
                return current.value;
            }
            current = current.next;
        }
        return null;
    }

    public V remove(K key) {
        int index = getBucketIndex(key);
        Entry<K, V> current = buckets[index];
        Entry<K, V> prev = null;

        while (current != null) {
            if ((key == null && current.key == null) || (key != null && key.equals(current.key))) {
                V value = current.value;
                if (prev == null) {
                    buckets[index] = current.next;
                } else {
                    prev.next = current.next;
                }
                size--;
                return value;
            }
            prev = current;
            current = current.next;
        }
        return null;
    }

    public int size() {
        return size;
    }

    @SuppressWarnings("unchecked")
    private void resize() {
        int newCapacity = buckets.length * 2;
        Entry<K, V>[] newBuckets = new Entry[newCapacity];

        for (Entry<K, V> head : buckets) {
            while (head != null) {
                Entry<K, V> next = head.next;
                int newIndex = (head.key == null) ? 0 : Math.abs(head.key.hashCode() % newCapacity);

                head.next = newBuckets[newIndex];
                newBuckets[newIndex] = head;

                head = next;
            }
        }
        buckets = newBuckets;
    }
}
