package sm.network;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private static final int PORT = 5338;
    private static final int MAX = 10;

    public static void main(String[] args) {

        ClientHandler[] tab = new ClientHandler[MAX];
        ServerSocket ss;
        Socket s;

        try {
            ss = new ServerSocket(PORT, MAX);
            System.out.println("Server is listening at port [" + ss.getLocalPort() + "]");
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

class ClientHandler extends Thread {

    private ClientHandler[] tab;
    private Socket socket;
    private DataInputStream dis;
    private DataOutputStream dos;
    private int id;

    ClientHandler(Socket socket, DataInputStream dis, DataOutputStream dos, ClientHandler[] tab) {
        this.socket = socket;
        this.dis = dis;
        this.dos = dos;
        this.tab = tab;
        id = -1;
    }

    @Override
    public void run() {
        print(id, "CONNECTED");

        String received;
        while (true) {
            try {
                received = dis.readUTF();
                print(id, received);
                int i = 0;
                while (tab[i] != null) {
                    tab[i++].dos.writeUTF(received);
                }
                if(received.equals("Exit")) {
                    remove(id, socket);
                    break;
                }
            } catch (IOException e) {
                remove(id);
                return;
            }
        }

        try {
            dis.close();
            dos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void print(int id, String msg) {
        System.out.println("["+id+":" + msg);
    }

    private void remove(int id, Socket s) throws IOException {
        print(id, "DISCONNECTED");
        tab[id] = null;
        s.close();
    }

    private void remove(int id) {
        print(id, "DISCONNECTED");
        tab[id] = null;
    }

    void setTabId(int id) {
        this.id = id;
    }

    int getTabId() {
        return id;
    }

}
