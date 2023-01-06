package JavaClasses;

import java.util.Hashtable;

public class Histogram extends Hashtable<String, Integer> {
    synchronized public void putTokens(String token){
        Integer value;
        if((value = put(token, 1)) != null)
            put(token, ++value);
    }
}
