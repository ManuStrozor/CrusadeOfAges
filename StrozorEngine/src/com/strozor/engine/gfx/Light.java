package com.strozor.engine.gfx;

public class Light {

    private int radius, diameter, color;
    private int[] lm;

    public Light(int radius, int color) {
        this.radius = radius;
        this.diameter = radius * 2;
        this.color = color;
        this.lm = new int[diameter * diameter];

        for(int y = 0; y < diameter; y++) {
            for(int x = 0; x < diameter; x++) {
                double dist = Math.sqrt((x - radius) * (x - radius) + (y - radius) * (y - radius));

                if(dist < radius) {
                    double power = 1 - dist / radius;
                    lm[x + y * diameter] = (int)(((color >> 16) & 255) * power) << 16 | (int)(((color >> 8) & 255) * power) << 8 | (int)((color & 255) * power);
                } else {
                    lm[x + y * diameter] = 0;
                }
            }
        }
    }

    public int getLightValue(int x, int y) {
        if(x < 0 || x >= diameter || y < 0 || y >= diameter)
            return 0;
        return lm[x + y * diameter];
    }

    public int getRadius() {
        return radius;
    }

    public int getDiameter() {
        return diameter;
    }
}
