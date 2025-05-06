package org.example.maps;

import java.util.ArrayList;
import java.util.Random;

public class LinearHasher {
    private final double maximumThreshold = 0.75;  // Load factor threshold for resizing
    private final double minimumThreshold = 0.25;  // Load factor threshold for shrinking
    private final int P = 1_000000_003;  // A large prime number for hashing
    private final Random random = new Random();

    ArrayList<String>[] table; // First level: Array of ArrayLists

    private int a, b;
    private int size;
    private int rehashCounter;
    private int[] A;
    private int[] B;
    private int Kcount;
    private int RehashCounter;

    public int getRehashCounter() {
        return RehashCounter;
    }

    public int getSize() {
        return size;
    }

    public LinearHasher() {
        this.size = 1000;
        table = new ArrayList[size];
        a = random.nextInt(1, P);
        b = random.nextInt(P);
        rehashCounter = 0;
        A = new int[size];
        B = new int[size];
        for (int i = 0; i < size; i++) {
            A[i] = random.nextInt(1, P);
            B[i] = random.nextInt(P);
        }
        Kcount = 0;
    }


    private void rehashAll() {
        ArrayList<String>[] oldTable = table;
        table = new ArrayList[size];
        a = random.nextInt(1, P);
        b = random.nextInt(P);
        A = new int[size];
        B = new int[size];
        for (int i = 0; i < size; i++) {
            A[i] = random.nextInt(1, P);
            B[i] = random.nextInt(P);
        }
        Kcount = 0; // Reset and re-insert all keys
        for (ArrayList<String> bucket : oldTable) {
            if (bucket != null) {
                for (String str : bucket) {
                    if (str != null) {
                        insert(str);
                    }
                }
            }
        }
    }

    private void rehashBucket(int idx) {
        ArrayList<String> oldBucket = table[idx];
        int oldSize = oldBucket.size();
        int newSize = oldSize * 2;
        boolean collisionFree = false;
        int attempts = 0;
        final int maxAttempts = 7;

        while (!collisionFree && attempts < maxAttempts) {
            table[idx] = new ArrayList<>(newSize);
            for (int i = 0; i < newSize; i++) {
                table[idx].add(null);
            }
            A[idx] = random.nextInt(1, P);
            B[idx] = random.nextInt(P);
            collisionFree = true;

            for (String str : oldBucket) {
                if (str != null) {
                    int second_level = hash(str, idx);
                    while (table[idx].size() <= second_level) {
                        table[idx].add(null);
                    }
                    if (table[idx].get(second_level) != null) {
                        collisionFree = false;
                        newSize *= 2;
                        break;
                    }
                    table[idx].set(second_level, str);
                }
            }
            attempts++;
        }
        if(!collisionFree)
        rehashAll();

    }

    private int hash(String key, int idx) {
        long h = key.hashCode();

        if (idx < 0) {
            int hash = (int) ((a*h+b)%P % size);
            return hash < 0 ? hash + size : hash;
        }
        int sz = 4; // Default size for second-level ArrayList
        if (table[idx] != null && !table[idx].isEmpty()) {
            sz = table[idx].size();
        }
        int hash = (int) (((A[idx] * h + B[idx]) % P) % sz);
        return hash < 0 ? hash + sz : hash;
    }

    public boolean insert(String K) {
        if (contain(K)) {
            return false;
        }
        if ((double) Kcount / size >= maximumThreshold) {
            size = size * 2;
            rehashAll();
        }
        int idx = hash(K, -1);
        if (table[idx] == null) {
            table[idx] = new ArrayList<>(4);
            for (int i = 0; i < 4; i++) {
                table[idx].add(null);
            }
        }
        int second_level = hash(K, idx);

        while (table[idx].get(second_level) != null) {
            rehashBucket(idx);
            second_level = hash(K, idx);
        }
        table[idx].set(second_level, K);
        Kcount++;
        return true;
    }

    public boolean contain(String key) {
        int first_level = hash(key, -1);
        if (table[first_level] != null) {
            int second_level = hash(key, first_level);
            if (second_level < table[first_level].size()) {
                String value = table[first_level].get(second_level);
                return value != null && value.equals(key);
            }
        }
        return false;
    }

    public boolean delete(String key) {
        if (!contain(key)) return false;
        Kcount--;
        if (Kcount > 0 && (double) Kcount / size <= minimumThreshold) {
            size = Math.max(1000, size / 2);
            rehashAll();
        }
        int first_level = hash(key, -1);
        int second_level = hash(key, first_level);
        table[first_level].set(second_level, null);
        if (table[first_level].stream().allMatch(str -> str == null)) {
            table[first_level] = null;
        }
        return true;
    }
}