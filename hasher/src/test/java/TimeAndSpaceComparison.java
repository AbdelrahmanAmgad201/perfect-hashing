import org.example.maps.LinearHasher;
import org.example.maps.MapInterface;
import org.example.maps.SquareHasher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TimeAndSpaceComparison {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final Random random = new Random();

    public static String generateRandomString(int length) {
        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(index));
        }

        return sb.toString();
    }

    @Test
    @DisplayName("Insert Time Compare")
    void totalInsertTimeSpaceCompare() {
        int n = 5000;
        List<String> keys = new ArrayList<>();
        for (int i = 0; i < n; i++)
            keys.add(generateRandomString(10));

        int[] sizes = {500, 1000, 1500, 2000, 2500, 3000, 3500, 4000, 4500, 5000};

        System.out.printf("%-10s %-20s %-20s %-20s %-20s%n", "Size", "Linear Time (ms)", "Square Time (ms)", "Linear Mem (KB)", "Square Mem (KB)");

        for (int size : sizes) {
            MapInterface linearHasher = new LinearHasher();
            MapInterface squareHasher = new SquareHasher();

            // GC and memory baseline
            System.gc();
            long memBeforeLinear = getUsedMemory();

            long startLinear = System.nanoTime();
            for (int j = 0; j < size; j++)
                linearHasher.insert(keys.get(j));
            long endLinear = System.nanoTime();
            long memAfterLinear = getUsedMemory();

            System.gc();
            long memBeforeSquare = getUsedMemory();

            long startSquare = System.nanoTime();
            for (int j = 0; j < size; j++)
                squareHasher.insert(keys.get(j));
            long endSquare = System.nanoTime();
            long memAfterSquare = getUsedMemory();

            long linearTime = (endLinear - startLinear) / 1_000_000;
            long squareTime = (endSquare - startSquare) / 1_000_000;
            long linearMem = (memAfterLinear - memBeforeLinear) / 1024;
            long squareMem = (memAfterSquare - memBeforeSquare) / 1024;

            System.out.printf("%-10d %-20d %-20d %-20d %-20d%n", size, linearTime, squareTime, linearMem, squareMem);
        }
    }

    @Test
    @DisplayName("Delete Time Compare")
    void totalDeleteTimeSpaceCompare() {
        int n = 5000;
        List<String> keys = new ArrayList<>();
        for (int i = 0; i < n; i++)
            keys.add(generateRandomString(10));

        MapInterface linearHasher = new LinearHasher();
        MapInterface squareHasher = new SquareHasher();

        for (String key : keys) {
            linearHasher.insert(key);
            squareHasher.insert(key);
        }

        long startLinear = System.nanoTime();
        for (String key : keys)
            linearHasher.delete(key);
        long endLinear = System.nanoTime();

        long startSquare = System.nanoTime();
        for (String key : keys)
            squareHasher.delete(key);
        long endSquare = System.nanoTime();

        System.out.printf("Delete Time (5000 keys): Linear = %d ms, Square = %d ms%n",
                (endLinear - startLinear) / 1_000_000,
                (endSquare - startSquare) / 1_000_000);
    }

    @Test
    @DisplayName("Search Time Compare")
    void totalSearchTimeSpaceCompare() {
        int n = 100;
        List<String> keys = new ArrayList<>();
        for (int i = 0; i < n; i++)
            keys.add(generateRandomString(10));

        MapInterface linearHasher = new LinearHasher();
        MapInterface squareHasher = new SquareHasher();

        for (String key : keys) {
            linearHasher.insert(key);
            squareHasher.insert(key);
        }

        long startLinear = System.nanoTime();
        for (String key : keys)
            linearHasher.contains(key);
        long endLinear = System.nanoTime();

        long startSquare = System.nanoTime();
        for (String key : keys)
            squareHasher.contains(key);
        long endSquare = System.nanoTime();

        System.out.printf("Search Time (5000 keys): Linear = %d ms, Square = %d ms%n",
                (endLinear - startLinear) / 1_000_000,
                (endSquare - startSquare) / 1_000_000);
    }

    @Test
    @DisplayName("Mean Search and Insert Time Compare")
    void meanSearchAndInsertTimeCompare() {
        int n = 5000;
        List<String> keys = new ArrayList<>();
        for (int i = 0; i < n; i++)
            keys.add(generateRandomString(10));

        MapInterface linearHasher = new LinearHasher();
        MapInterface squareHasher = new SquareHasher();

        long insertLinear = 0, insertSquare = 0;
        for (String key : keys) {
            long t1 = System.nanoTime();
            linearHasher.insert(key);
            long t2 = System.nanoTime();
            insertLinear += (t2 - t1);

            t1 = System.nanoTime();
            squareHasher.insert(key);
            t2 = System.nanoTime();
            insertSquare += (t2 - t1);
        }

        long searchLinear = 0, searchSquare = 0;
        for (String key : keys) {
            long t1 = System.nanoTime();
            linearHasher.contains(key);
            long t2 = System.nanoTime();
            searchLinear += (t2 - t1);

            t1 = System.nanoTime();
            squareHasher.contains(key);
            t2 = System.nanoTime();
            searchSquare += (t2 - t1);
        }

        System.out.printf("Mean Insert Time: Linear = %.2f µs, Square = %.2f µs%n",
                insertLinear / (double) n / 1000,
                insertSquare / (double) n / 1000);

        System.out.printf("Mean Search Time: Linear = %.2f µs, Square = %.2f µs%n",
                searchLinear / (double) n / 1000,
                searchSquare / (double) n / 1000);
    }

    private long getUsedMemory() {
        Runtime runtime = Runtime.getRuntime();
        return runtime.totalMemory() - runtime.freeMemory();
    }
}
