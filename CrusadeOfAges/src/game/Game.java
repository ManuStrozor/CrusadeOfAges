package game;

import engine.*;

import javax.imageio.ImageIO;
import java.awt.event.KeyEvent;
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Game extends AbstractGame {

    public static final int TS = 32;
    private Level level;
    private ArrayList<Notification> notifs = new ArrayList<>();

    public Game(World world) {
        super(world);
        level = new Level(world);
    }

    @Override
    public void update(GameContainer gc, float dt) {

        if(gc.getInput().isKeyDown(KeyEvent.VK_ESCAPE)) gc.setActiView("pausedGame");

        // Screenshots
        if(gc.getInput().isKeyDown(KeyEvent.VK_F12)) {
            try {
                DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");
                String filename = sdf.format(new Date()) + ".png";
                File out = new File(Conf.SM_FOLDER + "/screenshots/" + filename);
                ImageIO.write(gc.getWindow().getImage(), "png", out);
                notifs.add(new Notification(filename, 3, 100, -1));
            } catch(IOException e) {
                e.printStackTrace();
            }
        }

        level.update(gc, dt);

        // Notifications update
        for(int i = 0; i < notifs.size(); i++) {
            if(notifs.get(i).isEnded()) {
                notifs.remove(i--);
            } else {
                notifs.get(i).update(dt);
            }
        }
    }

    @Override
    public void render(GameContainer gc, Renderer r) {
        level.render(gc, r);
        for(Notification notif : notifs) notif.render(gc, r);
    }

    public Level getLevel() {
        return level;
    }
}
