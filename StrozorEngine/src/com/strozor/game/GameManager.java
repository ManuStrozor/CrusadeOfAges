package com.strozor.game;

import com.strozor.engine.AbstractGame;
import com.strozor.engine.GameContainer;
import com.strozor.engine.GameRender;
import com.strozor.engine.audio.SoundClip;
import com.strozor.engine.gfx.Image;
import com.strozor.engine.gfx.ImageTile;
import com.strozor.engine.gfx.Light;

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

    private boolean[] solid;
    private int[] bloc;
    private int levelW, levelH;
    private int spawnX, spawnY;
    private int currLevel = 0;
    private float animFire = 0, animCoin = 0;
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

        //Torch's animation
        animFire += dt * 3;
        if(animFire > 3) animFire = 0;

        //Coin's animation
        animCoin += dt * 3;
        if(animCoin > 6) animCoin = 0;

        //Reload after Death
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
        for(int y = 0; y < levelH; y++) {
            for(int x = 0; x < levelW; x++) {
                int index = x + y * levelW;
                if(bloc[index] != 1)
                    r.drawImageTile(objectsImage, x * TS, y * TS, 1, 0);
                switch(bloc[index]) {
                    case 1: r.drawImageTile(objectsImage, x * TS, y * TS, 0, 0); break;
                    case 2: r.drawImageTile(objectsImage, x * TS, y * TS, 3, 2); break;
                    case 3: r.drawImageTile(objectsImage, x * TS, y * TS, 1, 1); break;
                    case 4: r.drawImageTile(objectsImage, x * TS, y * TS, 2, 1); break;
                    case 5:
                        r.drawImageTile(objectsImage, x * TS, y * TS, 3, 1);
                        break;
                    case 6: r.drawImageTile(objectsImage, x * TS, y * TS, 3, 0); break;
                    case 7: r.drawImageTile(objectsImage, x * TS, y * TS, 5, (int)animCoin); break;
                    case 8: r.drawImageTile(objectsImage, x * TS, y * TS, 0, 1); break;
                    case 9: r.drawImageTile(objectsImage, x * TS, y * TS, 1, 2); break;
                    case 10: r.drawImageTile(objectsImage, x * TS, y * TS, 2, 2); break;
                    case 11:
                        r.drawImageTile(objectsImage, x * TS, y * TS, 4, (int)animFire);
                        if(gc.getSettings().isShowLights())
                            r.drawLight(lLamp, x * TS + TS / 2, y * TS + TS / 2);
                        break;
                    case 12: r.drawImageTile(objectsImage, x * TS, y * TS, 0, 2); break;
                    case 13:
                        r.drawImageTile(objectsImage, x * TS, (y - 1) * TS, 4, 3);
                        r.drawImageTile(objectsImage, x * TS, y * TS, 4, 4);
                        break;
                }
            }
        }
        for(GameObject obj : objects) obj.render(gc, this, r);
    }

    public void loadLevel(String path) {

        Image levelImage = mapTester ? new Image(path, true) : new Image(path);
        levelW = levelImage.getW();
        levelH = levelImage.getH();
        solid = new boolean[levelW * levelH];
        bloc = new int[levelW * levelH];

        int index;
        for(int y = 0; y < levelH; y++) {
            for(int x = 0; x < levelW; x++) {
                index = x + y * levelW;
                switch(levelImage.getP()[index]) {
                    case 0xff00ff00://spawn
                        spawnX = x;
                        spawnY = y;
                        break;
                    case 0xff000000://walls
                        solid[index] = true;
                        bloc[index] = 1;
                        break;
                    case 0xffff648c://heart
                        solid[index] = false;
                        bloc[index] = 2;
                        break;
                    case 0xffff0000://skewer top
                        solid[index] = false;
                        bloc[index] = 3;
                        break;
                    case 0xffff00ff://skewer down
                        solid[index] = false;
                        bloc[index] = 4;
                        break;
                    case 0xff0000ff://level key
                        solid[index] = false;
                        bloc[index] = 5;
                        break;
                    case 0xffff7700://check point
                        solid[index] = false;
                        bloc[index] = 6;
                        break;
                    case 0xffffff00://coin
                        solid[index] = false;
                        bloc[index] = 7;
                        break;
                    case 0xff00ffff://torch
                        solid[index] = false;
                        bloc[index] = 11;
                        break;
                    case 0xff777777://bouncing
                        solid[index] = false;
                        bloc[index] = 12;
                        break;
                    case 0xff999999://exit door
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
        gc.setTitle("Skewer Maker");
        gc.setScale(3f);
        gc.start();
    }
}
