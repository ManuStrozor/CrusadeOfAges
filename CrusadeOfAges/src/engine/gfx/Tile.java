package engine.gfx;

public class Tile {

    private String tag;
    private int code;
    private int x, y;
    private boolean solid;

    public Tile(String tag, int code, int x, int y, boolean solid) {
        this.tag = tag;
        this.code = code;
        this.x = x;
        this.y = y;
        this.solid = solid;
    }

    public String getTag() {
        return tag;
    }

    public int getCode() {
        return code;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isSolid() {
        return solid;
    }

    public boolean isTagged(String tag) {
        return this.tag.equals(tag);
    }
}
