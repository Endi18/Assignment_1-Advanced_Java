package JavaClasses;

import java.util.HashMap;

public class Histogram extends HashMap<String, Integer> {

    public Histogram(){

    }

    public void putTokens(String token){
        Integer value;
        synchronized (this) {
            if ((value = put(token, 1)) != null)
                put(token, ++value);
        }
    }
}
