package engine.gfx;

public class Checkbox {

    private String tag;
    private int offX, offY, width = 20, height = 20, bgColor;
    private boolean hover, checked = false;

    public Checkbox(String tag) {
        bgColor = 0x66818E9A;
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }

    public int getOffX() {
        return offX;
    }

    public int getOffY() {
        return offY;
    }

    public void setCoor(int offX, int offY) {
        setOffX(offX, 1);
        setOffY(offY, 1);
    }

    public void setCoor(int offX, int offY, int alignX, int alignY) {
        setOffX(offX, alignX);
        setOffY(offY, alignY);
    }

    public void setOffX(int offX, int alignX) {
        this.offX = alignAxe(offX, alignX, width);
    }

    public void setOffY(int offY, int alignY) {
        this.offY = alignAxe(offY, alignY, height);
    }

    private int alignAxe(int value, int align, int Axe) {
        if (align != 1) {
            if (align == 0) value -= Axe / 2;
            else if (align == -1) value -= Axe;
        }
        return value;
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
