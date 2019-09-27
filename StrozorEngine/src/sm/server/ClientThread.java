package sm.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;

public class ClientThread extends Thread {

    private Socket socket;
    private int id;

    ClientThread(Socket socket, int id) {
        this.socket = socket;
        this.id = id;
    }

    public void run() {
        InputStream in = null;
        BufferedReader bf = null;
        try {
            in = socket.getInputStream();
            bf = new BufferedReader(new InputStreamReader(in));
        } catch (IOException e) {
            return;
        }
        System.out.println("[client "+this.id+"] CONNECTED");

        String line;
        while (true) {
            try {
                line = bf.readLine();
                if (line != null) {
                    System.out.println("[client "+this.id+"] " + line);
                } else {
                    System.out.println("[client "+this.id+"] DISCONNECTED");
                    socket.close();
                    return;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
    }
}
