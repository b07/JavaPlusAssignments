package rmi.Client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


public class Connection implements Runnable {
    Socket sock;
    String name;
    PrintWriter out;
    BufferedReader in;
    
    public Connection () {
        
    }
    
    public Connection(Socket client, String name) {
        this.sock = client;
        this.name = name;
    }
    
    
    @Override
    public void run() {
        File folder = new File(name + "\\");
        if(!folder.exists()) {
            folder.mkdir();
        }
        
        
        try {
            out = new PrintWriter(sock.getOutputStream(),true);
            in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            
            while(true) {
                String str = null;
                while (true) {
                    try {
                        if ( (str = in.readLine()) != null){
                            break;
                        } 
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                
                String[] tmp;
                tmp = str.split(";");
                
                switch (tmp[0]) {
                    
                    case "0":
                        if ("1".equals(tmp[1])) {
                            p("Enter command and arguments seperated by ;");
                            p("Upload file - 1;D:\\folder\\file.txt");
                            p("Download file - 2;file.txt");
                            p("Browse files - 3;browse");
                            p("Search files - 4;key-word");
                            p("To quit - 5;quit\n");
                            break;
                        }
                        p(tmp[1] + "\n");
                        break;
                        
                    case "1":
                        
                        File sizeVer = new File(tmp[1]);
                        if (sizeVer.length() > 50000000) {
                            p("File size too large, max size 50mb");
                            out.println("1;1");
                            break;
                        }
                        
                        String text;
                        BufferedReader inFile = new BufferedReader(new FileReader(tmp[1]));
                        while ( (text = inFile.readLine()) != null) {
                            out.println(text);
                        }            
                        out.println("1;0");
                        break;
                        
                    case "2":
                        File file = new File(name + "\\" + tmp[1]);
                        file.createNewFile();
                        file.setWritable(true);
                        BufferedWriter fileOut = new BufferedWriter(new FileWriter(file));
                        String data;
                        
                        while (true) { 
                            data = in.readLine();
                            data = data.replaceFirst("\\s+$", "");
                            
                            if ("2;1".equals(data)) {
                                fileOut.close();
                                break;
                            }
                            
                            fileOut.write(data);
                            fileOut.newLine();
                        }
                        
                        break;
                    case "3":
                        
                        break;
                    case "4":
                        
                        break;
                    default:
                }
            }
        } catch (IOException ex) {
            System.exit(0);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                System.exit(0);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    private void p(String msg) {
        System.out.println(msg);
    }
}
