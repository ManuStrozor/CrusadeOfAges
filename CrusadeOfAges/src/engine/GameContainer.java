package engine;

import engine.audio.SoundClip;
import engine.gfx.Font;
import engine.gfx.Light;
import engine.view.*;
import game.GameManager;
import game.Editor;
import game.objects.GameObject;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;


public class GameContainer implements Runnable {

    private Thread thread;
    private Window window;
    private Renderer r;
    private InputHandler input;
    private GameManager game;
    private Editor editor;
    private World world;
    private Settings settings;
    private PlayerStats playerStats;
    private SoundClip hoverSound, clickSound, gameoverSound, impaleSound, leverSound;
    private Map<String, View> v = new HashMap<>();

    private boolean running = false;
    private int width, height;
    private float scale;
    private String title;
    private String prevView = null;
    private String actiView = "mainMenu"; // Ecran de départ !

    private static final String FACTORY = "Strozor Inc.";

    public GameContainer(Socket socket, Settings settings) throws IOException {
        world = new World();
        this.settings = settings;
        game = new GameManager(world, socket);
        playerStats = new PlayerStats();
        editor = new Editor(world);

        Confirm confirmView = new Confirm();
        v.put("confirmExit", confirmView);

        v.put("mainMenu", new MainMenu((Confirm) v.get("confirmExit")));
        v.put("gameSelection", new GameSelection());
        v.put("lobby", new Lobby());
        v.put("creativeMode", new CreativeMode());
        v.put("inputDialog", new InputDialog());
        v.put("options", new Options());
        v.put("stats", new Stats());
        v.put("credits", new Credits());
        v.put("pausedEdit", new PausedEdit());
        v.put("pausedGame", new PausedGame());
        v.put("gameOver", new GameOver());

        hoverSound = new SoundClip("/audio/hover.wav", -10f);
        clickSound = new SoundClip("/audio/click.wav", -10f);
        gameoverSound = new SoundClip("/audio/gameover.wav", -5f);
        impaleSound = new SoundClip("/audio/impaled.wav");
        leverSound = new SoundClip("/audio/lever.wav", -15f);
    }

    public synchronized void start() {
        window = new Window(this);
        r = new Renderer(this, settings);
        input = new InputHandler(this);

        thread = new Thread(this);
        thread.run();
    }

    private synchronized void stop() {
        try {
            thread.join();
            running = false;
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void run() {
        String upState = "exit";
        double startTime, passedTime, frameTime = 0, unprocessedTime = 0;
        double lastTime = System.nanoTime() / 1000000000.0;
        int fps = 0, frames = 0;
        double UPDATE_CAP = 1.0 / 60.0;

        running = true;

        while (running && !actiView.equals("exit")) {

            startTime = System.nanoTime() / 1000000000.0;
            passedTime = startTime - lastTime;
            lastTime = startTime;

            unprocessedTime += passedTime;
            frameTime += passedTime;


            while (unprocessedTime >= UPDATE_CAP) {
                unprocessedTime -= UPDATE_CAP;

                switch (actiView) {
                    case "edit":
                        editor.update(this, (float) UPDATE_CAP);
                        break;
                    case "game":
                        game.update(this, (float) UPDATE_CAP);
                        break;
                    case "exit":
                        break;
                    default:
                        v.get(actiView).update(this, (float) UPDATE_CAP);
                }

                input.update();

                if (frameTime >= 1.0) {
                    frameTime = 0;
                    fps = frames;
                    frames = 0;
                }
            }

            GameObject go = game.getObject("" + game.getSocket().getLocalPort()); // Utile pour afficher le hud
            r.clear();

            // Affichage du jeu en arrière plan
            switch (actiView) {
                case "gameOver":
                case "pausedGame":
                    game.render(this, r);
                    r.setCoorCam(0, 0);
                    if (settings.isShowLights()) r.process();
                    if (go != null) r.drawHUD(go);
                    break;
                case "stats":
                    if (prevView.equals("pausedGame")) {
                        game.render(this, r);
                        r.setCoorCam(0, 0);
                        if (settings.isShowLights()) r.process();
                        if (go != null) r.drawHUD(go);
                    }
                    break;
            }

            // Affichage de la view active
            switch (actiView) {
                case "edit":
                    editor.render(this, r);
                    break;
                case "pausedEdit":
                    editor.render(this, r);
                    v.get("pausedEdit").render(this, r);
                    break;
                case "game":
                    game.render(this, r);
                    r.setCoorCam(0, 0);
                    if (settings.isShowLights()) r.process();
                    if (go != null) r.drawHUD(go);
                    break;
                case "creativeMode":
                    v.get("creativeMode").render(this, r);
                    r.setCoorCam(0, 0);
                    break;
                case "inputDialog":
                    v.get("creativeMode").render(this, r);
                    v.get("inputDialog").render(this, r);
                    break;
                case "confirmExit":
                    v.get("mainMenu").render(this, r);
                    v.get("confirmExit").render(this, r);
                    break;
                case "exit":
                    break;
                default:
                    v.get(actiView).render(this, r);
            }

            // SI Lights = ON ALORS : Affichage d'une source de lumière à la position du curseur de la souris
            switch (actiView) {
                case "lobby":
                case "credits":
                case "mainMenu":
                case "options":
                    if (settings.isShowLights()) {
                        r.drawLight(new Light(150, 0xffffff99),
                                this.getInput().getMouseX(), this.getInput().getMouseY());
                    }
                    if (settings.isShowLights()) r.process();
                    break;
                case "stats":
                    if (prevView.equals("mainMenu")) {
                        if (settings.isShowLights()) {
                            r.drawLight(new Light(150, 0xffffff99),
                                    this.getInput().getMouseX(), this.getInput().getMouseY());
                        }
                        if (settings.isShowLights()) r.process();
                    }
                    break;
            }

            // Affichage FACTORY en bas de l'écran
            switch (actiView) {
                case "credits":
                case "mainMenu":
                case "options":
                    r.drawText(FACTORY, getWidth()/2, getHeight(), 0, -1, 0xffababab, Font.STANDARD);
                    break;
                case "stats":
                    if (prevView.equals("mainMenu")) {
                        r.drawText(FACTORY, getWidth()/2, getHeight(), 0, -1, 0xffababab, Font.STANDARD);
                    }
                    break;
            }

            // Affichage des FPS
            if (settings.isShowFps()) {
                r.drawText(fps + " fps", getWidth(), 0, -1, 1, 0xffababab, Font.STANDARD);
            }

            window.update();
            frames++;

            // Envoi au serveur la view active
            if (!upState.equals(actiView)) {
                upState = actiView;
                try {
                    game.getDos().writeUTF(game.getSocket().getLocalPort() + " " + upState.toUpperCase());
                } catch (IOException e) {
                    System.out.println("[IOException] " + e.getMessage());
                }
            }
        }

        try {
            game.getDis().close();
            game.getDos().close();
            game.getSocket().close();
        } catch (IOException e) {
            System.out.println("[IOException] " + e.getMessage());
        }
        stop();
    }

    public GameManager getGame() {
        return game;
    }

    public Renderer getR() {
        return r;
    }

    public World getWorld() {
        return world;
    }

    public Settings getSettings() {
        return settings;
    }

    public int getWidth() {
        return width;
    }

    void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    void setHeight(int height) {
        this.height = height;
    }

    float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Window getWindow() {
        return window;
    }

    public InputHandler getInput() {
        return input;
    }

    public PlayerStats getPlayerStats() {
        return playerStats;
    }

    public SoundClip getHoverSound() {
        return hoverSound;
    }

    public SoundClip getClickSound() {
        return clickSound;
    }

    public SoundClip getGameoverSound() {
        return gameoverSound;
    }

    public SoundClip getImpaleSound() {
        return impaleSound;
    }

    public SoundClip getLeverSound() {
        return leverSound;
    }

    public String getPrevView() {
        return prevView;
    }

    public String getActiView() {
        return actiView;
    }

    public void setActiView(String value) {
        prevView = actiView;
        actiView = value;
    }
}
