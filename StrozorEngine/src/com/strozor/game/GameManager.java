package com.strozor.game;

import com.strozor.engine.*;
import com.strozor.engine.gfx.*;
import com.strozor.view.Stats;

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
    public static final String APPDATA = System.getenv("APPDATA") + "\\.squaremonster";

    public static String[] dataStates = {
            "Door opened",
            "Lever actioned",
            "Game over",
            "Death",
            "Slime",
            "Jump",
            "Skull",
            "Coin",
            "Pill",
            "Key"
    };

    private ArrayList<GameObject> objects = new ArrayList<>();
    private ArrayList<FlashNotif> notifs = new ArrayList<>();
    private Camera camera;
    private GameMap gameMap;
    private int currLevel = 0;

    private String[] levelList = {
            "/levels/0.png",
            "/levels/1.png",
            "/levels/2.png"
    };

    private GameManager(GameMap gameMap) {
        this.gameMap = gameMap;
        load(levelList[currLevel]);
        objects.add(new Player("player", gameMap, 1));
        camera = new Camera("player", gameMap);
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

        //Animations
        gameMap.animate(dt * 3);

        //Reload level
        if(getObject("player") == null && (gc.getLastState() == 7 || gc.getLastState() == 0)) {

            load(levelList[currLevel]);

            objects.add(new Player("player", gameMap, 1));

            camera = null;
            camera = new Camera("player", gameMap);
        }

        camera.update(gc, this, dt);
    }

    @Override
    public void render(GameContainer gc, GameRender r) {
        camera.render(r);
        r.drawMap(gameMap);
        if(gc.getSettings().isShowLights())
            r.drawMapLights(gameMap, new Light(80, 0xffffff00));
        for(GameObject obj : objects) obj.render(gc, r);
        for(FlashNotif notif : notifs) notif.render(gc, r);
    }

    public void load(String path) {
        gameMap.init(new Image(path, false));
    }

    public int getCurrLevel() {
        return currLevel;
    }

    public void setCurrLevel(int currLevel) {
        this.currLevel = currLevel;
    }

    public String[] getLevelList() {
        return levelList;
    }

    public GameObject getObject(String tag) {
        for(GameObject obj : objects) if(obj.getTag().equals(tag)) return obj;
        return null;
    }

    static private void writeAppData() {
        //.squaremonster
        File smFolder = new File(APPDATA);
        if(!smFolder.exists()) smFolder.mkdir();
        //assets
        File smAssets = new File(APPDATA + "\\assets");
        if(!smAssets.exists()) smAssets.mkdir();
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
        if(!smScreenshots.exists()) smScreenshots.mkdir();
        //creative_mode
        File smCrea = new File(APPDATA + "\\creative_mode");
        if(!smCrea.exists()) smCrea.mkdir();
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
                for(int i = 0; i < dataStates.length; i++) {
                    oos.writeUTF(dataStates[i]);
                    oos.writeInt(0);
                }
                oos.close();
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    static private void readOptions(Settings settings) {
        try(BufferedReader br = new BufferedReader(new FileReader(APPDATA + "\\options.txt"))) {
            String line = br.readLine();
            while (line != null) {
                String[] sub = line.split(":");
                switch(sub[0]) {
                    case "lang":
                        switch(sub[1]) {
                            case "en": settings.setLangIndex(0); break;
                            case "fr": settings.setLangIndex(1); break;
                            default: settings.setLangIndex(0); break;
                        }
                        break;
                    case "guiScale": settings.setScale(Float.valueOf(sub[1])); break;
                    case "showFPS": settings.setShowFps(sub[1].equals("true")); break;
                    case "showLights": settings.setShowLights(sub[1].equals("true")); break;
                }
                line = br.readLine();
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Settings settings = new Settings();
        writeAppData();
        readOptions(settings);
        GameContainer gc = new GameContainer(new GameManager(new GameMap()), settings, new Data());
        gc.setTitle("Square Monster");
        gc.setScale(settings.getScale());
        gc.start();
    }
}
