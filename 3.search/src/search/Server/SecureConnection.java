package search.Server;

import search.Server.Database.WordDb;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;


public class SecureConnection implements Runnable {
    private Socket client;
    private WordDb db;
    private PrintWriter out = null;
    private BufferedReader in = null;
    
    
    public SecureConnection() {
        
    }
    public SecureConnection (Socket client, WordDb db) {
        this.client = client;
        this.db = db;
    }
    
    public void close() {
        
    }
    
    @Override
    public void run() {
        try {
            p("client thread");
            out = new PrintWriter(client.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            boolean run = true;
            
            File folder = new File("textFiles\\");
            if(!folder.exists()) {
                folder.mkdir();
            }
            while (run) {
                out.println("0;1");
                String request;
                while (true) {
                    if ( (request = in.readLine()) != null ) {
                        break;
                    }
                }
                request = request.trim();
                
                String[] tmp = request.split(";");
                
                switch (tmp[0]) {
                    case "1": // upload file to server
                        File[] listOfFiles = folder.listFiles();
                        
                        String[] tmp2 = tmp[1].split("\\\\");
                        
                        boolean faos = false;
                        for (File file : listOfFiles) {
                            if (file.isFile()) {
                                if (tmp2[tmp2.length-1].equals(file.getName())) {
                                    out.println("0;File already on server."); // file already on server
                                    faos = true;
                                }
                            }
                        }
                        if (faos) {
                            break;
                        }
                        
                        out.println("0;Beginning file transfer. Please wait...");
                        out.println("1;"+tmp[1]); // ready to recieve file info
                        
                        acceptFile(client, folder.getName(), tmp2[tmp2.length-1]);
                        
                        out.println("0;File uploaded.");
                        break;
                        
                    case "2": // send file to client
                        File[] list = folder.listFiles();
                        
                        boolean fileNotFound = true;
                        for (File file : list) {
                            if (file.isFile()) {
                                if (tmp[1].equals(file.getName())) {
                                    fileNotFound = false;
                                    break;
                                }
                            }
                        }
                        
                        if (fileNotFound) {
                            out.println("0;File not found on server, try again.");
                            break;
                        }
                        out.println("0;Sending file data, please wait...");
                        sendFile(client, folder.getName(), tmp[1]);
                        
                        out.println("0;File sent.");
                        break;
                        
                    case "3": // send file list to client
                        File[] browseList = folder.listFiles();
                        for (File file : browseList) {
                            if (file.isFile()) {
                                out.println("0;" + file.getName());
                            }
                        }
                        
                        break;
                        
                    case "4":
                        searchForFile(tmp[1]);
                        
                        break;
                        
                    case "5": // client wants to disconnect
                        try {
                            client.close();
                            run = false;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    default:
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } 
        finally {
            try {
                in.close();
                out.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    private void p(String msg) {
        System.out.println(msg);
    }
    
    private void acceptFile(Socket sock, String folder, String fileName) {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            
            folder = folder.concat("\\");
            folder = folder.concat(fileName);
            
            File file = new File(folder);
            if (file.createNewFile()) {
                
            }
            file.setWritable(true);
            
            BufferedWriter out = new BufferedWriter(new FileWriter(folder));
            String text;
            
            db.addFile(fileName);
            while(true) {
                text = in.readLine();
                text = text.replaceFirst("\\s+$", "");
                
                if ("1;0".equals(text)) 
                {
                    out.close();
                    break;
                } else if ("1;1".equals(text)) {
                    file.delete();
                }
                
                //String firstWord = text.split("[ \\t\\n\\,\\?\\;\\.\\:\\!]")[0];
                String txt = text.trim();
                String firstWord = txt.split("[ \\t\\n\\,\\?\\;\\.\\:\\!]")[0];
                db.addWordToFile(fileName, firstWord);
                
                out.write(text);
                out.newLine();
            }
            
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void sendFile (Socket sock, String folder, String fileName) {
        String file = (folder + "\\" + fileName);
        try {
            PrintWriter out = new PrintWriter(sock.getOutputStream(), true);
            BufferedReader inFile = new BufferedReader(new FileReader(file));
            
            out.println("2;" + fileName);
            
            String text;
            
            while ( (text = inFile.readLine()) != null ) {
                out.println(text);
            }
            
            out.println("2;1");
            
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    private void searchForFile(String word) {
        ArrayList<String> result;
        result = db.searchForWord(word);
        
        
        
        if (result.isEmpty()) {
            out.println("0;No files matching key words.");
            return;
        }
        
        for (String file : result) {
            out.println("0;"+file);
        }
    }
}
