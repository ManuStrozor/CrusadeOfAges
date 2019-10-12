package engine;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import engine.gfx.Image;


public class World {

    private Map<Integer, String> colTag;
    private Map<String, Integer> tagCol;
    private Map<String, int[]> tagPos;
    private int[] map, solids = {0xff000000, 0xff777777};
    private int width, height, spawnX = -1, spawnY = -1;

    public World() {
        colTag = new HashMap<>();
        colTag.put(0xff000000, "floor");
        colTag.put(0xff009900, "ladder");
        colTag.put(0xff777777, "slime");
        colTag.put(0x66000000, "under shadow");
        colTag.put(0x69000000, "above shadow");
        colTag.put(0, "wall");
        colTag.put(0xffffff, "wall");
        colTag.put(0xffff0000, "ground spikes");
        colTag.put(0xff990000, "ground spikes blooded");
        colTag.put(0xe1e1e1e1, "lever left");
        colTag.put(0xff00ff00, "spawn");
        colTag.put(0xffff00ff, "ceiling spikes");
        colTag.put(0xff990099, "ceiling spikes blooded");
        colTag.put(0xe2e2e2e2, "lever right");
        colTag.put(0xffff7700, "skull");
        colTag.put(0xff0000ff, "key");
        colTag.put(0xffff648c, "pill");
        colTag.put(0xff00ffff, "torch");
        colTag.put(0xffffff00, "coin");
        colTag.put(0xff999999, "door");
        colTag.put(0x42000000, "arrow down");
        colTag.put(0x42ff0000, "arrow left");
        colTag.put(0x420000ff, "arrow right");
        colTag.put(0x4200ff00, "arrow up");

        tagCol = new HashMap<>();
        tagCol.put("floor", 0xff000000);
        tagCol.put("ladder", 0xff009900);
        tagCol.put("slime", 0xff777777);
        tagCol.put("under shadow", 0x66000000);
        tagCol.put("above shadow", 0x69000000);
        tagCol.put("wall", 0);
        tagCol.put("ground spikes", 0xffff0000);
        tagCol.put("ground spikes blooded", 0xff990000);
        tagCol.put("lever left", 0xe1e1e1e1);
        tagCol.put("spawn", 0xff00ff00);
        tagCol.put("ceiling spikes", 0xffff00ff);
        tagCol.put("ceiling spikes blooded", 0xff990099);
        tagCol.put("lever right", 0xe2e2e2e2);
        tagCol.put("skull", 0xffff7700);
        tagCol.put("key", 0xff0000ff);
        tagCol.put("pill", 0xffff648c);
        tagCol.put("torch", 0xff00ffff);
        tagCol.put("coin", 0xffffff00);
        tagCol.put("door", 0xff999999);
        tagCol.put("arrow down", 0x42000000);
        tagCol.put("arrow left", 0x42ff0000);
        tagCol.put("arrow right", 0x420000ff);
        tagCol.put("arrow up", 0x4200ff00);

        tagPos = new TreeMap<>();
        tagPos.put("floor", pos(0, 0));
        tagPos.put("ladder", pos(0, 1));
        tagPos.put("slime", pos(0, 2));
        tagPos.put("under shadow", pos(0, 3));
        tagPos.put("above shadow", pos(0, 4));
        tagPos.put("wall", pos(1, 0));
        tagPos.put("ground spikes", pos(1, 1));
        tagPos.put("ground spikes blooded", pos(1, 2));
        tagPos.put("lever left", pos(1, 3));
        tagPos.put("spawn", pos(2, 0));
        tagPos.put("ceiling spikes", pos(2, 1));
        tagPos.put("ceiling spikes blooded", pos(2, 2));
        tagPos.put("lever right", pos(2, 3));
        tagPos.put("skull", pos(3, 0));
        tagPos.put("key", pos(3, 1));
        tagPos.put("pill", pos(3, 2));
        tagPos.put("torch", pos(4, 0));
        tagPos.put("coin", pos(5, 0));
        tagPos.put("door", pos(5, 2));
        tagPos.put("arrow down", pos(6, 0));
        tagPos.put("arrow left", pos(6, 1));
        tagPos.put("arrow right", pos(6, 2));
        tagPos.put("arrow up", pos(6, 3));
    }

    /**
     * Returns an array containing (x, y) coordinates in the object's sprite
     *
     * @param x Width coordinate
     * @param y Height coordinate
     * @return int[] Array
     */
    private int[] pos(int x, int y) {
        return new int[]{x, y};
    }

    /**
     * Initialize GameMap with all pixels of img
     *
     * @param img
     */
    public void init(Image img) {
        this.width = img.getW();
        this.height = img.getH();
        int[] p = img.getP();
        map = new int[width * height];
        for (int i = 0; i < width * height; i++) {
            if (p[i] == 0xff00ff00) {
                spawnY = i / width;
                spawnX = i - spawnY * width;
            }
            map[i] = p[i];
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    /**
     * X coordinate of Spawn point in the map
     *
     * @return the x coor of the spawn
     */
    public int getSpawnX() {
        return spawnX;
    }

    /**
     * Y coordinate of Spawn point in the map
     *
     * @return the y coor of the spawn
     */
    public int getSpawnY() {
        return spawnY;
    }

    /**
     * Verify if a bloc is solid (can't walk throw) or not
     *
     * @param x Width coordinate
     * @param y Height coordinate
     * @return True if the bloc is solid and False if it's not
     */
    public boolean isSolid(int x, int y) {
        if (x >= 0 && x < width && y >= 0 && y < height)
            for (int s : solids)
                if (s == map[x + y * width])
                    return true;
        return (x < 0 || x >= width || y < 0 || y >= height);
    }

    /**
     * Define one bloc with a rgba code
     *
     * @param x   Width coordinate
     * @param y   Height coordinate
     * @param col Rgba code
     */
    public void setBloc(int x, int y, int col) {
        if (col == 0xff00ff00) {
            spawnX = x;
            spawnY = y;
        }
        map[x + y * width] = col;
    }

    /**
     * Get the bloc's tag at (x, y) coordinates on the map
     *
     * @param x Width coordinate
     * @param y Height coordinate
     * @return String : the bloc's tag (ex: floor, spikes, spawn, etc...)
     */
    public String getTag(int x, int y) {
        if (x >= 0 && x < width && y >= 0 && y < height && colTag.containsKey(map[x + y * width]))
            return colTag.get(map[x + y * width]);
        else
            return "floor";
    }

    /**
     * Get a tag's rgba code
     *
     * @param tag
     * @return Rgba code
     */
    public int getCol(String tag) {
        return tagCol.get(tag);
    }

    public int[] getTile(String tag) {
        return tagPos.get(tag);
    }

    /**
     * Remove the bloc at (x, y) position in the map
     *
     * @param x With coordinate
     * @param y Height coordinate
     */
    public void clean(int x, int y) {
        this.map[x + y * width] = 0;
    }
}
