package engine;

import engine.gfx.Tile;
import game.Conf;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.stream.IntStream;

public class TileMap {

    private ArrayList<Tile> tiles = new ArrayList<>();
    private int[] map;
    private int width, height;
    private int spawnX = -1, spawnY = -1;

    public TileMap() {
        tiles.add(new Tile("free",                  -1, 6,5, false));
        tiles.add(new Tile("wall",                  0, 1,0, false));
        tiles.add(new Tile("water",                 1, 3,3, false));
        tiles.add(new Tile("floor",                 2, 0,0, true));
        tiles.add(new Tile("slime",                 3, 0,2, true));
        tiles.add(new Tile("ladder",                4, 0,1, false));
        tiles.add(new Tile("under_shadow",          5, 0,3, false));
        tiles.add(new Tile("ground_spikes",         6, 1,1, false));
        tiles.add(new Tile("ground_spikes_blooded", 7, 1,2, false));
        tiles.add(new Tile("ceiling_spikes",        8, 2,1, false));
        tiles.add(new Tile("ceiling_spikes_blooded",9, 2,2, false));
        tiles.add(new Tile("lever_left",            10, 1,3, false));
        tiles.add(new Tile("lever_right",           11, 2,3, false));
        tiles.add(new Tile("spawn",                 12, 2,0, false));
        tiles.add(new Tile("skull",                 13, 3,0, false));
        tiles.add(new Tile("key",                   14, 3,1, false));
        tiles.add(new Tile("pill",                  15, 3,2, false));
        tiles.add(new Tile("torch",                 16, 4,0, false));
        tiles.add(new Tile("coin",                  17, 5,0, false));
        tiles.add(new Tile("door",                  18, 5,2, false));
        tiles.add(new Tile("arrow_down",            19, 6,0, false));
        tiles.add(new Tile("arrow_left",            20, 6,1, false));
        tiles.add(new Tile("arrow_right",           21, 6,2, false));
        tiles.add(new Tile("arrow_up",              22, 6,3, false));
    }

    /**
     * Initialize map from file.map
     * @param mapFile file.map
     */
    public void init(File mapFile) throws IOException {

        byte[] bytes = new byte[(int) mapFile.length()];
        FileInputStream fis = new FileInputStream(mapFile);
        fis.read(bytes);
        fis.close();
        String[] valueStr = new String(bytes).trim().split(";");
        map = new int[valueStr.length - 2];
        this.width = Integer.parseInt(valueStr[0]);
        this.height = Integer.parseInt(valueStr[1]);
        for (int i = 2; i < valueStr.length; i++) {
            if (Integer.parseInt(valueStr[i]) == getTile("spawn").getCode()) {
                spawnY = i / width;
                spawnX = i - spawnY * width;
            }
            map[i] = Integer.parseInt(valueStr[i]);
        }
    }

    /**
     * Define one bloc with a Tile code
     * @param x   Width coordinate
     * @param y   Height coordinate
     * @param code Tile code (cf. constructor)
     */
    public void setTile(int x, int y, int code) {
        int pos = x + y * width;
        if (pos >= 0 && pos < map.length) {
            if (code == getTile("spawn").getCode()) {
                spawnX = x;
                spawnY = y;
            }
            map[pos] = code;
        }
    }

    public Tile getTileFromMap(int x, int y) {
        if (x >= 0 && x < width && y >= 0 && y < height)
            return getTile(map[x + y * width]);
        else
            return getTile("wall");
    }

    public Tile getTile(String tag) {
        int i = 0;
        while (i < tiles.size() && !tiles.get(i).isTagged(tag)) {
            i++;
        }
        if (i < tiles.size()) return tiles.get(i);
        return null;
    }

    private Tile getTile(int code) {
        int i = 0;
        while (i < tiles.size() && tiles.get(i).getCode() != code) {
            i++;
        }
        if (i < tiles.size()) return tiles.get(i);
        return getTile("wall");
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
        IntStream.range(0, map.length).forEach(i -> map[i] = 0);
    }

    public void export(String rename) {
        try {
            DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");
            String filename;
            if (rename.equals("")) filename = sdf.format(new Date()) + ".map";
            else filename = rename;
            FileWriter writer = new FileWriter(Conf.SM_FOLDER + "/creative_mode/" + filename);
            writer.write(width + ";" + height + ";");
            for (int i = 0; i < map.length; i++) {
                writer.write(map[i]);
                if (i < map.length - 1) writer.write(";");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
