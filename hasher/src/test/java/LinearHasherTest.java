
import org.example.maps.LinearHasher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class LinearHasherTest {

    private LinearHasher hasher;

    @BeforeEach
    void setUp() {
        hasher = new LinearHasher();
    }

    @Test
    @DisplayName("Test inserting and checking single item")
    void testInsertAndContainsSingleItem() {
        String key = "testKey";
        assertTrue(hasher.insert(key));
        assertTrue(hasher.contains(key));
    }

    @Test
    @DisplayName("Test inserting duplicate item returns false")
    void testInsertDuplicate() {
        String key = "testKey";
        assertTrue(hasher.insert(key));
        assertFalse(hasher.insert(key));
        assertTrue(hasher.contains(key));
    }

    @Test
    @DisplayName("Test deleting an item")
    void testDelete() {
        String key = "testKey";
        hasher.insert(key);
        assertTrue(hasher.delete(key));
        assertFalse(hasher.contains(key));
    }

    @Test
    @DisplayName("Test deleting non-existent item returns false")
    void testDeleteNonExistent() {
        String key = "nonExistentKey";
        assertFalse(hasher.delete(key));
    }

    @Test
    @DisplayName("Test inserting multiple items")
    void testInsertMultipleItems() {
        List<String> keys = List.of("key1", "key2", "key3", "key4", "key5");

        for (String key : keys) {
            assertTrue(hasher.insert(key));
        }

        for (String key : keys) {
            assertTrue(hasher.contains(key));
        }
    }

    @Test
    @DisplayName("Test performance with large number of insertions")
    void testLargeInsertionPerformance() {
        int numItems = 1000000;
        Set<String> keys = new HashSet<>();
        Random random = new Random(42); // Fixed seed for reproducibility

        for (int i = 0; i < numItems; i++) {
            keys.add("key" + random.nextInt(100000));
        }

        // Measure time
        long startTime = System.nanoTime();

        for (String key : keys) {
            hasher.insert(key);
        }

        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 1_000_000; // Convert to milliseconds

        System.out.println("Time taken to insert " + numItems + " items: " + duration + " ms");
        System.out.println("Rehash counter: " + hasher.getRehashCounter());

        // Verify all keys are present
        int found = 0;
        for (String key : keys) {
            if (hasher.contains(key)) {
                found++;
            }
        }

        System.out.println("Found " + found + " out of " + keys.size() + " keys");
        assertEquals(keys.size(), found);
    }

    @Test
    @DisplayName("Test memory usage")
    void testMemoryUsage() {
        // Get initial memory usage
        System.gc(); // Request garbage collection to get more accurate results
        long initialMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        int numItems = 10000;
        List<String> keys = new ArrayList<>();
        Random random = new Random(42);

        for (int i = 0; i < numItems; i++) {
            keys.add("key" + random.nextInt(1000000));
        }

        // Insert keys
        for (String key : keys) {
            hasher.insert(key);
        }

        // Get memory after insertion
        System.gc();
        long afterInsertionMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        long memoryUsed = afterInsertionMemory - initialMemory;
        System.out.println("Approximate memory used: " + (memoryUsed / 1024) + " KB");
        System.out.println("Average memory per entry: " + (memoryUsed / numItems) + " bytes");
    }

    @Test
    @DisplayName("Test insertion that triggers resizing")
    void testResizing() {
        int numItems = 1000; // Should trigger at least one resize
        List<String> keys = new ArrayList<>();

        for (int i = 0; i < numItems; i++) {
            keys.add("key" + i);
        }

        // Capture the initial size
        int initialSize = hasher.getSize();

        for (String key : keys) {
         hasher.insert(key);
        }

        // Check if resizing occurred
        System.out.println("Initial size: " + initialSize);
        System.out.println("Final size: " + hasher.getSize());
        System.out.println("Rehashes performed: " + hasher.getRehashCounter());

        // Verify all keys are still present after rehashing
        for (String key : keys) {
            assertTrue(hasher.contains(key), "Key should be present after rehashing: " + key);
        }
    }

    @Test
    @DisplayName("Test operation timing for different sizes")
    void testOperationTiming() {
        int[] testSizes = {100, 1000, 5000, 10000};

        for (int size : testSizes) {
            LinearHasher newHasher = new LinearHasher();
            List<String> keys = new ArrayList<>();

            for (int i = 0; i < size; i++) {
                keys.add("timing_key" + i);
            }

            long insertStart = System.nanoTime();
            for (String key : keys) {
                assertTrue(newHasher.insert(key));
            }
            long insertEnd = System.nanoTime();
            long insertTime = (insertEnd - insertStart) / 1_000_000;

            // Measure lookup time
            long lookupStart = System.nanoTime();
            for (String key : keys) {
                newHasher.contains(key);
            }
            long lookupEnd = System.nanoTime();
            long lookupTime = (lookupEnd - lookupStart) / 1_000_000;

            System.out.println("Size: " + size);
            System.out.println("  Insertion time: " + insertTime + " ms");
            System.out.println("  Lookup time: " + lookupTime + " ms");
            System.out.println("  Rehash counter: " + newHasher.getRehashCounter());
        }
    }

    @Test
    @DisplayName("Test deletion performance")
    void testDeletionPerformance() {
        int numItems = 5000;
        List<String> keys = new ArrayList<>();

        for (int i = 0; i < numItems; i++) {
            keys.add("del_key" + i);
        }

        // Insert all keys
        for (String key : keys) {
            assertTrue(hasher.insert(key));
        }

        // Measure deletion time for half the keys
        long startTime = System.nanoTime();

        for (int i = 0; i < numItems / 2; i++) {
            hasher.delete(keys.get(i));
        }

        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 1_000_000;

        System.out.println("Time taken to delete " + (numItems / 2) + " items: " + duration + " ms");

        // Verify deleted keys are gone and others remain
        for (int i = 0; i < numItems; i++) {
            if (i < numItems / 2) {
                assertFalse(hasher.contains(keys.get(i)), "Key should be deleted: " + keys.get(i));
            } else {
                assertTrue(hasher.contains(keys.get(i)), "Key should be present: " + keys.get(i));
            }
        }
    }

    @Test
    @DisplayName("Test mixed operations")
    void testMixedOperations() {
        int numOperations = 10000;
        List<String> keys = new ArrayList<>();
        Random random = new Random(42);

        long startTime = System.nanoTime();

        for (int i = 0; i < numOperations; i++) {
            int operation = random.nextInt(3);
            String key = "mixed_key" + random.nextInt(1000);

            switch (operation) {
                case 0: // Insert
                    if (hasher.insert(key)) {
                        keys.add(key);
                    }
                    break;
                case 1:
                    if (!keys.isEmpty()) {
                        int indexToDelete = random.nextInt(keys.size());
                        hasher.delete(keys.get(indexToDelete));
                        keys.remove(indexToDelete);
                    }
                    break;
                case 2: // Lookup
                    if (!keys.isEmpty()) {
                        int indexToLookup = random.nextInt(keys.size());
                        hasher.contains(keys.get(indexToLookup));
                    }
                    break;
            }
        }

        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 1_000_000;

        System.out.println("Time taken for " + numOperations + " mixed operations: " + duration + " ms");
    }
}