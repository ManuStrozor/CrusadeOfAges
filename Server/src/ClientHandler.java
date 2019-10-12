import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler extends Thread {

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
                if (received.equals("Exit")) {
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
        System.out.println(id + " " + msg);
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