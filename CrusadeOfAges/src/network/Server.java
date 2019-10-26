package network;

import engine.GameContainer;
import game.entity.PlayerMP;
import network.packets.Packet;
import network.packets.Packet00Login;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class Server extends Thread {

    private DatagramSocket socket;
    private GameContainer gc;
    private List<PlayerMP> connectedPlayers = new ArrayList<>();

    public Server(GameContainer gc) {
        this.gc = gc;
        try {
            this.socket = new DatagramSocket(5338);
        } catch(SocketException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        while (true) {
            byte[] data = new byte[1024];
            DatagramPacket packet = new DatagramPacket(data, data.length);
            try {
                socket.receive(packet);
            } catch(IOException e) {
                e.printStackTrace();
            }
            parsePacket(packet.getData(), packet.getAddress(), packet.getPort());
        }
    }

    private void parsePacket(byte[] data, InetAddress address, int port) {
        String msg = new String(data).trim();
        Packet.PacketTypes type = Packet.lookupPacket(msg.substring(0, 2));
        Packet packet = null;
        switch (type) {
            default:
            case INVALID:
                break;
            case LOGIN:
                packet = new Packet00Login(data);
                System.out.println("["+address.getHostAddress() + ":"+port+"] " + ((Packet00Login)packet).getUsername()
                        + " has connected...");
                PlayerMP player = new PlayerMP(((Packet00Login)packet).getUsername(), gc.getWorld(), address, port);
                addConnection(player, (Packet00Login)packet);
                gc.getGame().getLevel().addEntity(player);
                break;
            case DISCONNECT:
                break;
        }
    }

    public void addConnection(PlayerMP player, Packet00Login packet) {
        boolean connected = false;
        for (PlayerMP p : connectedPlayers) {
            if (player.getTag().equalsIgnoreCase(p.getTag())) {
                if (p.ipAddress == null) {
                    p.ipAddress = player.ipAddress;
                }
                if (p.port == -1) {
                    p.port = player.port;
                }
                connected = true;
            } else {
                sendData(packet.getData(), p.ipAddress, p.port);
                packet = new Packet00Login(p.getTag());
                sendData(packet.getData(), player.ipAddress, player.port);
            }
        }
        if (!connected) {
            connectedPlayers.add(player);
        }
    }

    public void sendData(byte[] data, InetAddress ipAddress, int port) {
        DatagramPacket packet = new DatagramPacket(data, data.length, ipAddress, port);
        try {
            socket.send(packet);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void sendDataToAllClients(byte[] data) {
        for (PlayerMP p : connectedPlayers) {
            sendData(data, p.ipAddress, p.port);
        }
    }
}
