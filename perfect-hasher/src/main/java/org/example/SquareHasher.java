package org.example;

import java.util.*;

public class SquareHasher {
    private String[] table;
    private int a, b;
    private int size;
    private final int P = 1_000_003;  // A large prime number for hashing
    private final Random random = new Random();
    private int keysNumber;
    private final double maximumThreshold = 0.75;  // Load factor threshold for resizing
    private final double minimumThreshold = 0.25;  // Load factor threshold for resizing
    private int rehashCounter;


    public SquareHasher() {
        this.keysNumber = 0;
        this.size = 100;  // Initial size
        table = new String[size];
        a = random.nextInt(1, P);
        b = random.nextInt(P);
        rehashCounter = 0;
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

    public void insert(String key) {
        if (contains(key)){
            System.out.println("\"" + key + "\" is stored already!");
            return;
        }
        keysNumber++;

        if ((double) keysNumber / Math.pow(size, 0.5) >= maximumThreshold) { //resize
            size = (int) (size * 2);  //Math.pow(Math.pow(size, 1/2) * 2), 2)
            rehashAll(key, true);
            System.out.println("\"" + key + "\" has been successfully Inserted and size of table has increased!");
            return;
        }

        int index = hash(key);

        if (table[index] != null) { //collision
            rehashAll(key, true);
        } else {
            table[index] = key;
        }

        System.out.println("\"" + key + "\" has been successfully Inserted!");
    }

    public boolean contains(String key) {
        if (table == null || table.length == 0) return false;
        int index = hash(key);
        return table[index] != null && table[index].equals(key);
    }

    private int hash(String key) {
        long h = key.hashCode();
        int hash = (int) (((a * h + b) % P) % size);
        return (hash < 0) ? hash + size : hash;  // Ensure the index is non-negative
    }

    boolean delete(String key){
        if (!contains(key)) return false;
        keysNumber--;
        int index = hash(key);
        table[index] = null;
        if ((double) keysNumber / Math.pow(size, 0.5) <= minimumThreshold) { //resize
            size = (int) (size / 2); //Decrease by 2, "4" is after squaring.
            rehashAll(key, false);
            System.out.println("\"" + key + "\" has been successfully Deleted and size of table has decreased!");
        }else{
            System.out.println("\"" + key + "\" has been successfully Deleted!");
        }
        return true;
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


    public static void main(String[] args) {
        SquareHasher hasher = new SquareHasher();

        System.out.println("Inserting 1 million unique keys...");
        for (int i = 0; i < 1_000_000; i++) {
            hasher.insert("word" + i);
        }

        System.out.println("Insertion done. Checking some keys:");
        System.out.println("Contains 'word0': " + hasher.contains("word0"));       // true
        System.out.println("Contains 'word999999': " + hasher.contains("word999999")); // true
        System.out.println("Contains 'word500000': " + hasher.contains("word500000")); // true
        System.out.println("Contains 'nonexistent': " + hasher.contains("nonexistent")); // false
    }


}
