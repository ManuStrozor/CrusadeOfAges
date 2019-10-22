package game;

import engine.*;
import engine.gfx.Image;
import game.objects.Player;

import javax.imageio.ImageIO;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Editor extends AbstractGame {

    public static File mapFile;
    public static TileMap tileMap;
    private static boolean spawn = false;
    public static boolean once = true, newOne = true;
    public static String rename = "";

    private Settings settings;
    private Player player;

    private int width, height;

    private String[] elems = {
            "free",
            "spawn",
            "floor",
            "slime",
            "water",
            "ladder",
            "ground_spikes",
            "ceiling_spikes",
            "pill",
            "coin",
            "key",
            "skull",
            "lever_left",
            "torch",
            "door"
    };
    private int tileCode, scroll = 0;

    public static int ts = GameManager.TS;
    private int toolSize = 32;
    private static final int DRAGSPEED = 3;
    private int dragOffX = -1, dragOffY = -1;
    private int tmpCamX = -1, tmpCamY = -1;

    private ArrayList<Notification> notifs = new ArrayList<>();

    public Editor(Settings settings, TileMap tileMap) {
        this.settings = settings;
        Editor.tileMap = tileMap;
        player = new Player("Tester", tileMap, 999);
    }

    @Override
    public void update(GameContainer gc, float dt) {

        if (gc.getInput().isKeyDown(KeyEvent.VK_ESCAPE)) gc.setActiView("pausedEdit");

        if (once) {
            if (newOne) {
                try {
                    DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");
                    String filename = sdf.format(new Date()) + ".map";
                    Path path = Paths.get(Conf.SM_FOLDER + "/creative_mode/" + filename);
                    Files.createFile(path);
                    tileMap.init(new File(String.valueOf(path)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            width = tileMap.getWidth();
            height = tileMap.getHeight();
            once = false;
        }

        if (spawn && spawnExists()) player.creativeUpdate(gc, dt); // Player update

        int wheel = gc.getInput().getScroll();
        if (!isCtrlDown(gc) && gc.getInput().getMouseX() < toolSize + 4) {

            /////////// TOOLBAR
            if (wheel > 0) {
                scroll = (scroll == elems.length - 1) ? 0 : scroll + 1;
            } else if (wheel < 0) {
                scroll = (scroll == 0) ? elems.length - 1 : scroll - 1;
            }
            /////////// TOOLBAR

        } else if (isCtrlDown(gc)) {

            if (wheel < 0) {

                /////////// ZOOM IN - MOUSE POSITION
                gc.getR().zoomIn(gc.getInput().getMouseX(), gc.getInput().getMouseY(), 1);
                Player.tileSize += 1;
                ts += 1;
                /////////// ZOOM IN - MOUSE POSITION

            } else if (wheel > 0) {

                /////////// ZOOM OUT - MOUSE POSITION
                gc.getR().zoomOut(gc.getInput().getMouseX(), gc.getInput().getMouseY(), 1);
                if (Player.tileSize > 1) Player.tileSize -= 1;
                if (ts > 1) ts -= 1;
                /////////// ZOOM OUT - MOUSE POSITION

            }

        } else {

            /////////// DEPLACEMENT ^/v MOUSE-WHEEL
            if (wheel < 0) {
                gc.getR().setCamY(notExceed(gc.getR().getCamY() - 2* ts, -gc.getHeight(), height * ts));
            } else if (wheel > 0) {
                gc.getR().setCamY(notExceed(gc.getR().getCamY() + 2* ts, -gc.getHeight(), height * ts));
            }
            /////////// DEPLACEMENT ^/v MOUSE-WHEEL

        }

        /////////// SAUVEGARDE
        if (isCtrlDown(gc) && gc.getInput().isKeyUp(KeyEvent.VK_S)) {
            tileMap.export(rename);
            notifs.add(new Notification(rename + settings.translate(" a été exporté avec succès")));
        }
        /////////// SAUVEGARDE

        tileCode = tileMap.getTile(elems[scroll]).getCode();

        ////////////// SCREENSHOT
        if(gc.getInput().isKeyDown(KeyEvent.VK_F12)) {
            DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");
            String filename = sdf.format(new Date()) + ".png";
            File out = new File(Conf.SM_FOLDER + "/screenshots/" + filename);
            try {
                ImageIO.write(gc.getWindow().getImage(), "png", out);
                notifs.add(new Notification(filename, 3, 100, -1));
            } catch(IOException e) {
                notifs.add(new Notification(settings.translate("Impossible d'éffectuer la capture d'écran")));
            }
        }
        ////////////// SCREENSHOT

        //////////// NOTIFS UPDATE
        for (int i = 0; i < notifs.size(); i++) {
            notifs.get(i).update(dt);
            if (notifs.get(i).isEnded()) {
                notifs.remove(i--);
            }
        }
        //////////// NOTIFS UPDATE
    }

    @Override
    public void render(GameContainer gc, Renderer r) {

        if (gc.getActiView().equals("edit")) {

            int mouseX = gc.getInput().getMouseX();
            int mouseY = gc.getInput().getMouseY();
            int mouseMapX = (mouseX + r.getCamX()) / ts;
            int mouseMapY = (mouseY + r.getCamY()) / ts;

            moveWithArrows(r, gc);

            if (isCtrlDown(gc) || isWheelDown(gc)) {

                ///////////////// Déplacement CTRL+Souris ou Molette
                if (!isDragging()) {
                    tmpCamX = r.getCamX();
                    tmpCamY = r.getCamY();
                } else {
                    r.setCamX(notExceed(tmpCamX + (dragOffX - mouseX) * DRAGSPEED, -gc.getWidth(), width * ts));
                    r.setCamY(notExceed(tmpCamY + (dragOffY - mouseY) * DRAGSPEED, -gc.getHeight(), height * ts));
                }

                if (!isDragging() && (gc.getInput().isButton(MouseEvent.BUTTON1) || isWheelDown(gc))) {
                    startDragging(gc);
                }

                if (isDragging() && (!gc.getInput().isButton(MouseEvent.BUTTON1) && !isWheelDown(gc))) {
                    stopDragging(gc);
                }
                ///////////////// Déplacement CTRL+Souris ou Molette

            } else {

                // Arret deplacement Molette
                if (isDragging() && !isWheelDown(gc)) {
                    stopDragging(gc);
                }

                if (!tileMap.getTile(elems[scroll]).isTagged("free")) {

                    ////////////// PLACEMENT + EFFACEMENT
                    if (gc.getInput().isButton(MouseEvent.BUTTON1)) {
                        putSomething(tileCode, mouseMapX, mouseMapY);
                    } else if (gc.getInput().isButton(MouseEvent.BUTTON3)) {
                        delSomething(gc, mouseMapX, mouseMapY);
                    } else {
                        gc.getWindow().setDefaultCursor();
                    }
                    ////////////// PLACEMENT + EFFACEMENT

                } else {
                    gc.getWindow().setDefaultCursor();
                }
            }
        }

        r.drawWorld(tileMap, false);
        r.drawDock(tileMap, elems, scroll, toolSize);
        if (mapFile != null) r.drawArrows(tileMap, tileMap.getWidth(), tileMap.getHeight(), 32);

        if (spawn && spawnExists()) player.render(gc, r);

        for (Notification notif : notifs) notif.render(gc, r);
    }

    private void moveWithArrows(Renderer r, GameContainer gc) {
        if (gc.getInput().isKey(KeyEvent.VK_LEFT)) {
            r.setCamX(notExceed(r.getCamX() - DRAGSPEED, -gc.getWidth(), width * ts));
        }
        if (gc.getInput().isKey(KeyEvent.VK_UP)) {
            r.setCamY(notExceed(r.getCamY() - DRAGSPEED, -gc.getHeight(), height * ts));
        }
        if (gc.getInput().isKey(KeyEvent.VK_RIGHT)) {
            r.setCamX(notExceed(r.getCamX() + DRAGSPEED, -gc.getWidth(), width * ts));
        }
        if (gc.getInput().isKey(KeyEvent.VK_DOWN)) {
            r.setCamY(notExceed(r.getCamY() + DRAGSPEED, -gc.getHeight(), height * ts));
        }
    }

    private void putSomething(int tileCode, int x, int y) {
        if (elems[scroll].equals("spawn") && spawnExists()) {
            tileMap.setTile(tileMap.getSpawnX(), tileMap.getSpawnY(), 0);
            player.getEvent().respawn(tileMap.getSpawnX(), tileMap.getSpawnY()); // Reset player position
        }
        tileMap.setTile(x, y, tileCode);
    }

    private void delSomething(GameContainer gc, int x, int y) {
        gc.getWindow().setRubberCursor();
        if (x == tileMap.getSpawnX() && y == tileMap.getSpawnY()) {
            tileMap.setTile(tileMap.getSpawnX(), tileMap.getSpawnY(), 0);
            tileMap.resetSpawn();
        }
        tileMap.setTile(x, y, 0);
    }

    public static void setSpawn(boolean spawn) {
        Editor.spawn = spawn;
    }

    private boolean spawnExists() {
        return tileMap.getSpawnX() != -1 && tileMap.getSpawnY() != -1;
    }

    private boolean isDragging() {
        return dragOffX != -1 && dragOffY != -1;
    }

    private void startDragging(GameContainer gc) {
        gc.getWindow().setMovingCursor();
        dragOffX = gc.getInput().getMouseX();
        dragOffY = gc.getInput().getMouseY();
    }

    private void stopDragging(GameContainer gc) {
        gc.getWindow().setDefaultCursor();
        dragOffX = -1;
        dragOffY = -1;
    }

    private boolean isCtrlDown(GameContainer gc) {
        return gc.getInput().isKey(KeyEvent.VK_CONTROL);
    }

    private boolean isWheelDown(GameContainer gc) {
        return gc.getInput().isButton(MouseEvent.BUTTON2);
    }

    private int notExceed(int val, int min, int max) {
        return Math.max(Math.min(val, max), min);
    }
}
