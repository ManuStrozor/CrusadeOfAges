package com.strozor.engine.gfx;

public class Light {

    private int radius, diameter;
    private int[] lm;

    public Light(int radius, int color) {
        this.radius = radius;
        this.diameter = radius * 2;
        this.lm = new int[diameter * diameter];

        for(int y = 0; y < diameter; y++) {
            for(int x = 0; x < diameter; x++) {
                double dist = Math.sqrt((x - radius) * (x - radius) + (y - radius) * (y - radius));
                double dim = 1 / Math.pow(dist/radius + 1, 2) - .25; // Atténuation lumière
                dim = Math.max(dim, 0);
                lm[x + y * diameter] = (int)(((color >> 16) & 255) * dim) << 16 | (int)(((color >> 8) & 255) * dim) << 8 | (int)((color & 255) * dim);
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
