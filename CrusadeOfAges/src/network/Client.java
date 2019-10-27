package network;

import engine.GameContainer;
import game.entity.PlayerMP;
import network.packets.Packet;
import network.packets.Packet00Login;

import java.io.IOException;
import java.net.*;

public class Client extends Thread {

    private InetAddress ipAddress;
    private String playerName;
    private DatagramSocket socket;
    private GameContainer gc;

    public Client(GameContainer gc, String ipAddress) {
        this.gc = gc;
        try {
            this.socket = new DatagramSocket();
            this.ipAddress = InetAddress.getByName(ipAddress);
        } catch(SocketException | UnknownHostException e) {
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
        Packet packet;
        switch (type) {
            default:
            case INVALID:
                break;
            case LOGIN:
                packet = new Packet00Login(data);
                System.out.println("["+address.getHostAddress() + ":"+port+"] " + ((Packet00Login)packet).getUsername()
                        + " has joined the game...");
                PlayerMP player = new PlayerMP(((Packet00Login)packet).getUsername(), gc.getWorld(), address, port);
                //gc.getGame().getLevel().addEntity(player);
                break;
            case DISCONNECT:
                break;
        }
    }

    public void sendData(byte[] data) {
        DatagramPacket packet = new DatagramPacket(data, data.length, ipAddress, 5338);
        try {
            socket.send(packet);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

}
