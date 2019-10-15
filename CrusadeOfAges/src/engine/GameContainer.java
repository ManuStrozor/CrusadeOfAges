package engine;

import engine.audio.SoundClip;
import engine.gfx.Font;
import engine.gfx.Light;
import engine.view.*;
import game.AbstractGame;
import game.GameManager;
import game.Editor;
import game.objects.GameObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class GameContainer implements Runnable {

    private Thread thread;
    private Window window;
    private Renderer renderer;
    private GameManager gm;
    private InputHandler inputHandler;
    private Settings settings;
    private PlayerStats playerStats;
    private SoundClip hoverSound, clickSound, gameoverSound, impaleSound, leverSound;
    private AbstractGame game, editor;
    private Map<String, View> v = new HashMap<>();

    private boolean running = false;
    private int width, height;
    private float scale;
    private String title;
    private String prevView = null;
    private String actiView = "mainMenu"; // Ecran de départ !

    private static final String VERSION = "version 1.0";
    private static final String FACTORY = "Strozor Inc.";

    public GameContainer(AbstractGame game, Settings settings, World world, PlayerStats playerStats) {

        this.game = game;
        gm = (GameManager) game;
        this.settings = settings;
        this.playerStats = playerStats;
        editor = new Editor();
        v.put("creativeMode", new CreativeMode(settings, world));
        v.put("credits", new Credits(settings, world));
        v.put("gameOver", new GameOver(settings));
        v.put("gameSelection", new GameSelection(settings, world, game));
        v.put("inputDialog", new InputDialog(settings, world));
        v.put("mainMenu", new MainMenu(settings, world));
        v.put("options", new Options(settings, world));
        v.put("pausedEdit", new PausedEdit(settings));
        v.put("pausedGame", new PausedGame(settings));
        v.put("stats", new Stats(settings, world));

        hoverSound = new SoundClip("/audio/hover.wav");
        hoverSound.setVolume(-15f);
        clickSound = new SoundClip("/audio/click.wav");
        clickSound.setVolume(-15f);
        gameoverSound = new SoundClip("/audio/gameover.wav");
        impaleSound = new SoundClip("/audio/impaled.wav");
        leverSound = new SoundClip("/audio/lever.wav");
        leverSound.setVolume(-15f);
    }

    public synchronized void start() {
        window = new Window(this);
        renderer = new Renderer(this);
        inputHandler = new InputHandler(this);

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

                inputHandler.update();

                if (frameTime >= 1.0) {
                    frameTime = 0;
                    fps = frames;
                    frames = 0;
                }
            }

            GameObject go = gm.getObject("" + gm.getSocket().getLocalPort()); // Utile pour afficher le hud
            renderer.clear();

            // Affichage du jeu en arrière plan
            switch (actiView) {
                case "gameOver":
                case "pausedGame":
                    game.render(this, renderer);
                    renderer.setCoorCam(0, 0);
                    if (settings.isShowLights()) renderer.process();
                    if (go != null) renderer.drawHUD(go);
                    break;
                case "stats":
                    if (prevView.equals("pausedGame")) {
                        game.render(this, renderer);
                        renderer.setCoorCam(0, 0);
                        if (settings.isShowLights()) renderer.process();
                        if (go != null) renderer.drawHUD(go);
                    }
                    break;
            }

            // Affichage de la view active
            switch (actiView) {
                case "edit":
                    editor.render(this, renderer);
                    break;
                case "pausedEdit":
                    editor.render(this, renderer);
                    v.get("pausedEdit").render(this, renderer);
                    break;
                case "game":
                    game.render(this, renderer);
                    renderer.setCoorCam(0, 0);
                    if (settings.isShowLights()) renderer.process();
                    if (go != null) renderer.drawHUD(go);
                    break;
                case "creativeMode":
                    v.get("creativeMode").render(this, renderer);
                    renderer.setCoorCam(0, 0);
                    break;
                case "inputDialog":
                    v.get("creativeMode").render(this, renderer);
                    v.get("inputDialog").render(this, renderer);
                    break;
                case "exit":
                    break;
                default:
                    v.get(actiView).render(this, renderer);
            }

            // SI Lights = ON ALORS : Affichage d'une source de lumière à la position du curseur de la souris
            switch (actiView) {
                case "credits":
                case "mainMenu":
                case "options":
                    if (settings.isShowLights()) {
                        renderer.drawLight(new Light(150, 0xffffff99),
                                this.getInputHandler().getMouseX(), this.getInputHandler().getMouseY());
                    }
                    if (settings.isShowLights()) renderer.process();
                    break;
                case "stats":
                    if (prevView.equals("mainMenu")) {
                        if (settings.isShowLights()) {
                            renderer.drawLight(new Light(150, 0xffffff99),
                                    this.getInputHandler().getMouseX(), this.getInputHandler().getMouseY());
                        }
                        if (settings.isShowLights()) renderer.process();
                    }
                    break;
            }

            // Affichage des infos du programme en bas de l'écran
            switch (actiView) {
                case "credits":
                case "mainMenu":
                case "options":
                    renderer.drawText(VERSION, 0, getHeight(), 1, -1, 0xffababab, Font.STANDARD);
                    renderer.drawText(FACTORY, getWidth(), getHeight(), -1, -1, 0xffababab, Font.STANDARD);
                    break;
                case "stats":
                    if (prevView.equals("mainMenu")) {
                        renderer.drawText(VERSION, 0, getHeight(), 1, -1, 0xffababab, Font.STANDARD);
                        renderer.drawText(FACTORY, getWidth(), getHeight(), -1, -1, 0xffababab, Font.STANDARD);
                    }
                    break;
            }

            // Affichage des FPS
            if (settings.isShowFps()) {
                renderer.drawText(fps + " fps", getWidth(), 0, -1, 1, 0xffababab, Font.STANDARD);
            }

            window.update();
            frames++;

            // Envoi au serveur la view active
            if (!upState.equals(actiView)) {
                upState = actiView;
                try {
                    gm.getDos().writeUTF(gm.getSocket().getLocalPort() + " " + upState.toUpperCase());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        try {
            gm.getDis().close();
            gm.getDos().close();
            gm.getSocket().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        stop();
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

    public float getScale() {
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

    public InputHandler getInputHandler() {
        return inputHandler;
    }

    public Settings getSettings() {
        return settings;
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
