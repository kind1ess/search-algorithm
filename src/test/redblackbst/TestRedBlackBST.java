package test.redblackbst;

import main.top.kindless.search.ST;
import main.top.kindless.search.bst.BST;
import main.top.kindless.search.redblackbst.RedBlackBST;

import java.util.Scanner;
import java.util.UUID;

public class TestRedBlackBST {

    public static void main(String[] args) {
        ST<String,String> st = new RedBlackBST<>();
        long currentTimeMillis = System.currentTimeMillis();
        for (int i = 0; i < 1000000; i++) {
            st.put(UUID.randomUUID().toString(),UUID.randomUUID().toString());
        }
        System.out.println("插入耗时："+(System.currentTimeMillis() - currentTimeMillis)+"毫秒");
        st.forEach((key,value) -> {
            System.out.println("key="+key+", value="+value);
        });
        Scanner scanner = new Scanner(System.in);
        while (true){
            String next = scanner.next();
            if (next.equals("quit"))
                break;
            long nanoTime = System.nanoTime();
            System.out.println(st.get(next));
            System.out.println("查询耗时：" + (System.nanoTime() - nanoTime) + "纳秒");
        }
    }

}
