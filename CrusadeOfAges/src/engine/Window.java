package engine;

import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.WindowConstants;


public class Window {

    private BufferedImage image;
    private Canvas canvas;
    private BufferStrategy bs;
    private Graphics g;
    private JFrame f;
    private Toolkit tk;
    private Image iD, iH, iM, iR;

    public Window(GameContainer gc) {

        f = new JFrame(gc.getTitle());
        f.setUndecorated(true);
        f.setResizable(false);

        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        f.setLayout(new BorderLayout());

        tk = Toolkit.getDefaultToolkit();

        int size;
        Dimension cursorSize = tk.getBestCursorSize(64, 64);
        if (cursorSize.width > 32) {
            size = 64;
        } else if (cursorSize.width > 16) {
            size = 32;
        } else {
            size = 16;
        }

        try {
            iD = ImageIO.read(Window.class.getResourceAsStream("/cursor/default"+size+".png"));
            iH = ImageIO.read(Window.class.getResourceAsStream("/cursor/hand"+size+".png"));
            iM = ImageIO.read(Window.class.getResourceAsStream("/cursor/moving"+size+".png"));
            iR = ImageIO.read(Window.class.getResourceAsStream("/cursor/rubber"+size+".png"));
        } catch(IOException e) {
            e.printStackTrace();
        }

        setDefaultCursor();

        int w = (int) tk.getScreenSize().getWidth();
        int h = (int) tk.getScreenSize().getHeight();

        Dimension d = new Dimension(w, h);
        canvas = new Canvas();
        canvas.setPreferredSize(d);
        canvas.setMaximumSize(d);
        canvas.setMinimumSize(d);

        f.add(canvas, BorderLayout.CENTER);
        f.pack();

        gc.setWidth((int) (w / gc.getScale()));
        gc.setHeight((int) (h / gc.getScale()));

        image = new BufferedImage(gc.getWidth(), gc.getHeight(), BufferedImage.TYPE_INT_RGB);

        f.setSize(w, h);
        f.setVisible(true);

        canvas.createBufferStrategy(3);
        bs = canvas.getBufferStrategy();
        g = bs.getDrawGraphics();
        canvas.requestFocus();
    }

    public void update() {
        g.drawImage(image, 0, 0, canvas.getWidth(), canvas.getHeight(), null);
        bs.show();
    }

    public BufferedImage getImage() {
        return image;
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public void setDefaultCursor() {
        if (!f.getRootPane().getCursor().getName().equals("default")) {
            f.getRootPane().setCursor(tk.createCustomCursor(iD, new Point(0,0), "default"));
        }
    }

    public void setHandCursor() {
        if (!f.getRootPane().getCursor().getName().equals("hand")) {
            f.getRootPane().setCursor(tk.createCustomCursor(iH, new Point(8,0), "hand"));
        }
    }

    public void setMovingCursor() {
        if (!f.getRootPane().getCursor().getName().equals("moving")) {
            f.getRootPane().setCursor(tk.createCustomCursor(iM, new Point(13,13), "moving"));
        }
    }

    public void setRubberCursor() {
        if (!f.getRootPane().getCursor().getName().equals("rubber")) {
            f.getRootPane().setCursor(tk.createCustomCursor(iR, new Point(8,30), "rubber"));
        }
    }

    public void setBlankCursor() {
        if (!f.getRootPane().getCursor().getName().equals("blank")) {
            BufferedImage blank = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
            f.getRootPane().setCursor(tk.createCustomCursor(blank, new Point(0,0), "blank"));
        }
    }
}
