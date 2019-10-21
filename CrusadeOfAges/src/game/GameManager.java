package game;

import engine.*;
import engine.gfx.*;
import game.objects.*;

import javax.imageio.ImageIO;
import java.awt.event.KeyEvent;
import java.io.*;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class GameManager extends AbstractGame {

    public static final int TS = 32;
    public static int current = 0;
    public static String[][] levels = {
            {"/levels/Techsdale (TXDL).png", "Techsdale (TXDL)"},
            {"/levels/Vilcomen.png", "Vilcomen"}
    };

    private Socket socket;
    private DataInputStream dis;
    private DataOutputStream dos;
    private ArrayList<GameObject> objects = new ArrayList<>();;
    private ArrayList<Notification> notifs = new ArrayList<>();
    private Camera camera;
    private World world;

    public GameManager(Socket socket, World world) throws IOException {
        this.socket = socket;
        this.world = world;
        dis = new DataInputStream(socket.getInputStream());
        dos = new DataOutputStream(socket.getOutputStream());
    }

    @Override
    public void update(GameContainer gc, float dt) {
        String data;
        try {
            if (dis.available() > 0) {
                data = dis.readUTF();
                if (data.contains(":") && Integer.parseInt(data.split(":")[0]) != socket.getLocalPort()) {
                    if (getObject(data.split(":")[0]) == null) {
                        objects.add(new NetworkPlayer(data.split(":")[0], world, 1));
                    } else {
                        getObject(data.split(":")[0]).setPosX(Float.parseFloat(data.split(":")[1]));
                        getObject(data.split(":")[0]).setPosY(Float.parseFloat(data.split(":")[2]));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

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

        // Objects update
        for(int i = 0; i < objects.size(); i++) {
            if(objects.get(i).isDead()) {
                objects.remove(i--);
            } else {
                objects.get(i).update(gc, this, dt);
            }
        }

        // Load level
        if(getObject(""+socket.getLocalPort()) == null && (gc.getPrevView().equals("gameOver") || gc.getPrevView().equals("mainMenu"))) {
            load(levels[current][0]);
        }

        camera.update(gc, this, dt);
    }

    @Override
    public void render(GameContainer gc, Renderer r) {
        camera.render(r);
        r.drawWorld(world, true);
        if(gc.getSettings().isShowLights()) r.drawWorldLights(world, new Light(30, 0xffffff99));
        for(GameObject obj : objects) obj.render(gc, r);
        for(Notification notif : notifs) notif.render(gc, r);
    }

    /**
     * Loads a game level
     * @param path of the level image
     */
    public void load(String path) {
        world.init(new Image(path, false));
        objects.clear();
        objects.add(new Player(""+socket.getLocalPort(), world, 1));
        camera = new Camera(""+socket.getLocalPort(), world);
    }

    /**
     * Returns a GameObject associated with a tag
     * @param tag is the object identifier
     * @return GameObject
     */
    public GameObject getObject(String tag) {
        for(GameObject obj : objects) if(obj.getTag().equals(tag)) return obj;
        return null;
    }

    public Socket getSocket() {
        return socket;
    }

    public DataInputStream getDis() {
        return dis;
    }

    public DataOutputStream getDos() {
        return dos;
    }
}
