/**
 * Created by harry7 on 25/5/16.
 * Created for DHT.
 */

import java.rmi.*;

import java.lang.*;
import java.util.*;

public class Node implements Nodedef {
    public HashMap<String,String> hm;

    public Node() throws RemoteException {
        super();
        this.hm = new HashMap();
    }

    public String get(String id) throws Exception {
        if (this.hm.get(id) != null) {
            return this.hm.get(id);
        } else {
            return "No Such Key Exists";
        }
    }
    public void put(String key, String val) throws Exception {
        this.hm.put(key,val);
    }
}