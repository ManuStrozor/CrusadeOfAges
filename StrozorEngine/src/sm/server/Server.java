package sm.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {

    private static final int PORT = 5338;

    public static void main(String[] args) {
        ArrayList<ClientThread> ct = new ArrayList<>();
        ServerSocket ss;
        Socket s = null;

        try {
            ss = new ServerSocket(PORT, 10);
            System.out.println("Server is listening port " + ss.getLocalPort());
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        while (true) {
            try {
                s = ss.accept();
            } catch (IOException e) {
                System.out.println("I/O error: " + e);
            }
            ct.add(new ClientThread(s, ct.size()));
            ct.get(ct.size() - 1).start();
        }
    }
}
