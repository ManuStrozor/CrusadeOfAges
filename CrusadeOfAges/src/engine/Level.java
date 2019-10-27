package engine;

import engine.gfx.Image;
import engine.gfx.Light;
import game.Camera;
import game.entity.Entity;
import game.entity.Player;
import game.entity.PlayerMP;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.stream.IntStream;

public class Level {

    private int[] tiles;
    private int currLevel = 0;
    private String[][] lvls = {
            {"/levels/Techsdale (TXDL).png", "Techsdale (TXDL)"},
            {"/levels/Vilcomen.png", "Vilcomen"}
    };
    private int spawnX = -1, spawnY = -1;
    private int width, height;
    private List<Entity> entities = new ArrayList<>();

    private World world;
    private Player player;
    private Camera camera;
    private Image image;

    public Level(World world) {
        world.setLevel(this);
        this.world = world;
    }

    public void update(GameContainer gc, float dt) {
        if (camera != null) camera.update(gc, dt);
        for(int i = 0; i < entities.size(); i++) {
            if(entities.get(i).isDead()) {
                entities.remove(i--);
            } else {
                entities.get(i).update(gc, dt);
            }
        }
    }

    public void render(GameContainer gc, Renderer r) {
        if (camera != null) camera.render(r);
        r.drawLevel(true);
        if(gc.getSettings().isShowLights()) r.drawLevelLights(new Light(30, 0xffffff99));
        try {
            for(Entity ent : entities) {
                System.out.print(ent.getTag() + " ");
                ent.render(gc, r);
            }
            System.out.println("");
        } catch(ConcurrentModificationException e) {
            System.out.println("/// " + entities.size() + " ///");
        }
    }

    public void load() {
        loadFromImage(new Image(lvls[currLevel][0], false));
        if (player == null || player.isDead()) {
            player = new Player("player1", world);
            camera = new Camera("player1", world);
        }
        addEntity(player);
    }

    public void loadMulti(PlayerMP player) {
        loadFromImage(new Image(lvls[currLevel][0], false));
        this.player = player;
        camera = new Camera(player.getTag(), world);
    }

    public void loadFromImage(Image image) {
        entities.clear();
        this.image = image;
        width = image.getWidth();
        height = image.getHeight();
        tiles = new int[width * height];
        loadTiles();
    }

    private void loadTiles() {
        int[] p = image.getP();
        for (int i = 0; i < width * height; i++) {
            if (p[i] == world.getBloc("spawn").getCode()) {
                spawnY = i / width;
                spawnX = i - spawnY * width;
            }
            tiles[i] = p[i];
        }
    }

    /**
     * Returns an Entity associated with a tag
     * @param tag is the object identifier
     * @return Entity
     */
    public Entity getEntity(String tag) {
        int i = 0;
        while (i < entities.size() && !entities.get(i).getTag().equalsIgnoreCase(tag)) {
            i++;
        }
        return i != entities.size() ? entities.get(i) : null;
    }

    public void addEntity(Entity ent) {
        entities.add(ent);
    }

    public int getSpawnX() {
        return spawnX;
    }

    public int getSpawnY() {
        return spawnY;
    }

    public boolean hasSpawn() {
        return spawnX != -1 && spawnY != -1;
    }

    public void setSpawn(int x, int y) {
        spawnX = x;
        spawnY = y;
    }

    public void resetSpawn() {
        setSpawn(-1, -1);
    }

    /**
     * Remove the bloc at (x, y) position in the level
     *
     * @param x With coordinate
     * @param y Height coordinate
     */
    public void clean(int x, int y) {
        tiles[x + y * width] = 0;
    }

    public void blank() {
        IntStream.range(0, tiles.length).forEach(i -> tiles[i] = 0);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int[] getTiles() {
        return tiles;
    }

    public String[][] getLvls() {
        return lvls;
    }

    public int getCurrLevel() {
        return currLevel;
    }

    public void setCurrLevel(int current) {
        this.currLevel = current;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

}
