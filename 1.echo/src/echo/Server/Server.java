package echo.Server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;


public class Server {
    ServerSocketChannel serverSocketChannel;
    Selector selector;
    
    public Server() {
        try {
            selector = Selector.open();
            
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            
            InetSocketAddress add = new InetSocketAddress(InetAddress.getLocalHost(), 9898);
            serverSocketChannel.socket().bind(add);
            
            SelectionKey key = serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("serverSocketChannel's registered key is: " + key.channel().toString());
            System.out.println();
            
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public void startListening() {
        System.out.println("Server is listening on: "
            + serverSocketChannel.socket().getInetAddress().getHostAddress() + ":"
            + serverSocketChannel.socket().getLocalPort());
        
        while(true) {
            try {
                selector.select();
                
                Set selectedKeys = selector.selectedKeys();
                Iterator iterator = selectedKeys.iterator();
                
                while (iterator.hasNext()) {
                    SelectionKey key = (SelectionKey) iterator.next();
                    
                    iterator.remove();
                    
                    //Client has asked for a connection
                    if (key.isAcceptable()) {
                        System.out.println("Key is read to perform accept() : " + key.channel().toString());
                        
                        SocketChannel client = serverSocketChannel.accept();
                        client.configureBlocking(false);
                        
                        client.register(selector, SelectionKey.OP_READ);
                        continue;
                    }
                    
                    //Client has sent data to the server
                    if (key.isReadable()) {
                        //System.out.println("Key ready to perform read() " + key.channel().toString());
                        
                        SocketChannel client = (SocketChannel) key.channel();
                        ByteBuffer bb = ByteBuffer.allocate(1024);
                        
                        client.read(bb);
                        
                        bb.flip();
                        byte[] array = new byte[bb.limit()];
                        bb.get(array);
                        String str = new String(array);
                        if ("quit".equals(str)) {
                            client.close();
                            break;
                        }
                        System.out.println(str);
                        
                        
                        String[] tmp;
                        tmp = str.split(":");
                        
                        bb = ByteBuffer.wrap(tmp[1].getBytes());
                        client.write(bb);
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
