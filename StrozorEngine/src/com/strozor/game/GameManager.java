package com.strozor.game;

import com.strozor.engine.AbstractGame;
import com.strozor.engine.GameContainer;
import com.strozor.engine.GameRender;
import com.strozor.engine.audio.SoundClip;
import com.strozor.engine.gfx.Image;
import com.strozor.engine.gfx.ImageTile;
import com.strozor.engine.gfx.Light;
import com.strozor.engine.gfx.Map;

import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class GameManager extends AbstractGame {

    public static final int TS = 16;

    public static boolean mapTester = false;
    public static String mapTest;

    private ImageTile objectsImage = new ImageTile("/objects.png", TS, TS);
    private ArrayList<GameObject> objects = new ArrayList<>();
    private Camera camera;
    private Light lLamp, lPlayer;
    private SoundClip gameOver;

    private Map map;
    private int currLevel = 0;
    private String[] levelList = {
            "/levels/level0.png",
            "/levels/level1.png",
            "/levels/level2.png",
            "/levels/level3.png",
            "/levels/level4.png"
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

        lLamp = new Light(70, -1);
        lPlayer = new Light(150, -1);
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
            r.drawMapLights(map, lLamp);
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

    public ImageTile getObjectsImage() {
        return objectsImage;
    }

    public boolean isMapTesting() {
        return mapTester;
    }

    public static String getMapTest() {
        return mapTest;
    }

    public static void main(String[] args) {
        if(args.length == 1) {
            mapTester = true;
            mapTest = args[0];
        }
        GameContainer gc = new GameContainer(new GameManager(new Map()));
        gc.setTitle("Square Monster");
        gc.setScale(3f);
        gc.start();
    }
}
