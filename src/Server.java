
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

    private static int fill_registry(Nodedef[] reg) throws Exception {
        int myindex = 0;
        String myip = get_my_ip();
        try (BufferedReader br = new BufferedReader(new FileReader("config.txt"))) {
            java.lang.String line;
            while ((line = br.readLine()) != null) {
                String[] toks = line.trim().split("\\s+");
                int index = Integer.parseInt(toks[0]);
                int port = Integer.parseInt(toks[2]);
                if (toks[1].equals(myip)) {
                    Registry temp = LocateRegistry.getRegistry(port);
                    myindex = index;
                } else {
                    Registry temp = LocateRegistry.getRegistry(toks[1], port);
                    reg[index] = (Nodedef)temp.lookup("node");
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
            //op.println();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }

        Nodedef[] reg = new Nodedef[num];
        int sic=0,ric=0,ssc=0,rsc=0,sdc=0,rdc=0;
        float sit=0,rit=0,sst=0,rst=0,sdt=0,rdt=0;
        // Take Input and perform Actions
        for (; ; ) {

            /*if (filled == 0)
                op.println("1 - Initialise");
            op.println("2 - Insert a Word into DHT");
            op.println("3 - Look up for a Word in DHT");
            op.println("4 - Exit");
            op.println();
            op.print("Choice : ");
*/
            String line = input.nextLine();
           // op.println();

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
                //op.print("Enter key: ");
                key = Integer.parseInt(input.nextLine());
                //op.print("Enter String: ");
                val = input.nextLine();
                int fir = key % num;
                key /= num;
                key1 = Integer.toString(key);
                try {
                    if (fir != myindex) {
                        ric+=1;
                        double startTime = System.nanoTime();
                        reg[fir].put(key1, val);
                        double endTime = System.nanoTime();
                        System.out.println("Insert on Remote Took "+(endTime - startTime)/1000 + " microsec");
                        rit+=((endTime - startTime)/1000);
                    } else {
                        sic+=1;
                        double startTime = System.nanoTime();
                        node.put(key1, val);
                        double endTime = System.nanoTime();
                        System.out.println("Insert on Self Took "+(endTime - startTime)/1000 + " microsec");
                        sit+=((endTime - startTime)/1000);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (line.equals("3")) {
                int key;
                String key1;
                //op.print("Enter key: ");
                key = Integer.parseInt(input.nextLine());
                int fir = key % num;
                key /= num;
                key1 = Integer.toString(key);

                if (fir != myindex) {
                    rsc+=1;
                    double startTime = System.nanoTime();

                    op.println(reg[fir].get(key1));
                    double endTime = System.nanoTime();
                    System.out.println("Search on Remote Took "+(endTime - startTime)/1000 + " microsec");
                    rst+=((endTime - startTime)/1000);
                } else {
                    ssc+=1;
                    double startTime = System.nanoTime();
                    op.println(node.get(key1));
                    double endTime = System.nanoTime();
                    System.out.println("Search on Self Took "+(endTime - startTime)/1000 + " microsec");
                    sst+=((endTime - startTime)/1000);
                }

            } else if (line.equals("4")) {
                int key;
                String key1;
                //op.print("Enter key: ");
                key = Integer.parseInt(input.nextLine());
                int fir = key % num;
                key /= num;
                key1 = Integer.toString(key);

                if (fir != myindex) {
                    rdc+=1;
                    double startTime = System.nanoTime();
                    reg[fir].delete(key1);
                    double endTime = System.nanoTime();
                    System.out.println("Delete on Remote Took "+(endTime - startTime)/1000 + " microsec");
                    rdt+=((endTime - startTime)/1000);
                } else {
                    sdc+=1;
                    double startTime = System.nanoTime();
                    node.delete(key1);
                    double endTime = System.nanoTime();
                    System.out.println("Delete on Self Took "+(endTime - startTime)/1000 + " microsec");
                    sdt+=((endTime - startTime)/1000);
                }
            } else if (line.equals("5")) {

                op.println("Final Results are");
                op.println("Average Insert time on Self - "+(sit/sic)+"for "+sic+" insertions");
                op.println("Average Insert time on Remote - "+(rit/ric)+"for "+ric+" insertions");
                op.println("Average Search time  on Self - "+(sst/ssc)+"for "+ssc+" searches");
                op.println("Average Search time on Remote - "+(rst/rsc)+"for "+rsc+" searches");
                op.println("Average Delete time on Self - "+(sdt/sdc)+"for "+sdc+" deletions");
                op.println("Average Delete time on Remote - "+(rdt/rdc)+"for "+rdc+" deletions");

                op.println("Exitting Now. Bye");
                System.exit(0);
            } else {
                op.println("Wrong Option");
            }
 //           op.println();
        }
    }
}