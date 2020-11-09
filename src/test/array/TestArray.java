package test.array;

import main.top.kindless.search.ST;
import main.top.kindless.search.array.ArrayST;

import java.util.ArrayList;
import java.util.List;

public class TestArray {
    public static void main(String[] args) {
        List<String> list = new ArrayList<>(0);
        list.add("你好");
        list.add("我好");
        System.out.println(list);
        ST<String,String> st = new ArrayST<>(1);
        st.put("hello","world");
        st.put("你好","hello");
        long nanoTime = System.nanoTime();
        st.delete("你好");
        System.out.println("耗时："+(System.nanoTime() - nanoTime)+"纳秒");
        st.forEach((key,value) -> {
            System.out.println("key="+key+", value="+value);
        });
//        System.out.println(st.get("hello"));
//        System.out.println(st.get(st.get("你好")));
//        long nanoTime = System.nanoTime();
//        String hello = st.get("hello");
//        System.out.println("耗时："+(System.nanoTime() - nanoTime)+"纳秒");
    }
}
