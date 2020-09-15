package test.bst;

import main.top.kindless.search.OrderedST;
import main.top.kindless.search.ST;
import main.top.kindless.search.bst.BST;
import main.top.kindless.search.utils.CastUtil;
import test.annotation.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Scanner;

import static main.top.kindless.search.utils.CastUtil.cast;

public class TestBst {

    public static void main(String[] args) {
        TestBst testBst = new TestBst();
        Class<? extends TestBst> testBstClass = testBst.getClass();
        Method[] methods = testBstClass.getMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(Test.class)){
                try {
                    method.invoke(testBst);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    @Test
    public void test(){
        OrderedST<Integer,String> st = new BST<>();
        Scanner scanner = new Scanner(System.in);
        boolean flag = true;
        while (flag){
            System.out.println("==========\n"+
                                "插入：1\n"+
                                "删除：2\n"+
                                "查询：3\n"+
                                "查询所有：4\n"+
                                "范围查询：5\n"+
                                "退出：0\n"+
                                "==========");
            int option = scanner.nextInt();
            switch (option){
                case 1:
                    System.out.println("=======请输入键=======");
                    int key = scanner.nextInt();
                    System.out.println("=======请输入值=======");
                    String value = scanner.next();
                    st.put(key, value);
                    break;
                case 2:
                    System.out.println("=======请输入键=======");
                    int deleteKey = scanner.nextInt();
                    st.delete(deleteKey);
                    break;
                case 3:
                    System.out.println("=======请输入键=======");
                    int queryKey = scanner.nextInt();
                    System.out.println("{Key="+queryKey+",Value="+st.get(queryKey)+"}");
                    break;
                case 4:
                    st.forEach((k,v) ->{
                        System.out.println("{Key="+k+",Value="+v+"}");
                    });
                    break;
                case 5:
                    System.out.println("=======请输入最小键=======");
                    int minKey = scanner.nextInt();
                    System.out.println("=======请输入最大键=======");
                    int maxKey = scanner.nextInt();
                    st.keys(minKey,maxKey).forEach(integer -> {
                        System.out.println("{Key="+integer+",Value="+st.get(integer)+"}");
                    });
                    break;
                case 0:
                    flag = false;
                    break;
            }
        }
    }
}
