package echo.Client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

public class Client {
    String myIdentity;
    SocketChannel mySocket;
    Selector selector;
    
    public Client(String clientIdentity) {
        myIdentity = clientIdentity;
    }
    
    public void talkToServer() {
        try {
            mySocket = SocketChannel.open();
            mySocket.configureBlocking(false);
            mySocket.connect(new InetSocketAddress(InetAddress.getLocalHost(), 9898));
        
            selector = Selector.open();
            mySocket.register(selector, SelectionKey.OP_CONNECT);
            
            while (selector.select() > 0) {
                Set keys = selector.selectedKeys();
                Iterator iterator = keys.iterator();
                
                while (iterator.hasNext()) {
                    SelectionKey key = (SelectionKey) iterator.next();
                    
                    SocketChannel myChannel = (SocketChannel) key.channel();
                    
                    iterator.remove();
                    
                    if (key.isConnectable()) {
                        if (myChannel.isConnectionPending()) {
                            myChannel.finishConnect();
                            System.out.println("Connection was pending but now has finished connecting.");
                        }
                        
                        ByteBuffer bb = null;
                        
                        while (true) {
                            String msg;
                            Scanner in = new Scanner(System.in);
                            
                            System.out.print("Enter echo message (type 'quit' to stop): ");
                            msg = in.nextLine();
                            
                            if ("quit".equals(msg)) {
                                bb = ByteBuffer.wrap(("quit").getBytes());
                                myChannel.write(bb);
                                bb.clear();
                                break;
                            }
                            
                            bb = ByteBuffer.wrap((myIdentity + ":" + msg).getBytes());
                            myChannel.write(bb);
                            bb.clear();
                            
                            while (myChannel.read(bb) < 1){};
                            
                            bb.flip();
                            byte[] array = new byte[bb.limit()];
                            bb.get(array);
                            System.out.println(new String(array));
                            bb.clear();
                        }
                    }
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
