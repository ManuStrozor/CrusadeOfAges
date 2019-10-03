package sm.game;

import sm.engine.*;
import sm.engine.gfx.Image;
import sm.engine.gfx.Light;
import sm.game.objects.GameObject;
import sm.game.objects.NetworkPlayer;
import sm.game.objects.Player;

import javax.imageio.ImageIO;
import java.awt.event.KeyEvent;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class Game extends AbstractGame {

    public static final int TS = 32;
    public static int current = 0;
    public static String APPDATA;
    public static String[][] levels = {
            {"/levels/0.png", "Learn to jump"},
            {"/levels/1.png", "Trampoline room"},
            {"/levels/2.png", "Go on slowly..."},
            {"/levels/3.png", "Crown of thorns"}
    };
    public static String[] dataStates = {
            "Level up",
            "Death",
            "Game over",
            "Lever pulled",
            "Slime",
            "Jump"
    };

    private Socket socket;
    private DataInputStream dis;
    private DataOutputStream dos;

    private ArrayList<GameObject> objects = new ArrayList<>();
    private ArrayList<Notification> notifs = new ArrayList<>();
    private Camera camera;
    private World world;

    private String data;

    private Game(Socket socket, World world) throws IOException {
        this.socket = socket;
        dis = new DataInputStream(socket.getInputStream());
        dos = new DataOutputStream(socket.getOutputStream());
        this.world = world;
    }

    public ArrayList<Notification> getNotifs() {
        return notifs;
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

    @Override
    public void update(GameContainer gc, float dt) {

        try {
            if (dis.available() > 0) {
                data = dis.readUTF();
                //notifs.add(new Notification(data, 1, 300, -1));
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

        if(gc.getInputHandler().isKeyDown(KeyEvent.VK_ESCAPE)) gc.setState(2);

        // Screenshots
        if(gc.getInputHandler().isKeyDown(KeyEvent.VK_F12)) {
            try {
                DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");
                String filename = sdf.format(new Date()) + ".png";
                File out = new File(APPDATA + "\\screenshots\\" + filename);
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
        if(getObject(""+socket.getLocalPort()) == null && (gc.getLastState() == 7 || gc.getLastState() == 0)) {
            load(levels[current][0]);
        }

        camera.update(gc, this, dt);
    }

    @Override
    public void render(GameContainer gc, Renderer r) {
        camera.render(r);
        r.drawWorld(world);
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
     * @param tag
     * @return GameObject
     */
    public GameObject getObject(String tag) {
        for(GameObject obj : objects) if(obj.getTag().equals(tag)) return obj;
        return null;
    }

    /**
     * Writes all game files in the right place
     * Windows : AppData
     * Linux : Current directory
     */
    private static boolean writeAppData(File appdata) {

        // .squaremonster
        File smFolder = new File(APPDATA + "/.squaremonster");
        if(!smFolder.exists()) {
            if (!appdata.canWrite()) {
                System.out.println("Impossible de créer le dossier .squaremonster");
                return false;
            } else if(!smFolder.mkdir()) {
                System.out.println("Dossier .squaremonster déjà existant");
            }
        }
        APPDATA += "/.squaremonster";

        //assets
        File smAssets = new File(APPDATA + "/assets");
        if(!smAssets.exists()) {
            if (!smFolder.canWrite()) {
                System.out.println("Impossible de créer le dossier assets");
                return false;
            } else if(!smAssets.mkdir()) {
                System.out.println("Dossier assets déjà existant");
            }
        }

        //screenshots
        File smScreenshots = new File(APPDATA + "/screenshots");
        if(!smScreenshots.exists()) {
            if (!smFolder.canWrite()) {
                System.out.println("Impossible de créer le dossier screenshots");
                return false;
            } else if (!smScreenshots.mkdir()) {
                System.out.println("Dossier screenshots déjà existant");
            }
        }

        //creative_mode
        File smCrea = new File(APPDATA + "/creative_mode");
        if(!smCrea.exists()) {
            if (!smFolder.canWrite()) {
                System.out.println("Impossible de créer le dossier creative_mode");
                return false;
            } else if (!smCrea.mkdir()) {
                System.out.println("Dossier creative_mode déjà existant");
            }
        }

        //objects.png (assets)
        File outObjs = new File(APPDATA + "/assets/objects.png");
        File outPl = new File(APPDATA + "/assets/player.png");
        try {
            if(!outObjs.exists() && smAssets.canWrite())
                ImageIO.write(ImageIO.read(Image.class.getResourceAsStream("/objects.png")), "png", outObjs);
            if(!outPl.exists() && smAssets.canWrite())
                ImageIO.write(ImageIO.read(Image.class.getResourceAsStream("/player.png")), "png", outPl);
        } catch(IOException e) {
            e.printStackTrace();
        }

        //options.txt
        File smOptFile = new File(APPDATA + "/options.txt");
        if(!smOptFile.exists() && smFolder.canWrite()) {
            try {
                List<String> lines = Arrays.asList(
                        "lang:fr",
                        "guiScale:3",
                        "showFPS:false",
                        "showLights:true"
                );
                Path path = Paths.get(APPDATA + "/options.txt");
                Files.write(path, lines, StandardCharsets.UTF_8);
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
        
        //player.dat
        File smStatsFile = new File(APPDATA + "/player.dat");
        if(!smStatsFile.exists() && smFolder.canWrite()) {
            try {
                Path path = Paths.get(APPDATA + "/player.dat");
                Files.createFile(path);

                FileOutputStream fos = new FileOutputStream(APPDATA + "/player.dat");
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                for(String ds : dataStates) {
                    oos.writeUTF(ds);
                    oos.writeInt(0);
                }
                oos.close();
            } catch(Exception e) {
                e.printStackTrace();
            }
        }

        return true;
    }

    /**
     * Reads options file and serialize the content
     * @param s
     */
    private static void readOptions(Settings s) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(APPDATA + "/options.txt"));
            String line = br.readLine();
            while (line != null) {
                String[] sub = line.split(":");
                switch(sub[0]) {
                    case "lang":
                        if ("fr".equals(sub[1])) {
                            s.setLangIndex(1);
                        } else {
                            s.setLangIndex(0);
                        }
                        break;
                    case "guiScale": s.setScale(Float.parseFloat(sub[1])); break;
                    case "showFPS": s.setShowFps(sub[1].equals("true")); break;
                    case "showLights": s.setShowLights(sub[1].equals("true")); break;
                }
                line = br.readLine();
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Starts the game
     * @param args
     */
    public static void main(String[] args) throws IOException {

        String OS = (System.getProperty("os.name")).toUpperCase();
        if (OS.contains("WIN")) {
            APPDATA = System.getenv("AppData");
        } else {
            APPDATA = System.getProperty("user.dir");
        }
        //System.out.println("Game Folder: " + APPDATA);

        Settings s = new Settings();
        World world = new World();
        if (!writeAppData(new File(APPDATA))) return;
        readOptions(s);
        GameContainer gc = new GameContainer(new Game(new Socket("localhost", 5338), world), s, world, new DataStats());
        gc.setTitle("Square Monster");
        gc.setScale(s.getScale());
        gc.start();
    }
}
