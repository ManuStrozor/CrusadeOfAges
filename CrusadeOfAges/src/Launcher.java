import engine.*;
import game.Conf;
import exceptions.ConfException;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.net.Socket;
import java.net.ConnectException;
import java.io.IOException;

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
    public static void main(String[] args) {

        appdata = setAppdata();
        manageArgs(args);

        try {
            Settings settings = new Settings();

            Conf conf = new Conf(appdata);
            conf.initiate();
            conf.readSettings(settings);

            Socket socket = new Socket(host, port); // A déplacer dans Multiplayer (AbstractGame)

            GameContainer gc = new GameContainer(socket, settings);
            gc.setTitle(GAMENAME + " " + VERSION);
            gc.setScale(settings.getScale());
            gc.start();
        } catch (ConnectException e) {
            System.out.println("[ConnectException] Connexion refusée: " + host + ":" + port);
        } catch (ConfException e) {
            System.out.println("[ConfException] " + e.getMessage());
        } catch (IOException e) {
            System.out.println("[IOException] " + e.getMessage());
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
