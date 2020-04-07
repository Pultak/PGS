package utils;

import core.AFileSegment;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class Functions {


    public static void sumUpEveryWord(HashMap<String, MutableInteger> resultMap, List listOfFragments){
        for(AFileSegment fragment : (List<AFileSegment>)listOfFragments){
            for(Iterator keys = fragment.wordMap.keySet().iterator(), values = fragment.wordMap.values().iterator(); keys.hasNext();){
                String key = (String) keys.next();
                int value = ((MutableInteger) values.next()).get();

                MutableInteger foundWord = resultMap.get(key);

                if(foundWord == null){
                    resultMap.put(key, new MutableInteger(value));
                }else{
                    foundWord.addValue(value);
                }
            }
        }
    }
}
