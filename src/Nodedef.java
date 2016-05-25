/**
 * Created by harry7 on 25/5/16.
 * Created for DHT.
 */

import java.rmi.*;

public interface Nodedef extends Remote {
    String get(int id) throws Exception;
    void put(int key, String val) throws Exception;
}