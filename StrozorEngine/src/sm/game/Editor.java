package sm.game;

import sm.engine.GameContainer;
import sm.engine.Renderer;
import sm.engine.gfx.Image;
import sm.engine.World;
import sm.game.objects.Player;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class Editor extends AbstractGame {

    public static Image creaImg;
    public static boolean spawn = false, once = false, newOne = false;
    public static String rename = "";

    private World world;
    private Player player;

    private int width, height;

    private String[] elems = {
            "spawn",
            "floor",
            "slime",
            "ladder",
            "ground spikes",
            "ceiling spikes",
            "pill",
            "coin",
            "key",
            "skull",
            "lever left",
            "torch",
            "door"
    };
    private int color, scroll = 0;

    public Editor(int width, int height) {
        world = new World();
        player = new Player("Tester", world, 999);
        creaImg = new Image(new int[width * height], width, height);
        this.width = width;
        this.height = height;
    }

    @Override
    public void update(GameContainer gc, float dt) {

        if(!once) {
            if(newOne) {
                creaImg = null;
                creaImg = new Image(new int[width * height], width, height);
            }
            world.init(creaImg);
            once = true;
        }

        if(!spawn && (world.getSpawnX() != -1 || world.getSpawnY() != -1)) {
            player = new Player("player", world, 1);
            spawn = true;
        }

        if(gc.getInputHandler().isKeyDown(KeyEvent.VK_ESCAPE)) gc.setState(5);

        //Player update
        if(spawn && (world.getSpawnX() != -1 || world.getSpawnY() != -1))
            player.creativeUpdate(gc, dt);

        if(gc.getInputHandler().getScroll() > 0)
            scroll = (scroll == elems.length - 1) ? 0 : scroll + 1;
        else if(gc.getInputHandler().getScroll() < 0)
            scroll = (scroll == 0) ? elems.length - 1 : scroll - 1;

        color = world.getCol(elems[scroll]);
    }

    @Override
    public void render(GameContainer gc, Renderer r) {

        if(gc.getCurrState() == 4) {
            int mouseX = gc.getInputHandler().getMouseX();
            int mouseY = gc.getInputHandler().getMouseY();

            int x = (mouseX + r.getCamX()) / Game.TS;
            int y = (mouseY + r.getCamY()) / Game.TS;

            int speed = 10;

            if(r.getCamX() + gc.getWidth() < creaImg.getW() * Game.TS) {
                if(mouseX == gc.getWidth() - 1) r.setCamX(r.getCamX() + speed);
            }
            if(r.getCamX() > -Game.TS) {
                if(mouseX == 0) r.setCamX(r.getCamX() - speed);
            }
            if(r.getCamY() + gc.getHeight() < creaImg.getH() * Game.TS) {
                if(mouseY == gc.getHeight() - 1) r.setCamY(r.getCamY() + speed);
            }
            if(r.getCamY() > 0) {
                if(mouseY == 0) r.setCamY(r.getCamY() - speed);
            }

            if(mouseX > Game.TS) {
                if(gc.getInputHandler().isButton(MouseEvent.BUTTON1)) {

                    if(elems[scroll].equals("spawn") && world.getSpawnX() != -1 && world.getSpawnY() != -1) {

                        //Delete previous spawn
                        creaImg.setP(world.getSpawnX(), world.getSpawnY(), 0x00000000);
                        world.setBloc(world.getSpawnX(), world.getSpawnY(), 0);

                        //create new spawn
                        creaImg.setP(x, y, color);

                        //Reset player position
                        player.getEvent().respawn(world.getSpawnX(), world.getSpawnY());
                    } else {
                        creaImg.setP(x, y, color);
                    }

                    world.setBloc(x, y, world.getCol(elems[scroll]));

                } else if(gc.getInputHandler().isButton(MouseEvent.BUTTON3)) {
                    creaImg.setP(x, y, 0x00000000);
                    world.setBloc(x, y, 0);
                }
            }
        }

        r.drawWorld(world);
        r.drawMiniMap(gc, creaImg);
        r.drawDock(gc, world, elems, scroll);
        r.drawArrows(gc, world, creaImg.getW(), creaImg.getH());

        if(spawn && (world.getSpawnX() != -1 || world.getSpawnY() != -1))
            player.render(gc, r);
    }
}
