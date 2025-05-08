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
    void totalInsertTimeCompare() {
        int n = 5000;
        List<String> keys = new ArrayList<>();
        for (int i = 0; i < n; i++)
            keys.add(generateRandomString(10));

        int[] sizes = {1000, 2000, 3000, 4000, 5000};

        System.out.printf("%-10s %-20s %-20s%n", "Size", "Linear Time (ms)", "Square Time (ms)");

        for (int size : sizes) {
            MapInterface linearHasher = new LinearHasher();
            long startLinear = System.nanoTime();
            for (int j = 0; j < size; j++)
                linearHasher.insert(keys.get(j));
            long endLinear = System.nanoTime();

            MapInterface squareHasher = new SquareHasher();
            long startSquare = System.nanoTime();
            for (int j = 0; j < size; j++)
                squareHasher.insert(keys.get(j));
            long endSquare = System.nanoTime();

            long linearTime = (endLinear - startLinear) / 1_000_000;
            long squareTime = (endSquare - startSquare) / 1_000_000;

            System.out.printf("%-10d %-20d %-20d%n", size, linearTime, squareTime);
        }
    }

    @Test
    @DisplayName("Insert Space Compare")
    void totalInsertSpaceCompare() {
        int n = 5000;
        List<String> keys = new ArrayList<>();
        for (int i = 0; i < n; i++)
            keys.add(generateRandomString(10));

        int[] sizes = {1000, 2000, 3000, 4000, 5000};

        System.out.printf("%-10s %-20s %-20s%n", "Size", "Linear Mem (KB)", "Square Mem (KB)");

        for (int size : sizes) {
            // LinearHasher memory
            System.gc();
            long memBeforeLinear = getUsedMemory();
            MapInterface linearHasher = new LinearHasher();
            for (int j = 0; j < size; j++)
                linearHasher.insert(keys.get(j));
            long memAfterLinear = getUsedMemory();

            // SquareHasher memory
            System.gc();
            long memBeforeSquare = getUsedMemory();
            MapInterface squareHasher = new SquareHasher();
            for (int j = 0; j < size; j++)
                squareHasher.insert(keys.get(j));
            long memAfterSquare = getUsedMemory();

            long linearMemKB = (memAfterLinear - memBeforeLinear) / 1024;
            long squareMemKB = (memAfterSquare - memBeforeSquare) / 1024;

            System.out.printf("%-10d %-20d %-20d%n", size, linearMemKB, squareMemKB);
        }
    }

    @Test
    @DisplayName("Search Time Compare")
    void totalSearchTimeCompare() {
        int n = 5000;
        List<String> keys = new ArrayList<>();
        for (int i = 0; i < n; i++)
            keys.add(generateRandomString(10));

        int[] sizes = {1000, 2000, 3000, 4000, 5000};

        System.out.printf("%-10s %-20s %-20s%n", "Size", "Linear Search (ms)", "Square Search (ms)");

        for (int size : sizes) {
            MapInterface linearHasher = new LinearHasher();
            for (int i = 0; i < size; i++) {
                linearHasher.insert(keys.get(i));
            }

            long startLinear = System.nanoTime();
            for (int i = 0; i < size; i++)
                linearHasher.contains(keys.get(i));
            long endLinear = System.nanoTime();

            MapInterface squareHasher = new SquareHasher();
            for (int i = 0; i < size; i++) {
                squareHasher.insert(keys.get(i));
            }

            long startSquare = System.nanoTime();
            for (int i = 0; i < size; i++)
                squareHasher.contains(keys.get(i));
            long endSquare = System.nanoTime();

            long linearTime = (endLinear - startLinear) / 1_000_000;
            long squareTime = (endSquare - startSquare) / 1_000_000;

            System.out.printf("%-10d %-20d %-20d%n", size, linearTime, squareTime);
        }
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
