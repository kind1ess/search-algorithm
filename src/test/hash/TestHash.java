package test.hash;


import main.top.kindless.search.hash.SeparateChainingHashST;

import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

public class TestHash {
    public static void main(String[] args) {
        HashMap<String, String> stringStringHashMap = new HashMap<>();
        SeparateChainingHashST<String,String> st = new SeparateChainingHashST<>(1,.75);
        long currentTimeMillis = System.currentTimeMillis();
        for (int i = 0; i < 200; i++) {
            st.put(UUID.randomUUID().toString(),UUID.randomUUID().toString());
        }
        System.out.println("插入耗时："+(System.currentTimeMillis() - currentTimeMillis)+"毫秒");
        System.out.println("装载因子："+st.loadFactor());
        int[] ints = st.sizeOfEachButton();
        Arrays.stream(ints)
                .max()
                .ifPresent(System.out::println);
        System.out.println(st.treeNum());
    }
}
