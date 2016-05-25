/**
 * Created by harry7 on 25/5/16.
 * Created for DHT.
 */

import java.rmi.*;
;
import java.lang.*;

public class Node implements Nodedef {
    public int num;
    public String[] hm;

    public Node(int num) throws RemoteException {
        super();
        this.num = num;
        this.hm = new String[num];
    }

    public String get(int id) throws Exception {
        if (id < this.num && this.hm[id] != null) {
            return this.hm[id];
        } else {
            return "No Such Key Exists";
        }
    }

    public void put(int key, String val) throws Exception {
        this.hm[key] = val;
    }

}