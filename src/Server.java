
/**
 * Created by harry7 on 25/5/16.
 * Created for DHT.
 */


import java.net.*;
import java.util.*;
import java.rmi.registry.*;
import java.io.*;
import java.rmi.*;
import java.rmi.server.*;


public class Server {

    /* Provides Ip address of Machine */

    private static String get_my_ip() throws Exception {
        try {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /* Initialises Registries by getting remote registries */

    private static int fill_registry(Registry[] reg) throws Exception {
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

    /* Main code which runs the code */

    public static void main(String[] args) throws Exception {

        Scanner input = new Scanner(System.in);
        PrintStream op = System.out;

        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new RMISecurityManager());
        }

        // Filling port myindex and create a registry

        int num = 0, myindex = 0, filled = 0;
        String myip = get_my_ip();

        /* Setting this property so that remote machines can connect */
        System.setProperty("java.rmi.server.hostname", myip);

        /* Getting the total number of machines and port number to create a registry */

        Node node = new Node();

        try (BufferedReader br = new BufferedReader(new FileReader("config.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {

                String[] toks = line.trim().split("\\s+");
                int index = Integer.parseInt(toks[0]);
                int port = Integer.parseInt(toks[2]);
                if (toks[1].equals(myip)) {
                    myindex = index;
                }
                num = index + 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Nodedef stub = (Nodedef) UnicastRemoteObject.exportObject(node, 0);
            Naming.rebind("node", stub);
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
            if (filled == 0 && line.equals("1")) {
                myindex = fill_registry(reg);
                op.println("Initialised successfully");
                filled = 1;
            } else if (line.equals("2")) {
                String val,key1;
                int key;
                op.print("Enter key: ");
                key = Integer.parseInt(input.nextLine());
                op.print("Enter String: ");
                val = input.nextLine();
                int fir = key % num;
                key /= num;
                key1 = Integer.toString(key);
                try {
                    if (fir != myindex) {
                        Nodedef stub = (Nodedef) reg[fir].lookup("node");
                        System.out.println("Got the Stub Attempting call now");
                        stub.put(key1, val);
                    } else
                        node.put(key1, val);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (line.equals("3")) {
                int key;
                String key1;
                op.print("Enter key: ");
                key = Integer.parseInt(input.nextLine());
                int fir = key % num;
                key /= num;
                key1 = Integer.toString(key);
                if (fir != myindex) {
                    Nodedef stub = (Nodedef) reg[fir].lookup("node");
                    op.println(stub.get(key1));
                } else {
                    op.println(node.get(key1));
                }
            } else if (line.equals("4")) {
                System.exit(0);
            } else {
                op.println("Wrong Option");
            }
            op.println();
        }
    }
}
