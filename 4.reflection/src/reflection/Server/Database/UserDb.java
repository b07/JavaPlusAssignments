package reflection.Server.Database;

import java.io.Serializable;
import java.util.Map;
import java.util.HashMap;

public class UserDb implements Serializable{

    private static final long serialVersionUID = 3772117533959816263L;
    public Map db;
    
    public UserDb() {
        db = new HashMap();
    }
    
    @Override
    public String toString() {
        return (db.size() + " entries in DB\n");
    }
    
    public void addUser(String user, String pass) {
        db.put(user, pass);
    }
}

