package file.Main;

import file.Client.*;
import file.Server.*;
import java.util.Scanner;

public class Main {


    public static void main(String[] args) {
        switch (args[0]) {
            case "s" :
                Server serv = new Server();
                
                Thread thr = new Thread(serv);
                thr.start();
                
                while(true) {
                    Scanner in = new Scanner(System.in);
                    String tmp;
                    System.out.println("quit?");
                    tmp = in.next();
                    if ("quit".equals(tmp)) {
                        serv.close();
                        System.exit(0);
                        break;
                    }
                }
                break;
            case "c" :
                Client client = new Client();
                client.startClient();
                break;
            default:
                break;
        }
    }  
}
