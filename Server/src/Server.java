import java.awt.*;
import java.io.Console;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private static final int PORT = 5338;
    private static final int MAX = 10;

    public static void main(String[] args) throws IOException {
        Console console = System.console();
        if(console == null && !GraphicsEnvironment.isHeadless()){
            String jar;
            if (System.getProperty("user.dir").contains("artifacts")) jar = "Server.jar";
            else jar = "../out/artifacts/Server.jar";
            Runtime.getRuntime().exec(new String[]{"cmd","/c","start","cmd","/k","java -jar \"" + jar + "\""});
        }else{
            launch();
            System.out.println("Program has ended, please type 'exit' to close the console");
        }
    }

    private static void launch() {
        ClientHandler[] tab = new ClientHandler[MAX];
        ServerSocket ss;
        Socket s;

        try {
            ss = new ServerSocket(PORT, MAX);
            System.out.println("Server is listening at port " + ss.getLocalPort());
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        while (true) {
            try {
                s = ss.accept();
                try {
                    tab[addClient(s, tab)].start();
                } catch (IllegalThreadStateException e) {
                    System.out.println("Maximum number of clients reached: " + tab.length);
                }
            } catch (IOException e) {
                System.out.println("I/O error: " + e);
                return;
            }
        }
    }

    private static int addClient(Socket s, ClientHandler[] tab) throws IOException {
        DataInputStream dis = new DataInputStream(s.getInputStream());
        DataOutputStream dos = new DataOutputStream(s.getOutputStream());
        ClientHandler c = new ClientHandler(s, dis, dos, tab);
        int i = 0;
        while (c.getTabId() == -1 && i < tab.length) {
            if (tab[i] == null) {
                tab[i] = c;
                c.setTabId(i);
            }
            i++;
        }
        return c.getTabId();
    }
}
