import engine.*;
import game.Conf;
import exceptions.ConfException;

import java.io.IOException;

public class Launcher {

    private static final String GAMENAME = "Crusade Of Ages";
    private static final String VERSION = "0.1.0";
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

            GameContainer gc = new GameContainer(settings);
            gc.setTitle(GAMENAME + " " + VERSION);
            gc.setScale(settings.getScale());
            gc.start();
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
                case "--conf":
                    appdata = args[i + 1];
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
