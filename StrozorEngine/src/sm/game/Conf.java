package sm.game;

import sm.exceptions.ConfException;
import sm.engine.Settings;
import sm.engine.gfx.Image;

import javax.imageio.ImageIO;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class Conf {

    public static String[] STATS = {
            "Level up",
            "Death",
            "Game over",
            "Lever pulled",
            "Slime",
            "Jump"
    };
    public static String APPDATA;
    public static String SM_FOLDER;

    private File dirSM;
    private File dirAssets;

    public Conf(String path) {
        APPDATA = path;
        SM_FOLDER = null;
    }

    public void initiate() throws ConfException {
        dirSM = mkdirSM(new File(APPDATA));
        mkdirScreenshots();
        mkdirCreative_mode();
        initSettings();
        initPlayerStats();
        dirAssets = mkdirAssets();
        initAssets();
    }

    public void readSettings(Settings s) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(SM_FOLDER + "/settings.txt"));
            String line = br.readLine();
            while (line != null) {
                String[] sub = line.split(":");
                switch(sub[0]) {
                    case "lang": s.setFlag(sub[1]); break;
                    case "guiScale": s.setScale(Float.parseFloat(sub[1])); break;
                    case "showFPS": s.setShowFps(sub[1].equals("true")); break;
                    case "showLights": s.setShowLights(sub[1].equals("true")); break;
                }
                line = br.readLine();
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    private boolean initSettings() {
        File optTxt = new File(SM_FOLDER + "/settings.txt");
        if(!optTxt.exists() && dirSM.canWrite()) {
            try {
                List<String> lines = Arrays.asList(
                        "lang:fr",
                        "guiScale:3",
                        "showFPS:true",
                        "showLights:false");
                Path path = Paths.get(SM_FOLDER + "/settings.txt");
                Files.write(path, lines, StandardCharsets.UTF_8);
            } catch(IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    private boolean initPlayerStats() {
        File statsData = new File(SM_FOLDER + "/stats.dat");
        if(!statsData.exists() && dirSM.canWrite()) {
            try {
                Path path = Paths.get(SM_FOLDER + "/stats.dat");
                Files.createFile(path);
                FileOutputStream fos = new FileOutputStream(SM_FOLDER + "/stats.dat");
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                for(String ds : STATS) {
                    oos.writeUTF(ds);
                    oos.writeInt(0);
                }
                oos.close();
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    private boolean initAssets() {
        File objsImg = new File(SM_FOLDER + "/assets/objects.png");
        File playImg = new File(SM_FOLDER + "/assets/player.png");
        try {
            if(!objsImg.exists() && dirAssets.canWrite())
                ImageIO.write(ImageIO.read(Image.class.getResourceAsStream("/objects.png")), "png", objsImg);
            if(!playImg.exists() && dirAssets.canWrite())
                ImageIO.write(ImageIO.read(Image.class.getResourceAsStream("/player.png")), "png", playImg);
        } catch(IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private File mkdirSM(File appdata) throws ConfException {

        SM_FOLDER = APPDATA + "/.crusadeofages";
        File dirSM = new File(SM_FOLDER);
        if(!dirSM.exists()) {
            if (!appdata.canWrite()) {
                throw new ConfException("Unable to create the folder: .crusadeofages");
            } else if (!dirSM.mkdir()) {
                throw new ConfException("Folder already existing: .crusadeofages");
            }
        }
        return dirSM;
    }

    private File mkdirAssets() throws ConfException {
        File dirAssets = new File(SM_FOLDER + "/assets");
        if(!dirAssets.exists()) {
            if (!dirSM.canWrite()) {
                throw new ConfException("Unable to create the folder: assets");
            } else if(!dirAssets.mkdir()) {
                throw new ConfException("Folder already existing: assets");
            }
        }
        return dirAssets;
    }

    private void mkdirScreenshots() throws ConfException {
        File dirScreenshots = new File(SM_FOLDER + "/screenshots");
        if(!dirScreenshots.exists()) {
            if (!dirSM.canWrite()) {
                throw new ConfException("Unable to create the folder: screenshots");
            } else if (!dirScreenshots.mkdir()) {
                throw new ConfException("Folder already existing: screenshots");
            }
        }
    }

    private void mkdirCreative_mode() throws ConfException {
        File dirCreative_mode = new File(SM_FOLDER + "/creative_mode");
        if(!dirCreative_mode.exists()) {
            if (!dirSM.canWrite()) {
                throw new ConfException("Unable to create the folder: creative_mode");
            } else if (!dirCreative_mode.mkdir()) {
                throw new ConfException("Folder already existing: creative_mode");
            }
        }
    }
}
