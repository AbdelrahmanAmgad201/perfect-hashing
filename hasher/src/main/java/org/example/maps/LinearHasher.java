// Source code is decompiled from a .class file using FernFlower decompiler.
package org.example.maps;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class LinearHasher {
    ArrayList<String>[] table;
    private int a;
    private int b;
    private int size = 1000;
    private final int P = 1000003;
    private final Random random = new Random();
    private final double maximumThreshold = 0.75;
    private final double minimumThreshold = 0.25;
    private int rehashCounter;
    private int[] A;
    private int[] B;
    private int Kcount;

    public int getSize() {
        return size;
    }
    public int getRehashingCount(){
        return rehashCounter;
    }

    public LinearHasher() {
        this.table = new ArrayList[this.size];
        this.a = this.random.nextInt(1, 1000003);
        this.b = this.random.nextInt(1000003);
        this.rehashCounter = 0;
        this.A = new int[this.size];
        this.B = new int[this.size];

        for(int i = 0; i < this.size; ++i) {
            this.A[i] = this.random.nextInt(1, 1000003);
            this.B[i] = this.random.nextInt(1000003);
        }

        this.Kcount = 0;
    }

    private void rehashRow(int idx) {
        this.A[idx] = this.random.nextInt(1, 1000003);
        this.B[idx] = this.random.nextInt(1000003);
        ArrayList<String> tmp = new ArrayList(this.table[idx].size());
        Iterator var3 = this.table[idx].iterator();

        while(var3.hasNext()) {
            String str = (String)var3.next();
            if (str != null) {
                int second_level = this.hash(str, idx);
                tmp.set(second_level, str);
            }
        }

        this.table[idx] = tmp;
    }

    private void rehashAll() {
        this.a = this.random.nextInt(1, 1000003);
        this.b = this.random.nextInt(1000003);
        this.A = new int[this.size];
        this.B = new int[this.size];

        for(int i = 0; i < this.size; ++i) {
            this.A[i] = this.random.nextInt(1, 1000003);
            this.B[i] = this.random.nextInt(1000003);
        }

        ArrayList<String>[] tmp = new ArrayList[this.size];
        ArrayList[] var2 = this.table;
        int var3 = var2.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            ArrayList<String> bin = var2[var4];
            if (bin != null) {
                Iterator var6 = bin.iterator();

                while(var6.hasNext()) {
                    String str = (String)var6.next();
                    int idx = this.hash(str, -1);
                    if (tmp[idx] == null) {
                        tmp[idx] = new ArrayList(0);
                        tmp[idx].add(str);
                    } else {
                        ArrayList<String> temp = new ArrayList(this.size);
                        temp.set(0, (String)tmp[idx].getFirst());
                        tmp[idx] = temp;
                        tmp[idx].set(this.hash(str, idx), str);
                    }
                }
            }
        }

        this.table = tmp;
    }

    private int hash(String key, int idx) {
        long h;
        int hash;
        if (idx < 0) {
            h = (long)key.hashCode();
            hash = (int)(((long)this.a * h + (long)this.b) % 1000003L % (long)this.size);
            return hash < 0 ? hash + this.size : hash;
        } else {
            h = (long)key.hashCode();
            hash = (int)(((long)this.A[idx] * h + (long)this.B[idx]) % 1000003L % (long)this.size);
            return hash < 0 ? hash + this.size : hash;
        }
    }

    public boolean insert(String K) {
        if (!this.contain(K)) {
            ++this.Kcount;
            if ((double)this.Kcount / Math.pow((double)this.size, 0.5) >= 0.75) {
                this.size = (int)((double)this.size * 1.6);
                this.rehashAll();
            } else {
                int idx = this.hash(K, -1);
                if (this.table[idx] == null) {
                    this.table[idx] = new ArrayList(0);
                    this.table[idx].add(K);
                } else {
                    ArrayList<String> tmp = new ArrayList(this.size);
                    tmp.set(0, (String)this.table[idx].getFirst());
                    this.table[idx] = tmp;
                    this.table[idx].set(this.hash(K, idx), K);
                }

            }
            return true;
        }
        return false;
    }

    public boolean contain(String key) {
        int first_level = this.hash(key, -1);
        if (this.table[first_level] != null) {
            if (this.table[first_level].size() == 1) {
                return ((String)this.table[first_level].getFirst()).equals(key);
            } else {
                int second_level = this.hash(key, first_level);
                return ((String)this.table[first_level].get(second_level)).equals(key);
            }
        } else {
            return false;
        }
    }

    public boolean delete(String key) {
        if (!this.contain(key)) {
            return false;
        } else {
            --this.Kcount;
            if ((double)this.Kcount / Math.pow((double)this.size, 0.5) <= 0.25) {
                this.size /= 2;
                this.rehashAll();
            }

            int first_level = this.hash(key, -1);
            if (this.table[first_level].size() == 1) {
                this.table[first_level] = null;
            } else {
                int second_level = this.hash(key, first_level);
                this.table[first_level].set(second_level, null);
            }

            return true;
        }
    }

    public static void main(String[] args) {
        LinearHasher hasher = new LinearHasher();
        System.out.println("Inserting 1 million unique keys...");

        int i;
        for(i = 0; i < 10000; ++i) {
            hasher.insert("word" + i);
        }

        for(i = 0; i < 1000; ++i) {
            hasher.delete("word" + i);
        }

        for(i = 0; i < 1000; ++i) {
            hasher.insert("word" + i);
        }

        System.out.println("Insertion done. Checking some keys:");
        System.out.println("Contains 'word0': " + hasher.contain("word0"));
        System.out.println("Contains 'word999999': " + hasher.contain("word999"));
        System.out.println("Contains 'word500000': " + hasher.contain("word50"));
        System.out.println("Contains 'nonexistent': " + hasher.contain("nonexistent"));
    }
}
