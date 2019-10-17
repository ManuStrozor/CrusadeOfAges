package game;

import engine.GameContainer;
import engine.Renderer;
import engine.Settings;
import engine.gfx.Image;
import engine.World;
import game.objects.Player;

import javax.imageio.ImageIO;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Editor extends AbstractGame {

    public static Image creaImg;
    public static World world;
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
    private int color, scroll = 0;

    public static int ts = GameManager.TS;
    private int toolSize = 32;
    private static final int DRAGSPEED = 3;
    private int dragX = -1, dragY = -1;
    private int tmpCamX = -1, tmpCamY = -1;

    private ArrayList<Notification> notifs = new ArrayList<>();

    public Editor(Settings settings, World world) {
        this.settings = settings;
        Editor.world = world;
        player = new Player("Tester", world, 999);
    }

    @Override
    public void update(GameContainer gc, float dt) {

        if (gc.getInput().isKeyDown(KeyEvent.VK_ESCAPE)) gc.setActiView("pausedEdit");

        if (once) {
            if (newOne) creaImg = new Image(new int[60 * 30], 60, 30);
            world.init(creaImg);
            width = creaImg.getW();
            height = creaImg.getH();
            once = false;
        }

        if (spawn && spawnExists()) player.creativeUpdate(gc, dt); // Player update

        int wheel = gc.getInput().getScroll();
        if (!gc.getInput().isKey(KeyEvent.VK_CONTROL) && gc.getInput().getMouseX() < toolSize + 4) {
            /////////// Dock
            if (wheel > 0) {
                scroll = (scroll == elems.length - 1) ? 0 : scroll + 1;
            } else if (wheel < 0) {
                scroll = (scroll == 0) ? elems.length - 1 : scroll - 1;
            }
            /////////// Dock
        } else if (gc.getInput().isKey(KeyEvent.VK_CONTROL)) {
            /////////// ZOOM IN - ZOOM OUT (CTRL + molette)
            if (wheel < 0) { // ZOOM IN
                gc.getR().zoomIn(gc.getInput().getMouseX(), gc.getInput().getMouseY(), 1);
                Player.tileSize += 1;
                ts += 1;
            } else if (wheel > 0) { // ZOOM OUT
                gc.getR().zoomOut(gc.getInput().getMouseX(), gc.getInput().getMouseY(), 1);
                if (Player.tileSize > 1) Player.tileSize -= 1;
                if (ts > 1) ts -= 1;
            }
            /////////// ZOOM IN - ZOOM OUT (CTRL + molette)
            /////////// Sauvegarde (CTRL + S)
            if (gc.getInput().isKeyUp(KeyEvent.VK_S)) {
                creaImg.save(rename);
                notifs.add(new Notification(rename + settings.translate(" a été sauvegardé avec succès")));
            }
            /////////// Sauvegarde (CTRL + S)
        } else {
            if (wheel < 0) {
                gc.getR().setCamY(notExceed(gc.getR().getCamY() - 2* ts, -gc.getHeight(), height * ts));
            } else if (wheel > 0) {
                gc.getR().setCamY(notExceed(gc.getR().getCamY() + 2* ts, -gc.getHeight(), height * ts));
            }
        }

        color = world.getBloc(elems[scroll]).getCode();

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

        //Notifications update
        for (int i = 0; i < notifs.size(); i++) {
            notifs.get(i).update(dt);
            if (notifs.get(i).isEnded()) {
                notifs.remove(i--);
            }
        }
    }

    @Override
    public void render(GameContainer gc, Renderer r) {

        if (gc.getActiView().equals("edit")) { // Bloquer l'editeur sur la view pausedEdit

            int x = (gc.getInput().getMouseX() + r.getCamX()) / ts;
            int y = (gc.getInput().getMouseY() + r.getCamY()) / ts;

            /////////////// Déplacement Clavier
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
            /////////////// Déplacement Clavier


            if (gc.getInput().isKey(KeyEvent.VK_CONTROL) || gc.getInput().isButton(MouseEvent.BUTTON2)) {
                gc.getWindow().setMovingCursor();
                ///////////////// Déplacement (CTRL + souris/molette)
                if (!isDragging()) {
                    tmpCamX = r.getCamX();
                    tmpCamY = r.getCamY();
                    if (gc.getInput().isButton(MouseEvent.BUTTON1) || gc.getInput().isButton(MouseEvent.BUTTON2)) {
                        dragX = gc.getInput().getMouseX();
                        dragY = gc.getInput().getMouseY();
                    }
                } else {
                    r.setCamX(notExceed(tmpCamX + (dragX - gc.getInput().getMouseX()) * DRAGSPEED, -gc.getWidth(), width * ts));
                    r.setCamY(notExceed(tmpCamY + (dragY - gc.getInput().getMouseY()) * DRAGSPEED, -gc.getHeight(), height * ts));
                    if (!gc.getInput().isButton(MouseEvent.BUTTON1) &&
                            !gc.getInput().isButton(MouseEvent.BUTTON2)) { // Arret deplacement (CTRL + souris)
                        gc.getWindow().setDefaultCursor();
                        dragX = -1;
                        dragY = -1;
                    }
                }
                ///////////////// Déplacement (CTRL + souris/molette)
            } else {

                if (!gc.getInput().isButton(MouseEvent.BUTTON2) &&
                        !gc.getInput().isButton(MouseEvent.BUTTON3)) { // Arret deplacement (CTRL + molette)
                    gc.getWindow().setDefaultCursor();
                    dragX = -1;
                    dragY = -1;
                }

                if (!world.getBloc(elems[scroll]).isTagged("free")) {
                    if (gc.getInput().isButton(MouseEvent.BUTTON1)) {
                        ////////// Placement des blocs
                        if (elems[scroll].equals("spawn") && spawnExists()) {
                            creaImg.setP(world.getSpawnX(), world.getSpawnY(), 0x00000000); // Delete previous spawn
                            world.setBloc(world.getSpawnX(), world.getSpawnY(), 0);
                            player.getEvent().respawn(world.getSpawnX(), world.getSpawnY()); // Reset player position
                        }
                        creaImg.setP(x, y, color);
                        world.setBloc(x, y, color);
                        ////////// Placement des blocs
                    } else if (gc.getInput().isButton(MouseEvent.BUTTON3)) {
                        gc.getWindow().setRubberCursor();
                        ////////// Effacement des blocs
                        if (x == world.getSpawnX() && y == world.getSpawnY()) {
                            creaImg.setP(world.getSpawnX(), world.getSpawnY(), 0x00000000); // Delete spawn
                            world.setBloc(world.getSpawnX(), world.getSpawnY(), 0);
                            world.resetSpawn();
                        }
                        creaImg.setP(x, y, 0x00000000);
                        world.setBloc(x, y, 0);
                        ////////// Effacement des blocs
                    }
                }
            }
        }

        r.drawWorld(world);
        if (creaImg != null) r.drawMiniMap(creaImg, 100);
        r.drawDock(world, elems, scroll, toolSize);
        if (creaImg != null) r.drawArrows(world, creaImg.getW(), creaImg.getH(), 32);

        if (spawn && spawnExists()) player.render(gc, r);

        for (Notification notif : notifs) notif.render(gc, r);
    }

    public static void setSpawn(boolean spawn) {
        Editor.spawn = spawn;
    }

    private boolean spawnExists() {
        return world.getSpawnX() != -1 && world.getSpawnY() != -1;
    }

    private boolean isDragging() {
        return dragX != -1 && dragY != -1;
    }

    private int notExceed(int val, int min, int max) {
        return Math.max(Math.min(val, max), min);
    }
}
