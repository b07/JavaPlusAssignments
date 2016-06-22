package rmi.Server.Database;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class WordDb implements Serializable {
    private static final long serialVersionUID = 5909729410317636592L;
    
    public Map<String, ArrayList<String>> wordDb;
    
    public WordDb() {
        wordDb = new HashMap<String, ArrayList<String>>();
    }
    
    public ArrayList<String> searchForWord(String word) {
        ArrayList<String> result = new ArrayList<>();
        
        Iterator it = wordDb.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            
            ArrayList<String> tmp = (ArrayList<String>)pair.getValue();
            for (String value : tmp) {
                if ( value.equals(word)) {
                    result.add((String)pair.getKey());
                    break;
                }
            }
            
           // it.remove();
        }
        
        return result;
    }
    
    public void addFile(String name) {
        wordDb.put(name, new ArrayList<String>());
    }
    
    public void addWordToFile(String name, String word) {
        wordDb.get(name).add(word);
    }
    
    public void printDb() {
        Iterator it = wordDb.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            
            p((String)pair.getKey());
            
            ArrayList<String> tmp = (ArrayList<String>)pair.getValue();
            for (String value : tmp) {
                
                    p(value);
                
            }
            
            it.remove();
        }
    }
 
    private void p(String msg) {
        System.out.println(msg);
    }
}
