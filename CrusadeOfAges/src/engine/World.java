package engine;

import engine.gfx.Image;

import java.util.ArrayList;

public class World {

    private ArrayList<Bloc> blocs;
    private int[] map;
    private int width, height;
    private int spawnX = -1, spawnY = -1;

    public World() {
        blocs = new ArrayList<>();
        blocs.add(new Bloc("wall",                  0, 1,0, false));
        blocs.add(new Bloc("wall",                  0xffffff, 1,0, false));
        blocs.add(new Bloc("wall",                  0x00ffffff, 1,0, false));
        blocs.add(new Bloc("wall",                  0xffffffff, 1,0, false));
        blocs.add(new Bloc("floor",                 0xff000000, 0,0, true));
        blocs.add(new Bloc("slime",                 0xff777777, 0,2, true));
        blocs.add(new Bloc("ladder",                0xff009900, 0,1, false));
        blocs.add(new Bloc("under_shadow",          0x66000000, 0,3, false));
        blocs.add(new Bloc("above_shadow",          0x69000000, 0,4, false));
        blocs.add(new Bloc("ground_spikes",         0xffff0000, 1,1, false));
        blocs.add(new Bloc("ground_spikes_blooded", 0xff990000, 1,2, false));
        blocs.add(new Bloc("ceiling_spikes",        0xffff00ff, 2,1, false));
        blocs.add(new Bloc("ceiling_spikes_blooded",0xff990099, 2,2, false));
        blocs.add(new Bloc("lever_left",            0xe1e1e1e1, 1,3, false));
        blocs.add(new Bloc("lever_right",           0xe2e2e2e2, 2,3, false));
        blocs.add(new Bloc("spawn",                 0xff00ff00, 2,0, false));
        blocs.add(new Bloc("skull",                 0xffff7700, 3,0, false));
        blocs.add(new Bloc("key",                   0xff0000ff, 3,1, false));
        blocs.add(new Bloc("pill",                  0xffff648c, 3,2, false));
        blocs.add(new Bloc("torch",                 0xff00ffff, 4,0, false));
        blocs.add(new Bloc("coin",                  0xffffff00, 5,0, false));
        blocs.add(new Bloc("door",                  0xff999999, 5,2, false));
        blocs.add(new Bloc("arrow_down",            0x42000000, 6,0, false));
        blocs.add(new Bloc("arrow_left",            0x42ff0000, 6,1, false));
        blocs.add(new Bloc("arrow_right",           0x420000ff, 6,2, false));
        blocs.add(new Bloc("arrow_up",              0x4200ff00, 6,3, false));
    }

    /**
     * Initialize GameMap with all pixels of img
     * @param img
     */
    public void init(Image img) {
        this.width = img.getW();
        this.height = img.getH();
        int[] p = img.getP();
        map = new int[width * height];
        for (int i = 0; i < width * height; i++) {
            if (p[i] == getBloc("spawn").getCode()) {
                spawnY = i / width;
                spawnX = i - spawnY * width;
            }
            map[i] = p[i];
        }
    }

    /**
     * Define one bloc with a rgba code
     * @param x   Width coordinate
     * @param y   Height coordinate
     * @param col Rgba code
     */
    public void setBloc(int x, int y, int col) {
        int pos = x + y * width;
        if (pos >= 0 && pos < map.length) {
            if (col == getBloc("spawn").getCode()) {
                spawnX = x;
                spawnY = y;
            }
            map[pos] = col;
        }
    }

    public Bloc getBlocMap(int x, int y) {
        if (x >= 0 && x < width && y >= 0 && y < height)
            return getBloc(map[x + y * width]);
        else
            return getBloc("wall");
    }

    public Bloc getBloc(String tag) {
        int i = 0;
        while (i < blocs.size() && !blocs.get(i).isTagged(tag)) {
            i++;
        }
        if (i < blocs.size()) return blocs.get(i);
        return null;
    }

    private Bloc getBloc(int code) {
        int i = 0;
        while (i < blocs.size() && blocs.get(i).getCode() != code) {
            i++;
        }
        if (i < blocs.size()) return blocs.get(i);
        return getBloc("wall");
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

    public void resetSpawn() {
        spawnX = -1;
        spawnY = -1;
    }

    /**
     * Remove the bloc at (x, y) position in the map
     *
     * @param x With coordinate
     * @param y Height coordinate
     */
    public void clean(int x, int y) {
        map[x + y * width] = 0;
    }

    public void blank() {
        for (int i = 0; i < map.length; i++) {
            map[i] = 0;
        }
    }
}
