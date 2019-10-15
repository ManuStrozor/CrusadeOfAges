package game;

import engine.GameContainer;
import engine.Renderer;
import engine.gfx.Image;
import engine.World;
import game.objects.Player;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class Editor extends AbstractGame {

    public static Image creaImg;
    private static boolean spawn = false;
    public static boolean once = true, newOne = true;
    public static String rename = "";

    public static World world;
    private Player player;

    private int width, height;

    private String[] elems = {
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

    public static int tileSize = GameManager.TS;
    private int toolSize = 24;
    private static final int DRAGSPEED = 3;
    private int dragX = -1, dragY = -1;
    private int tmpCamX = -1, tmpCamY = -1;
    private boolean zooming = false;

    public Editor() {
        world = new World();
        player = new Player("Tester", world, 999);
    }

    @Override
    public void update(GameContainer gc, float dt) {

        if (gc.getInputHandler().isKeyDown(KeyEvent.VK_ESCAPE)) gc.setActiView("pausedEdit");

        if (once) {
            if (newOne) creaImg = new Image(new int[60 * 30], 60, 30);
            world.init(creaImg);
            width = creaImg.getW();
            height = creaImg.getH();
            once = false;
        }

        if (spawn && spawnExists()) player.creativeUpdate(gc, dt); // Player update

        if (!gc.getInputHandler().isKey(KeyEvent.VK_CONTROL)) {
            /////////// Dock
            if (gc.getInputHandler().getScroll() > 0) {
                scroll = (scroll == elems.length - 1) ? 0 : scroll + 1;
            } else if (gc.getInputHandler().getScroll() < 0) {
                scroll = (scroll == 0) ? elems.length - 1 : scroll - 1;
            }
            /////////// Dock
        } else {
            /////////// Zoom - dézoom (CTRL + molette)
            if (gc.getInputHandler().getScroll() < 0) {
                Renderer.tileSize+=1;
                Player.tileSize+=1;
                tileSize+=1;
                zooming = true;
            } else if (gc.getInputHandler().getScroll() > 0) {
                if (Renderer.tileSize > 1) Renderer.tileSize-=1;
                if (Player.tileSize > 1) Player.tileSize-=1;
                if (tileSize > 1) tileSize-=1;
                zooming = true;
            }
            /////////// Zoom - dézoom (CTRL + molette)
        }

        color = world.getBloc(elems[scroll]).getCode();
    }

    @Override
    public void render(GameContainer gc, Renderer r) {

        if (gc.getActiView().equals("edit")) { // Bloquer l'editeur sur la view pausedEdit

            int mouseX = gc.getInputHandler().getMouseX();
            int mouseY = gc.getInputHandler().getMouseY();

            int x = (mouseX + r.getCamX()) / tileSize;
            int y = (mouseY + r.getCamY()) / tileSize;

            if (gc.getInputHandler().isKey(KeyEvent.VK_CONTROL) || gc.getInputHandler().isButton(MouseEvent.BUTTON2)) {
                ///////////////// Déplacement (CTRL + souris/molette)
                if (!isDragging()) {
                    tmpCamX = r.getCamX();
                    tmpCamY = r.getCamY();
                    ////////////// Zoom + dezoom
                    int zoom = gc.getInputHandler().getScroll();
                    if (zoom != 0 && zooming) {
                        int prevX, prevY;
                        if (zoom < 0) {
                            prevX = (mouseX + r.getCamX()) / (tileSize-1);
                            prevY = (mouseY + r.getCamY()) / (tileSize-1);
                            r.setCoorCam(r.getCamX() + prevX, r.getCamY() + prevY);
                        } else {
                            prevX = (mouseX + r.getCamX()) / (tileSize+1);
                            prevY = (mouseY + r.getCamY()) / (tileSize+1);
                            r.setCoorCam(r.getCamX() - prevX, r.getCamY() - prevY);
                        }
                        zooming = false;
                    }
                    ////////////// Zoom + dezoom
                    if (gc.getInputHandler().isButton(MouseEvent.BUTTON1) || gc.getInputHandler().isButton(MouseEvent.BUTTON2)) {
                        dragX = mouseX;
                        dragY = mouseY;
                    }
                } else {
                    int ddX = dragX - mouseX;
                    int ddY = dragY - mouseY;
                    int newCamX = notExceed(tmpCamX + ddX * DRAGSPEED, -gc.getWidth(), width * tileSize);
                    int newCamY = notExceed(tmpCamY + ddY * DRAGSPEED, -gc.getHeight(), height * tileSize);
                    r.setCoorCam(newCamX, newCamY);
                    if (!gc.getInputHandler().isButton(MouseEvent.BUTTON1)) { // Arret deplacement (CTRL + souris)
                        dragX = -1;
                        dragY = -1;
                    }
                }
                ///////////////// Déplacement (CTRL + souris/molette)
            } else {

                if (!gc.getInputHandler().isButton(MouseEvent.BUTTON2)) { // Arret deplacement (CTRL + molette)
                    dragX = -1;
                    dragY = -1;
                }

                if (gc.getInputHandler().isButton(MouseEvent.BUTTON1)) {
                    ////////// Placement des blocs
                    if (elems[scroll].equals("spawn") && spawnExists()) {
                        creaImg.setP(world.getSpawnX(), world.getSpawnY(), 0x00000000); // Delete previous spawn
                        world.setBloc(world.getSpawnX(), world.getSpawnY(), 0);
                        player.getEvent().respawn(world.getSpawnX(), world.getSpawnY()); // Reset player position
                    }
                    creaImg.setP(x, y, color);
                    world.setBloc(x, y, world.getBloc(elems[scroll]).getCode());
                    ////////// Placement des blocs
                } else if (gc.getInputHandler().isButton(MouseEvent.BUTTON3)) {
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

        r.drawWorld(world);
        if (creaImg != null) r.drawMiniMap(creaImg, 100);
        r.drawDock(world, elems, scroll, toolSize);
        if (creaImg != null) r.drawArrows(world, creaImg.getW(), creaImg.getH(), 32);

        if (spawn && spawnExists()) player.render(gc, r);
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
