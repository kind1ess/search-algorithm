package test.sequential;

import main.top.kindless.search.ST;
import main.top.kindless.search.sequential.SequentialSearchST;


public class TestSequential {

    public static void main(String[] args) {
        ST<String,String> st = new SequentialSearchST<>();
        st.put("a","apple");
        st.put("b","apple");
        st.put("c","cherry");
        st.delete("a");
        st.forEach((k,v) -> System.out.println("[Key="+k+", Value="+v+"]"));
    }

}
