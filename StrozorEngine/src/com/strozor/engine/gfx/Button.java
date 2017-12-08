package com.strozor.engine.gfx;

public class Button {

    private int offX, offY, width = 170, height = 20, bgColor, goState;
    private String text;

    public Button(String text, int goState) {
        this.goState = goState;
        this.text = text;
    }

    public Button(int width, int height, String text, int goState) {
        this.width = width;
        this.height = height;
        this.goState = goState;
        this.text = text;
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

    public int getGoState() {
        return goState;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}