package com.strozor.engine;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;
import javax.swing.WindowConstants;


public class Window {

    private BufferedImage image;
    private Canvas canvas;
    private BufferStrategy bs;
    private Graphics g;

    private GameContainer gc;

    public Window(GameContainer gc) {

        this.gc = gc;
        JFrame frame = new JFrame(this.gc.getTitle());
        frame.setUndecorated(true);
        frame.setResizable(false);
        
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        Toolkit tk = Toolkit.getDefaultToolkit();
        int w = (int) tk.getScreenSize().getWidth();
        int h = (int) tk.getScreenSize().getHeight();

        Dimension d = new Dimension(w, h);
        this.canvas = new Canvas();
        this.canvas.setPreferredSize(d);
        this.canvas.setMaximumSize(d);
        this.canvas.setMinimumSize(d);

        frame.add(this.canvas, BorderLayout.CENTER);
        frame.pack();

        this.gc.setWidth((int)(w / this.gc.getScale()));
        this.gc.setHeight((int)(h / this.gc.getScale()));

        this.image = new BufferedImage(this.gc.getWidth(), this.gc.getHeight(), BufferedImage.TYPE_INT_RGB);

        frame.setSize(w, h);
        frame.setVisible(true);

        this.canvas.createBufferStrategy(3);
        this.bs = this.canvas.getBufferStrategy();
        this.g = this.bs.getDrawGraphics();
        this.canvas.requestFocus();
    }

    public void update() {
        this.g.drawImage(this.image, 0, 0, this.canvas.getWidth(), this.canvas.getHeight(), null);
        this.bs.show();
    }

    public BufferedImage getImage() {
        return this.image;
    }

    Canvas getCanvas() {
        return this.canvas;
    }
}
