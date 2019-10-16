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

    public Window(GameContainer gc) {

        JFrame frame = new JFrame(gc.getTitle());
        frame.setUndecorated(true);
        frame.setResizable(false);

        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        try {
            Toolkit toolkit = Toolkit.getDefaultToolkit();
            Image img = ImageIO.read(Window.class.getResourceAsStream("/cursor.png"));
            Cursor c = toolkit.createCustomCursor(img, new Point(0,0), "custom cursor");
            frame.getRootPane().setCursor(c);
        } catch(IOException e) {
            e.printStackTrace();
        }
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

        gc.setWidth((int) (w / gc.getScale()));
        gc.setHeight((int) (h / gc.getScale()));

        image = new BufferedImage(gc.getWidth(), gc.getHeight(), BufferedImage.TYPE_INT_RGB);

        frame.setSize(w, h);
        frame.setVisible(true);

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
}
