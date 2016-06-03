/**
 * Created by harry7 on 25/5/16.
 * Created for DHT.
 */

import java.rmi.*;

public interface Nodedef extends Remote {
    String get(String id) throws Exception;
    void put(String key, String val) throws Exception;
    void delete(String key) throws Exception;
}