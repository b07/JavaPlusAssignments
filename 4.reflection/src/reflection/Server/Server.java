package reflection.Server;

import reflection.Server.Database.UserDb;
import reflection.Server.Database.SerUtil;
import reflection.Server.Database.WordDb;
import java.io.BufferedReader;
import java.io.File;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import reflection.Main.Main;


public class Server implements Runnable {
    private ServerSocket serverSocket;
    
    private UserDb db;
    private final String serFileDb = "tmp\\userDb.ser";
    
    private WordDb wordDb;
    private final String serFileWordDb = "tmp\\wordDb.ser";
    
    
    private ExecutorService executor;
    
    private Class aClass;
    
    public Server() {
        
    }
    
    private void loadDb() {
        try {
            File folder = new File("tmp\\");
            folder.mkdir();
            File tmp = new File(serFileDb);
            if(!tmp.exists() || tmp.length() == 0) {
                tmp.createNewFile();
                db = new UserDb();
                return;
            }
            
            db = (UserDb) SerUtil.deserialize(serFileDb);
        } catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }
    
    private void loadWordDb() {
        try {
            File tmp = new File(serFileWordDb);
            if (!tmp.exists() || tmp.length() == 0) {
                tmp.createNewFile();
                wordDb = new WordDb();
                return;
            }
            wordDb = (WordDb) SerUtil.deserialize(serFileWordDb);
        } catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }
    
    private void loadClass() {
        try { 
            ClassLoader classLoader = Main.class.getClassLoader();
            
            aClass = classLoader.loadClass("bg.nbu.java.server.plugin.IndexService");
            p(aClass.getName());
        } catch (ClassNotFoundException ex) {
            
        }
    }
    
    public void close() {
        try {
            serverSocket.close();
            
            SerUtil.serialize(db, serFileDb);
            SerUtil.serialize(wordDb, serFileWordDb);
            executor.shutdown();
            
            System.out.println("closing");
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }
    
    @Override
    public void run() {
        
        try {
            serverSocket = new ServerSocket();  
            serverSocket.bind(new InetSocketAddress(InetAddress.getLocalHost(), 9898));
            loadDb();
            loadWordDb();
            wordDb.printDb();
            
            loadClass();

            System.out.println(db.toString());
            
            if(wordDb.wordDb.isEmpty()) {
                p("COCK");
            }
            
            for (Object dbKey : db.db.keySet()) {
                System.out.print(dbKey + "-" + db.db.get(dbKey));
            }
            System.out.println();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        
        System.out.println("Server is listening on: "
            + serverSocket.getInetAddress() + ":"
            + serverSocket.getLocalPort());
        executor = Executors.newFixedThreadPool(10);
        Socket clientSocket;
        while (true) {
            try {
                clientSocket = serverSocket.accept();
                
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String login;
                while (true) {
                    
                    if ( (login = in.readLine()) != null) {
                        break;
                    }
                }
                login = login.trim();
                
                String[] tmp = login.split("\\*");
                if (tmp.length != 2) {
                    p("Unexpected answer from client");
                    clientSocket.close();
                    continue;
                }
                
                if (db.db.containsKey(tmp[0])) { // user present
                    if (!( tmp[1].equals(db.db.get(tmp[0])) ) ) { // password incorrect
                        out.println("6;0");
                        clientSocket.close();
                        continue;
                    } else { // password correct
                        out.println("8;0");
                    }
                } else { // no such user, create one
                    db.db.put(tmp[0], tmp[1]);
                    out.println("8;1");
                }
                executor.execute(new SecureConnection(clientSocket, wordDb));
            } catch (IOException e){
               break;
            }
        }
        
    }
    
    private void p(String msg) {
        System.out.println(msg);
    }
}