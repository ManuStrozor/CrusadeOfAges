package com.strozor.game;

import com.strozor.engine.AbstractGame;
import com.strozor.engine.GameContainer;
import com.strozor.engine.GameRender;
import com.strozor.engine.audio.SoundClip;
import com.strozor.engine.gfx.Image;
import com.strozor.engine.gfx.ImageTile;
import com.strozor.engine.gfx.Light;
import jdk.nashorn.internal.parser.JSONParser;
import org.json.JSONPointer;

import java.awt.event.KeyEvent;
import java.io.FileReader;
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

    private boolean[] solid;
    private int[] bloc;
    private int levelW, levelH;
    private int spawnX, spawnY;
    private int currLevel = 0;
    private float animTorch = 0, animCoin = 0;
    private String[] levelList = {
            "/level0.png",
            "/level1.png",
            "/level2.png",
            "/level3.png",
            "/level4.png"
    };

    private GameManager() {
        if(mapTester)
            loadLevel(mapTest);
        else
            loadLevel(levelList[currLevel]);

        gameOver = new SoundClip("/audio/gameover.wav");
        objects.add(new Player("player", spawnX, spawnY, 1));
        camera = new Camera("player");

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
        animTorch += dt * 3;
        animCoin += dt * 3;

        //Reload level
        if(getObject("player") == null && (gc.getLastState() == 7 || gc.getLastState() == 0)) {
            if(mapTester)
                loadLevel(mapTest);
            else
                loadLevel(levelList[currLevel]);

            gameOver.stop();
            objects.add(new Player("player", spawnX, spawnY, 1));

            camera = null;
            camera = new Camera("player");
        }

        camera.update(gc, this, dt);
    }

    @Override
    public void render(GameContainer gc, GameRender r) {
        camera.render(r);

        r.setAnimTorch(animTorch > 3 ? animTorch = 0 : animTorch);
        r.setAnimCoin(animCoin > 6 ? animCoin = 0 : animCoin);

        for(int y = 0; y < levelH; y++) {
            for(int x = 0; x < levelW; x++) {
                r.drawBloc(bloc[x + y * levelW], objectsImage, x * GameManager.TS, y * GameManager.TS, false);
                if(gc.getSettings().isShowLights() && bloc[x + y * levelW] == 11)
                    r.drawLight(lLamp, x * GameManager.TS + GameManager.TS / 2, y * GameManager.TS + GameManager.TS / 2);
            }
        }
        for(GameObject obj : objects) obj.render(gc, this, r);
    }

    public void loadLevel(String path) {

        Image levelImage = new Image(path, mapTester);
        levelW = levelImage.getW();
        levelH = levelImage.getH();
        solid = new boolean[levelW * levelH];
        bloc = new int[levelW * levelH];

        for(int y = 0; y < levelH; y++) {
            for(int x = 0; x < levelW; x++) {
                int index = x + y * levelW;
                switch(levelImage.getP()[index]) {
                    case 0xff00ff00://Spawn
                        spawnX = x;
                        spawnY = y;
                        break;
                    case 0xff000000://Floor
                        solid[index] = true;
                        bloc[index] = 1;
                        break;
                    case 0xffff648c://Heart
                        solid[index] = false;
                        bloc[index] = 2;
                        break;
                    case 0xffff0000://Bottom trap
                        solid[index] = false;
                        bloc[index] = 3;
                        break;
                    case 0xffff00ff://Top trap
                        solid[index] = false;
                        bloc[index] = 4;
                        break;
                    case 0xff0000ff://Key
                        solid[index] = false;
                        bloc[index] = 5;
                        break;
                    case 0xffff7700://Check point
                        solid[index] = false;
                        bloc[index] = 6;
                        break;
                    case 0xffffff00://Coin
                        solid[index] = false;
                        bloc[index] = 7;
                        break;
                    case 0xff00ffff://Torch
                        solid[index] = false;
                        bloc[index] = 11;
                        break;
                    case 0xff777777://Bouncing bloc
                        solid[index] = false;
                        bloc[index] = 12;
                        break;
                    case 0xff999999://Door
                        solid[index] = false;
                        bloc[index] = 13;
                        break;
                }
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

        for(GameObject obj : objects) {
            if(obj.getTag().equals(tag)) return obj;
        }
        return null;
    }

    public boolean getSolid(int x, int y) {
        return x < 0 || x >= levelW || y < 0 || y >= levelH || solid[x + y * levelW];
    }

    public int getBloc(int x, int y) {
        return bloc[x + y * levelW];
    }

    public void setBloc(int x, int y, int value) {
        bloc[x + y * levelW] = value;
    }

    public int getLevelW() {
        return levelW;
    }

    public int getLevelH() {
        return levelH;
    }

    public int getSpawnX() {
        return spawnX;
    }

    public int getSpawnY() {
        return spawnY;
    }

    public void setSpawnX(int spawnX) {
        this.spawnX = spawnX;
    }

    public void setSpawnY(int spawnY) {
        this.spawnY = spawnY;
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
        GameContainer gc = new GameContainer(new GameManager());
        gc.setTitle("Square Monster");
        gc.setScale(3f);
        gc.start();
    }
}
