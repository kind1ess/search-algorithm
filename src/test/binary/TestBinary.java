package test.binary;

import main.top.kindless.search.binary.BinarySearchST;

import java.util.*;

public class TestBinary {
    public static void main(String[] args) {
        BinarySearchST<String,Integer> st = new BinarySearchST<>(1);
        ArrayList<Integer> integers = new ArrayList<>();
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < 1; i++) {
            long nanoTime = System.nanoTime();
//            st.put(UUID.randomUUID().toString(),new Random().nextInt());
            st.put("a",1234);
            st.put("b",1234);
//            integers.add(1);
//            map.put("a","apple");
            System.out.println("插入耗时："+(System.nanoTime() - nanoTime)+"纳秒");
        }
        {
            long nanoTime = System.nanoTime();
        System.out.println(st.get("a"));
//        System.out.println(map.get("a"));
//            System.out.println(integers.get(0));
            System.out.println("查询耗时：" + (System.nanoTime() - nanoTime) + "纳秒");
        }
//        st.forEach((key,value) -> {
//            long nanoTime = System.nanoTime();
//            System.out.println("Key="+key+",value="+value);
//            System.out.println("耗时："+(System.nanoTime() - nanoTime)+"纳秒");
//        });
        {
            long nanoTime = System.nanoTime();
            st.delete("a");
//            map.remove("a");
            System.out.println("删除耗时：" + (System.nanoTime() - nanoTime) + "纳秒");
        }
    }
}
