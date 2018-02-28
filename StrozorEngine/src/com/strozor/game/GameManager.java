package com.strozor.game;

import com.strozor.engine.*;
import com.strozor.engine.gfx.*;

import javax.imageio.ImageIO;
import java.awt.event.KeyEvent;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class GameManager extends AbstractGame {

    public static final int TS = 32;
    public static int current = 0;
    public static final String APPDATA = System.getenv("APPDATA") + "\\.squaremonster";
    public static String[][] levels = {
            {"/levels/0.png", "Learn to jump"},
            {"/levels/1.png", "Trampoline room"},
            {"/levels/2.png", "Go on slowly..."},
            {"/levels/3.png", "Crown of thorns"}
    };
    public static String[] dataStates = {
            "Level up",
            "Death",
            "Game over",
            "Lever pulled",
            "Slime",
            "Jump"
    };

    private ArrayList<GameObject> objects = new ArrayList<>();
    private ArrayList<FlashNotif> notifs = new ArrayList<>();
    private Camera camera;
    private GameMap map;

    private GameManager(GameMap map) {
        this.map = map;
    }

    @Override
    public void update(GameContainer gc, float dt) {

        if(gc.getInput().isKeyDown(KeyEvent.VK_ESCAPE)) gc.setState(2);

        if(gc.getInput().isKeyDown(KeyEvent.VK_F12)) {
            try {
                DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");
                String filename = sdf.format(new Date()) + ".png";
                File out = new File(APPDATA + "\\screenshots\\" + filename);
                ImageIO.write(gc.getWindow().getImage(), "png", out);
                notifs.add(new FlashNotif(filename, 3, 100, -1));
            } catch(IOException e) {
                e.printStackTrace();
            }
        }

        //Notifications update
        for(int i = 0; i < notifs.size(); i++) {
            notifs.get(i).update(dt);
            if(notifs.get(i).isEnded()) notifs.remove(i);
        }

        //All objects update
        for(int i = 0; i < objects.size(); i++) {
            objects.get(i).update(gc, this, dt);
            if(objects.get(i).isDead()) {
                objects.remove(i);
                i--;
            }
        }
        //Reload level
        if(getObject("player") == null && (gc.getLastState() == 7 || gc.getLastState() == 0))
            load(levels[current][0]);

        camera.update(gc, this, dt);
    }

    @Override
    public void render(GameContainer gc, GameRender r) {
        camera.render(r);
        r.drawMap(map);
        if(gc.getSettings().isShowLights()) r.drawMapLights(map, new Light(80, 0xffffff00));
        for(GameObject obj : objects) obj.render(gc, r);
        for(FlashNotif notif : notifs) notif.render(gc, r);
    }

    public void load(String path) {
        map.init(new Image(path, false));
        objects.clear();
        objects.add(new Player("player", map, 1));
        camera = new Camera("player", map);
    }

    public GameObject getObject(String tag) {
        for(GameObject obj : objects) if(obj.getTag().equals(tag)) return obj;
        return null;
    }

    static private void writeAppData() {
        //.squaremonster
        File smFolder = new File(APPDATA);
        if(!smFolder.exists()) if(!smFolder.mkdir()) System.out.println("Dossier .squaremonster déjà existant");
        //assets
        File smAssets = new File(APPDATA + "\\assets");
        if(!smAssets.exists()) if(!smAssets.mkdir()) System.out.println("Dossier assets déjà existant");
        //objects.png (assets)
        try {
            File outObjs = new File(APPDATA + "\\assets\\objects.png");
            File outPl = new File(APPDATA + "\\assets\\player.png");
            if(!outObjs.exists())
                ImageIO.write(ImageIO.read(Image.class.getResourceAsStream("/objects.png")), "png", outObjs);
            if(!outPl.exists())
                ImageIO.write(ImageIO.read(Image.class.getResourceAsStream("/player.png")), "png", outPl);
        } catch(IOException e) {
            e.printStackTrace();
        }
        //screenshots
        File smScreenshots = new File(APPDATA + "\\screenshots");
        if(!smScreenshots.exists()) if(!smScreenshots.mkdir()) System.out.println("Dossier screenshots déjà existant");
        //creative_mode
        File smCrea = new File(APPDATA + "\\creative_mode");
        if(!smCrea.exists()) if(!smCrea.mkdir()) System.out.println("Dossier creative_mode déjà existant");
        //options.txt
        File smOptFile = new File(APPDATA + "\\options.txt");
        if(!smOptFile.exists()) {
            try {
                List<String> lines = Arrays.asList(
                        "lang:fr",
                        "guiScale:3",
                        "showFPS:false",
                        "showLights:true"
                );
                Path path = Paths.get(APPDATA + "\\options.txt");
                Files.write(path, lines, Charset.forName("UTF-8"));
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
        //player.dat
        File smStatsFile = new File(APPDATA + "\\player.dat");
        if(!smStatsFile.exists()) {
            try {
                Path path = Paths.get(APPDATA + "\\player.dat");
                Files.createFile(path);

                FileOutputStream fos = new FileOutputStream(APPDATA + "\\player.dat");
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                for(String ds : dataStates) {
                    oos.writeUTF(ds);
                    oos.writeInt(0);
                }
                oos.close();
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    static private void readOptions(Settings s) {
        try(BufferedReader br = new BufferedReader(new FileReader(APPDATA + "\\options.txt"))) {
            String line = br.readLine();
            while (line != null) {
                String[] sub = line.split(":");
                switch(sub[0]) {
                    case "lang":
                        switch(sub[1]) {
                            case "en": s.setLangIndex(0); break;
                            case "fr": s.setLangIndex(1); break;
                            default: s.setLangIndex(0); break;
                        }
                        break;
                    case "guiScale": s.setScale(Float.valueOf(sub[1])); break;
                    case "showFPS": s.setShowFps(sub[1].equals("true")); break;
                    case "showLights": s.setShowLights(sub[1].equals("true")); break;
                }
                line = br.readLine();
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Settings s = new Settings();
        GameMap map = new GameMap();
        writeAppData();
        readOptions(s);
        GameContainer gc = new GameContainer(new GameManager(map), s, map, new Data());
        gc.setTitle("Square Monster");
        gc.setScale(s.getScale());
        gc.start();
    }
}
