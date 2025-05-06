package org.example.maps;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class LinearHasher implements MapInterface {
    private final double maximumThreshold = 0.75;  // Load factor threshold for resizing
    private final double minimumThreshold = 0.25;  // Load factor threshold for resizing
    private final int P = 1_000_003;  // A large prime number for hashing
    private final Random random = new Random();

    ArrayList<String>[] table ;

    private int a, b;
    private int size;
    private int rehashCounter ;
    private int [] A;
    private int [] B ;
    private int Kcount ;

    LinearHasher(){
        this.size = 1000;
        table = new ArrayList[size];
        a = random.nextInt(1, P);
        b = random.nextInt(P);
        rehashCounter = 0;
        A = new int[size];
        B = new int[size];
        for(int i=0;i<size;i++) {
            A[i] = random.nextInt(1, P);
            B[i] = random.nextInt(P);
        }
        Kcount = 0;
    }

    private void rehashRow(int idx){
        A[idx] = random.nextInt(1, P);
        B[idx] = random.nextInt(P);
        
        ArrayList<String> tmp = new ArrayList<>(Collections.nCopies(table[idx].size(), null));
        
        for(String str:table[idx]){
            if(str==null) continue;
            int second_level=hash(str,idx);
            tmp.set(second_level,str);
        }
        table[idx]=tmp;
    }

    private void rehashAll(){
        a = random.nextInt(1, P);
        b = random.nextInt(P);
        A = new int[size];
        B = new int[size];
        for(int i=0;i<size;i++) {
            A[i] = random.nextInt(1, P);
            B[i] = random.nextInt(P);
        }
        ArrayList<String>[]tmp=new ArrayList[size];
        for(ArrayList<String> bin:table){
            if(bin!=null)
            for(String str:bin){
                int idx=hash(str,-1);
                if(tmp[idx]==null){
                    tmp[idx]=new ArrayList<>(0);
                    tmp[idx].add(str);
                }
                else{
                    ArrayList<String> temp = new ArrayList<>(Collections.nCopies(size, null));
                    temp.set(0,tmp[idx].getFirst());
                    tmp[idx]=temp;
                    tmp[idx].set(hash(str,idx),str);
                }
            }
        }
        table=tmp;
    }

    private int hash(String key , int idx) {
        if(idx < 0){
            long h = key.hashCode();
            int hash = (int) (((a * h + b) % P) % size);
            return (hash < 0) ? hash + size : hash;
        }
        long h = key.hashCode();
        int hash = (int) (((A[idx] * h + B[idx]) % P) % table[idx].size());
        return (hash < 0) ? hash + size : hash;
    }

    boolean completeRow(ArrayList<String> list){
        for(String str:list){
            if(str==null) return false;
        }
        return true;
    }

    public boolean insert(String K){
        if (contain(K)){
            return false;
        }

        if ((double) Kcount / Math.pow(size, 0.5) >= maximumThreshold) {
            size = (int) (size * 1.6);  //Math.pow(Math.pow(size, 1/2) * 2), 2)
            rehashAll();
            return;
        }
        int idx=hash(K,-1);
        if(table[idx]==null){
            table[idx]=new ArrayList<>(0);
            table[idx].add(K);
            Kcount++;
        }
        else{
           int second_level=hash(K,idx);
           if(completeRow(table[idx])){
               ArrayList<String> tmp = new ArrayList<>(Collections.nCopies(table[idx].size()*2, null));
               for(int i = 0 ; i < table[idx].size(); i++){
                   tmp.set(i,table[idx].get(i));
               }
               table[idx] = tmp ;
               rehashRow(idx);
               insert(K);
           }else if(table[idx].get(second_level)!=null){
               rehashRow(idx);
                insert(K);
           }else{
                table[idx].set(second_level,K);
                Kcount++;
           }
        }
    }

    public boolean contain(String key){
        int first_level=hash(key,-1);
        if(table[first_level]!=null){
            if(table[first_level].size()==1) return table[first_level].getFirst().equals(key);
            int second_level=hash(key,first_level);
            return table[first_level].get(second_level).equals(key);
        }
        return false;
    }

    public boolean delete(String key){
        if (!contain(key)) return false;
        Kcount--;
        if ((double) Kcount / Math.pow(size, 0.5) <= minimumThreshold) {
            size = (int) (size / 2); //Decrease by 2, "4" is after squaring.
            rehashAll();
        }
        int first_level=hash(key,-1);
        if(table[first_level].size()==1) table[first_level]=null;
        else {
            int second_level=hash(key,first_level);
            table[first_level].set(second_level,null);
        }
        return true;
    }
}
