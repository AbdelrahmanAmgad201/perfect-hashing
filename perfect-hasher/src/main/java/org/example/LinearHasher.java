package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LinearHasher {
    ArrayList[] table ;
    private int a, b;
    private int size;
    private final int P = 1_000_003;  // A large prime number for hashing
    private final Random random = new Random();
    private final double maximumThreshold = 0.75;  // Load factor threshold for resizing
    private final double minimumThreshold = 0.25;  // Load factor threshold for resizing
    private int rehashCounter ;
    private int [] A;
    private int [] B ;
    private int Kcount ;

    LinearHasher(){
        this.size = 100;
        table = new ArrayList[size];
        a = random.nextInt(1, P);
        b = random.nextInt(P);
        rehashCounter = 0;
        A = new int[size];
        B = new int[size];
        Kcount = 0;
    }
    private int hash(String key , int idx) {
        if(idx < 0){
            long h = key.hashCode();
            int hash = (int) (((a * h + b) % P) % size);
            return (hash < 0) ? hash + size : hash;
        }
        long h = key.hashCode();
        int hash = (int) (((A[idx] * h + B[idx]) % P) % size);
        return (hash < 0) ? hash + size : hash;
    }
    public void instrt(String K){
        //if(not found)
        Kcount++;
        if ((double) Kcount / Math.pow(size, 0.5) >= maximumThreshold) { //resize
            size = (int) (size * 2);  //Math.pow(Math.pow(size, 1/2) * 2), 2)
            //rehashAll(key, true);
            System.out.println("\"" + K + "\" has been successfully Inserted and size of table has increased!");
            return;
        }

    }
}
