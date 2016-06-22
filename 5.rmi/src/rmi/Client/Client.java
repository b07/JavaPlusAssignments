package rmi.Client;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Scanner;
import rmi.Server.AdminInt;

public class Client {
    private Socket mySocket;
    Scanner reader;
    PrintWriter out;
    BufferedReader in;
    
    private boolean admin;
    
    public Client() {
        this.admin = false;
    }
    
    public void p(String msg) {
        System.out.println(msg);
    }
    
    private void close() {
        try {
            mySocket.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public void startClient(){
        try {
            mySocket = new Socket();
            
            mySocket.connect(new InetSocketAddress(InetAddress.getLocalHost(), 9898));
            
           
            reader = new Scanner(System.in);
            out = new PrintWriter(mySocket.getOutputStream(),true);
            in = new BufferedReader(new InputStreamReader(mySocket.getInputStream()));
             
            //login
            String login = login();
            
            //start receiving thread
            if (!admin) {
                String[] name = login.split("\\*");
                new Thread(new Connection(mySocket, name[0])).start(); 
            }
            //
            if (admin) {
                adminMode();
            }
            
            
            //Main loop
            while (!admin) {
                String input;
                
                input = reader.nextLine();
                input = input.trim();
                
                String[] tmp;
                tmp = input.split(";");
                
                if (tmp.length != 2) {
                    p("Enter proper command.\n");
                    continue;
                }
                
                switch (tmp[0]) {
                    case "1":
                        File file = new File(tmp[1]);
                        if ((!file.exists()) | (file.length() == 0)) {
                            p("Incorrect path or file doesn't exist.");
                            break;
                        }
                        
                        out.println(input);
                        break;
                    case "2":
                        out.println(input);
                        break;
                    case "3":
                        out.println(input);
                        break;
                    case "4":
                        out.println(input);
                        break;
                    case "5":
                        out.print("4;quit\n");
                
                        try {
                            synchronized (this) {
                              wait(2000);  
                            }
                            
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                
                        reader.close();
                        out.close();
                        in.close();
                        
                        close();
                        System.exit(0);
                        break;
                    default:
                        p("Enter proper command.\n");
                        break;
                }
            }
            
            reader.close();
            out.close();
            in.close();
            
            close();
            System.exit(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        
    }
    
    String login () throws IOException {
        System.out.println("Enter credentials seperated by '*':\n"
                            + "Supports only letters and numbers!\n");
        boolean auth = true;
        String login;
        while (true) {
            if (!auth) {
                mySocket.close();
                out.close();
                in.close();

                mySocket = new Socket();
                mySocket.connect(new InetSocketAddress(InetAddress.getLocalHost(), 9898));

                out = new PrintWriter(mySocket.getOutputStream(),true);
                in = new BufferedReader(new InputStreamReader(mySocket.getInputStream()));
            }


            login = reader.nextLine();
            login = login.trim();

            String[] tmp;
            tmp = login.split("\\*");

            if (tmp.length != 2) {
                System.out.println("Enter proper credentials, please.");
                continue;
            } else if ((tmp[0].contains(";") | tmp[1].contains(";"))) {
                System.out.println("Enter proper credentials, please.");
                    continue;
            }

            out.println(login);
            String answer;

            while (true) {
                answer = in.readLine();
                if ( answer == null) {
                    continue;
                }
                break;
            }
            answer = answer.trim();
            tmp = answer.split(";");

            if (tmp.length != 2) {
                p("Unexpected answer from server");
            }
            auth = false;
            switch (tmp[0]) {
                case "8": // login successfull
                    if ("0".equals(tmp[1])) {
                        p("Successful login");
                    } else if ("1".equals(tmp[1])) {
                        p("User created. Login successful.");
                    } else if ("8".equals(tmp[1])) {
                        admin = true;
                        p("God mode activated. /Admin");
                    } else {
                        p("Unexpected answer from server");
                    }
                    auth = true;
                    break;
                case "6": // wrong password
                    p("Wrong password. If trying to create a user, it's probably taken.");
                    break;
                default:
                    System.out.println("Unexpected answer from server");
                    break;
            }
            if (auth) {
                break;
            }
        }
        
        return login;
    }
    
    void adminMode() {
        boolean quit = false;
        
        try {
            Registry registry = LocateRegistry.getRegistry(9696);
            AdminInt stub = (AdminInt) registry.lookup("AdminInt");
        
        
            while (!quit) {

                p("List users - 1;list");
                p("Delete user - 2;username");
                p("Change pass - 3;username*newpass");
                p("Quit - 4;quit");

                String input;

                input = reader.nextLine();
                input = input.trim();

                String[] tmp;
                tmp = input.split(";");

                if (tmp.length != 2) {
                    p("Enter proper command.\n");
                    continue;
                }

                switch (tmp[0]) {
                    case "1":
                        ArrayList<String> array = stub.listUsers();
                        
                        for (String user : array) {
                            p(user);
                        }
                        break;

                    case "2":
                        stub.deleteUser(tmp[1]);
                        break;

                    case "3":
                        String[] tmp2 = tmp[1].split("\\*");
                        stub.changePass(tmp2[0], tmp2[1]);
                        break;

                    case "4":
                        quit = true;
                        break;

                    default:
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}