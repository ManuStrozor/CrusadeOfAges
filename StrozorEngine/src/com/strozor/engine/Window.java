package com.strozor.engine;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.event.FocusListener;
import java.awt.event.FocusEvent;

public class Window {

    private JFrame frame;
    private BufferedImage image;
    private Canvas canvas;
    private BufferStrategy bs;
    private Graphics g;

    public Window(GameContainer gc) {

        frame = new JFrame(gc.getTitle());
        frame.setUndecorated(true);
        frame.setResizable(false);
        frame.setVisible(true);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        Toolkit tk = Toolkit.getDefaultToolkit();
        int w = (int) tk.getScreenSize().getWidth();
        int h = (int) tk.getScreenSize().getHeight();

        Dimension d = new Dimension(w, h);
        canvas = new Canvas();
        canvas.setPreferredSize(d);
        canvas.setMaximumSize(d);
        canvas.setMinimumSize(d);

        frame.add(canvas, BorderLayout.CENTER);
        frame.pack();

        gc.setWidth((int)(w / gc.getScale()));
        gc.setHeight((int)(h / gc.getScale()));

        image  = new BufferedImage(gc.getWidth(), gc.getHeight(), BufferedImage.TYPE_INT_RGB);

        frame.setSize(w, h);

        canvas.createBufferStrategy(3);
        bs = canvas.getBufferStrategy();
        g = bs.getDrawGraphics();
        canvas.requestFocus();
    }

    public void update() {
        g.drawImage(image,0, 0, canvas.getWidth(), canvas.getHeight(), null);
        bs.show();
    }

    public BufferedImage getImage() {
        return image;
    }

    public Canvas getCanvas() {
        return canvas;
    }
}
