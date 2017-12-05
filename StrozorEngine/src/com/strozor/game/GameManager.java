package com.strozor.game;

import com.strozor.engine.AbstractGame;
import com.strozor.engine.GameContainer;
import com.strozor.engine.GameRender;
import com.strozor.engine.Settings;
import com.strozor.engine.audio.SoundClip;
import com.strozor.engine.gfx.Image;
import com.strozor.engine.gfx.Light;
import com.strozor.engine.gfx.Map;

import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GameManager extends AbstractGame {

    public static final int TS = 32;

    public static boolean mapTester = false;
    public static String mapTest;

    private ArrayList<GameObject> objects = new ArrayList<>();
    private Camera camera;
    private Light lPlayer;
    private SoundClip gameOver;

    private Map map;
    private int currLevel = 0;
    private String[] levelList = {
            "/levels/0.png",
            "/levels/1.png"
    };

    private GameManager(Map map) {
        this.map = map;

        if(mapTester)
            load(mapTest);
        else
            load(levelList[currLevel]);

        gameOver = new SoundClip("/audio/gameover.wav");
        objects.add(new Player("player", map, 1));
        camera = new Camera("player", map);

        lPlayer = new Light(100, -1);
    }

    @Override
    public void update(GameContainer gc, float dt) {

        if(gc.getInput().isKeyDown(KeyEvent.VK_ESCAPE)) gc.setState(2);

        for(int i = 0; i < objects.size(); i++) {
            objects.get(i).update(gc, this, dt);
            if(objects.get(i).isDead()) {
                objects.remove(i);
                i--;
            }
        }

        //Animations
        map.animate(dt * 3);

        //Reload level
        if(getObject("player") == null && (gc.getLastState() == 7 || gc.getLastState() == 0)) {
            if(mapTester)
                load(mapTest);
            else
                load(levelList[currLevel]);

            gameOver.stop();
            objects.add(new Player("player", map, 1));

            camera = null;
            camera = new Camera("player", map);
        }

        camera.update(gc, this, dt);
    }

    @Override
    public void render(GameContainer gc, GameRender r) {
        camera.render(r);
        r.drawMap(map);
        if(gc.getSettings().isShowLights())
            r.drawMapLights(map, new Light(80, 0xffffff00));
        for(GameObject obj : objects) obj.render(gc, this, r);
    }

    public void load(String path) {
        Image img = new Image(path, mapTester);
        map.initMap(img.getW(), img.getH());
        for(int y = 0; y < map.getHeight(); y++) {
            for(int x = 0; x < map.getWidth(); x++) {
                map.initBloc(x, y, img.getP()[x + y * map.getWidth()]);
            }
        }
    }

    public Light getlPlayer() {
        return lPlayer;
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

    public SoundClip getGameOver() {
        return gameOver;
    }

    public boolean isMapTesting() {
        return mapTester;
    }

    public static String getMapTest() {
        return mapTest;
    }

    static void writeAppData(String appdata) {
        File smFolder = new File(appdata);
        if(!smFolder.exists())
            smFolder.mkdir();
        File smOptFile = new File(appdata + "\\options.txt");
        if(!smOptFile.exists()) {
            try {
                List<String> lines = Arrays.asList(
                        "gameLang:en",
                        "showFPS:true",
                        "showLights:false"
                );
                Path file = Paths.get(appdata + "\\options.txt");
                Files.write(file, lines, Charset.forName("UTF-8"));
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
    }

    static void readAppData(Settings settings, String appdata) {
        try(BufferedReader br = new BufferedReader(new FileReader(appdata + "\\options.txt"))) {
            String line = br.readLine();
            while (line != null) {
                String[] sub = line.split(":");
                switch(sub[0]) {
                    case "gameLang":
                        switch(sub[1]) {
                            case "en": settings.setLangIndex(0); break;
                            case "fr": settings.setLangIndex(1); break;
                            default: settings.setLangIndex(0); break;
                        }
                        break;
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
        String appdata = System.getenv("APPDATA") + "\\.squaremonster";
        writeAppData(appdata);
        readAppData(settings, appdata);
        if(args.length == 1) {
            mapTester = true;
            mapTest = args[0];
        }
        GameContainer gc = new GameContainer(new GameManager(new Map()), settings);
        gc.setTitle("Square Monster");
        gc.setScale(3f);
        gc.start();
    }
}
