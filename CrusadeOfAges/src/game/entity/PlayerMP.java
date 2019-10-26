package game.entity;

import engine.World;

import java.net.InetAddress;

public class PlayerMP extends Player {

    public InetAddress ipAddress;
    public int port;

    public PlayerMP(String tag, World world, InetAddress ipAddress, int port) {
        super(tag, world);
        this.ipAddress = ipAddress;
        this.port = port;
    }
}
