/**
 * Created by harry7 on 23/5/16.
 */


import java.rmi.*;

public interface Nodedef extends Remote {
    String get(int id) throws Exception;
    void put(int key,String val) throws Exception;
}
