
package rmi.Server;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import rmi.Server.Database.UserDb;

public class Admin implements AdminInt {

    private UserDb db;
    
    public Admin(UserDb db) {
        this.db = db;
    }
    
    @Override
    public ArrayList<String> listUsers() {
        ArrayList<String> result = new ArrayList<>();
        
        Iterator it = db.db.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            
            result.add((String)pair.getKey());
            
           // it.remove();
        }
        
        return result;
    }
    
    @Override
    public void deleteUser(String name) {
        db.db.remove(name);
    }
    
    @Override
    public void changePass(String name, String newPass) {
        Iterator it = db.db.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            
            if (pair.getKey().equals(name)) {
                pair.setValue(newPass);
                break;
            }
            
            it.remove();
        }
    }
}
