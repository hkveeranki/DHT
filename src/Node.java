/**
 * Created by harry7 on 23/5/16.
 */


import java.io.*;
import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.UnicastRemoteObject;
import java.net.*;
import java.util.*;
import java.lang.*;

public class Node extends UnicastRemoteObject implements Nodedef {
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

    private String get_my_ip() throws Exception {
        Enumeration e = NetworkInterface.getNetworkInterfaces();
        while (e.hasMoreElements()) {
            NetworkInterface n = (NetworkInterface) e.nextElement();
            Enumeration ee = n.getInetAddresses();
            while (ee.hasMoreElements()) {
                InetAddress i = (InetAddress) ee.nextElement();
                if (i instanceof Inet4Address && !(i.isLoopbackAddress())) {
                    return i.getHostAddress();
                }
            }
        }
        return null;
    }

    public int fill_registry(Registry[] reg) throws Exception {
        int myindex = 0;
        String myip = get_my_ip();
        try (BufferedReader br = new BufferedReader(new FileReader("config.txt"))) {
            java.lang.String line;
            while ((line = br.readLine()) != null) {
                String[] toks = line.trim().split("\\s+");
                int index = Integer.parseInt(toks[0]);
                int port = Integer.parseInt(toks[2]);
                if (toks[1].equals(myip)) {
                    reg[index] = LocateRegistry.getRegistry(port);
                    myindex = index;
                } else {
                    reg[index] = LocateRegistry.getRegistry(toks[1], port);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return myindex;
    }

    public static void main(String[] args) throws Exception {

        Scanner input = new Scanner(System.in);
        PrintStream op = System.out;

        Node node = new Node(8);
        Registry myreg;

        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new RMISecurityManager());
        }

        // Filling port myindex and create a registry

        int num = 0, myport = 1099, myindex = 0, filled = 0;
        String myip = node.get_my_ip();

        try (BufferedReader br = new BufferedReader(new FileReader("config.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {

                String[] toks = line.trim().split("\\s+");
                int index = Integer.parseInt(toks[0]);
                int port = Integer.parseInt(toks[2]);
                if (toks[1].equals(myip)) {
                    myport = port;
                    myindex = index;
                }
                num = index + 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            myreg = LocateRegistry.createRegistry(myport);
            myreg.bind("node", node);
            op.println("Created Registry");
            op.println();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }

        Registry[] reg = new Registry[num];
        // Take Input and perform Actions
        for (; ; ) {
            if (filled == 0)
                op.println("1 - Initialise");
            op.println("2 - Insert a Word into DHT");
            op.println("3 - Look up for a Word in DHT");
            op.println("4 - Exit");
            op.println();
            op.print("Choice : ");

            String line = input.nextLine();
            if (filled == 0 && !line.equals("1") && !line.equals("4")) {
                op.println("Initialise First");
                continue;
            }
            if (filled == 0 && line.equals("1") ) {
                myindex = node.fill_registry(reg);
                op.println("Initialised successfully");
                filled = 1;
            } else if (line.equals("2")) {
                int key;
                String val;
                op.print("Enter key: ");
                key = Integer.parseInt(input.nextLine());
                op.print("Enter String: ");
                val = input.nextLine();
                int fir = key % num;
                key /= num;
                //op.println("Fir is "+fir+" key is "+key);
                try {
                    if (fir != myindex) {
                        Nodedef stub = (Node) reg[fir].lookup("node");
                        stub.put(key, val);
                    }
                    else
                        node.put(key,val);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (line.equals("3")) {
                int key;
                op.print("Enter key: ");
                key = Integer.parseInt(input.nextLine());
                int fir = key % num;
                key /= num;
                if (fir != myindex) {
                    Node stub = (Node) reg[fir].lookup("node");
                    op.println(stub.get(key));
                } else {
                    op.println(node.get(key));
                }
            } else if (line.equals("4")) {
                System.exit(0);
            } else {
                op.println("Wrong Option");
            }
        }
    }
}