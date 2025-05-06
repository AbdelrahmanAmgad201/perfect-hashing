package org.example.maps;

import java.util.*;

public class SquareHasher implements MapInterface{
    private final int P = 1_000_003;  // A large prime number for hashing
    private final Random random = new Random();
    private final double maximumThreshold = 0.75;  // Load factor threshold for resizing
    private final double minimumThreshold = 0.25;  // Load factor threshold for resizing

    private String[] table;

    private int a, b;
    private int size;
    private int keysNumber;
    private int rehashCounter;

    public SquareHasher() {
        this.keysNumber = 0;
        this.size = 100;  // Initial size
        table = new String[size];
        a = random.nextInt(1, P);
        b = random.nextInt(P);
        rehashCounter = 0;
    }

    public boolean insert(String key) {
        if (contains(key))
            return false;

        keysNumber++;

        if ((double) keysNumber / Math.pow(size, 0.5) >= maximumThreshold) { //resize
            size = (int) (size * 2);  //Math.pow(Math.pow(size, 1/2) * 2), 2)
            rehashAll(key, true);
            return true;
        }

        int index = hash(key);

        if (table[index] != null) { //collision
            rehashAll(key, true);
        } else {
            table[index] = key;
        }

        return true;
    }

    public boolean contains(String key) {
        if (table == null || table.length == 0) return false;
        int index = hash(key);
        return table[index] != null && table[index].equals(key);
    }

    public boolean delete(String key){
        if (!contains(key)) return false;
        keysNumber--;
        int index = hash(key);
        table[index] = null;
        if ((double) keysNumber / Math.pow(size, 0.5) <= minimumThreshold) { //resize
            size = (int) (size / 2); //Decrease by 2, "4" is after squaring.
            rehashAll(key, false);
        }
        return true;
    }

    private void rehashAll(String problematicKey, boolean insert) {
        boolean collision;
        int index;
        String[] temp = new String[size];

        while (true) {
            collision = false;
            a = random.nextInt(1, P);  // New random values for a and b
            b = random.nextInt(P);

            rehashCounter++;
            System.out.println("\"Rehashing!!!!\"");

            Arrays.fill(temp, null);

            if (insert) {
                index = hash(problematicKey); // insert the key that caused collision
                temp[index] = problematicKey;
            }

            for (String key: table){
                if (key!= null){
                    index = hash(key);
                    if (temp[index] != null && !temp[index].equals(key)){
                        collision = true;
                        break;
                    }
                    temp[index] = key;
                }
            }

            if (!collision) {
                break;
            }else {
                rehashCounter++;
            }

        }
        table = temp;
    }

    private int hash(String key) {
        long h = key.hashCode();
        int hash = (int) (((a * h + b) % P) % size);
        return (hash < 0) ? hash + size : hash;  // Ensure the index is non-negative
    }

    // Print the current table contents (Optional)
    public void printTable() {
        for (int i = 0; i < table.length; i++) {
            if (table[i] != null) {
                System.out.println("Index " + i + ": " + table[i]);
            }
        }
    }

    public int getRehashingCount(){
        return rehashCounter;
    }
}
