package echo.Main;
import echo.Client.Client;
import echo.Server.Server;
import java.lang.management.ManagementFactory;

public class Main {
    public static void main(String[] args) {
        if ( "1".equals(args[0])) {
            Server server = new Server();
            server.startListening();
        } else if ( "2".equals(args[0])) {
            Client client = new Client("Client/" + ManagementFactory.getRuntimeMXBean().getName());
            client.talkToServer();
        }
    }
}
