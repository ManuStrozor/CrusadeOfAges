package game;

import engine.*;
import engine.gfx.*;
import game.entity.Player;

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
    private Camera camera;

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

        // Notifications update
        for(int i = 0; i < notifs.size(); i++) {
            if(notifs.get(i).isEnded()) {
                notifs.remove(i--);
            } else {
                notifs.get(i).update(dt);
            }
        }

        level.update(gc, dt);

        // Load level
        if(level.getEntity(gc.getSocketClient().getPlayerName()) == null
                && (gc.getPrevView().equals("gameOver") || gc.getPrevView().equals("mainMenu"))) {
            level.load();
        }

        if (camera != null) camera.update(gc, dt);
    }

    @Override
    public void render(GameContainer gc, Renderer r) {
        if (camera != null) camera.render(r);
        r.drawLevel(true);
        if(gc.getSettings().isShowLights()) r.drawLevelLights(new Light(30, 0xffffff99));
        level.render(gc, r);
        for(Notification notif : notifs) notif.render(gc, r);
    }

    public Level getLevel() {
        return level;
    }
}
