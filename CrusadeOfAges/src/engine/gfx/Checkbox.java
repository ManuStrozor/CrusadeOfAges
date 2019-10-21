package engine.gfx;

public class Checkbox {

    private String tag;
    private int offX, offY, width = 20, height = 20, bgColor;
    private boolean hover, checked = false;

    public Checkbox(String tag) {
        bgColor = 0x66818E9A;
        this.tag = tag;
    }

    public Checkbox(String tag, boolean checked) {
        bgColor = 0x66818E9A;
        this.tag = tag;
        this.checked = checked;
    }

    public Checkbox(String tag, int width, int height) {
        bgColor = 0x66818E9A;
        this.tag = tag;
        this.width = width;
        this.height = height;
    }

    public String getTag() {
        return tag;
    }

    public int getOffX() {
        return offX;
    }

    public void setOffX(int offX) {
        this.offX = offX;
    }

    public int getOffY() {
        return offY;
    }

    public void setOffY(int offY) {
        this.offY = offY;
    }

    public void setAlignCoor(int offX, int offY, int alignX, int alignY) {
        if (alignX != 1) {
            if (alignX == 0) offX -= width / 2;
            else if (alignX == -1) offX -= width;
        }

        if (alignY != 1) {
            if (alignY == 0) offY -= height / 2;
            else if (alignY == -1) offY -= height;
        }
        this.offX = offX;
        this.offY = offY;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getBgColor() {
        return bgColor;
    }

    public void setBgColor(int bgColor) {
        this.bgColor = bgColor;
    }

    public boolean setHover(boolean hover) {
        this.hover = hover;
        return hover;
    }

    public boolean isHover() {
        return hover;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public boolean isChecked() {
        return checked;
    }
}
