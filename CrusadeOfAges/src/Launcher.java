import engine.*;
import exceptions.ConfException;
import game.Conf;
import game.GameManager;

import java.net.ConnectException;
import java.net.Socket;

public class Launcher {

    private static final String GAMENAME = "Crusade Of Ages";
    private static final String VERSION = "0.0.1";

    private static String host = "localhost";
    private static int port = 5338;
    private static String appdata;

    /**
     * Starts the game
     *
     * @param args [-c ConfigFolder] [-h Host] [-p Port]
     */
    public static void main(String[] args) throws Exception {

        appdata = setAppdata();
        manageArgs(args);

        Settings settings = new Settings();
        World world = new World();
        try {
            Conf conf = new Conf(appdata);
            conf.initiate();
            conf.readSettings(settings);
        } catch (ConfException e) {
            return;
        }

        try {
            Socket socket = new Socket(host, port);
            GameManager gm = new GameManager(socket, world);
            PlayerStats ps = new PlayerStats();
            GameContainer gc = new GameContainer(gm, settings, world, ps);
            gc.setTitle(GAMENAME + " " + VERSION);
            gc.setScale(settings.getScale());
            gc.start();
        } catch (ConnectException e) {
            System.out.println("Connection refused: " + host + ":" + port);
        }
    }

    private static void manageArgs(String[] args) {
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-c":
                    appdata = args[i + 1];
                    break;
                case "-h":
                    host = args[i + 1];
                    break;
                case "-p":
                    port = Integer.parseInt(args[i + 1]);
                    break;
            }
        }
    }

    private static String setAppdata() {
        String OS = (System.getProperty("os.name")).toUpperCase();
        if (OS.contains("WIN")) {
            return System.getenv("AppData");
        } else {
            return System.getProperty("user.dir");
        }
    }
}
