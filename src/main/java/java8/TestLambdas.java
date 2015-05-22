package java8;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class TestLambdas {
    
    /**
     * @param args 
     */
    public static void main(String[] args) {
        
        List<Object> objects = Arrays.asList(
                new File("/tmp/test0"),
                "a string object",
                "another string",
                new File("/tmp/test1"),
                new File("/tmp/test2")
        );
        
        objects.stream().filter(File.class::isInstance)
                .map(o -> (File) o)
                .filter(f -> isValid(f))
                .forEach(f -> doSomething(f));
    }
    
    private static boolean isValid(File f) {
        return f != null && f.getName().endsWith("0");
    }
    
    private static void doSomething(File f) {
        System.out.println(f.getName());
    }
}