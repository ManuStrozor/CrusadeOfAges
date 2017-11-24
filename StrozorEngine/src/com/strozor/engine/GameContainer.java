package com.strozor.engine;

import com.strozor.game.GameManager;
import com.strozor.game.Menu;
import com.strozor.game.Options;
import com.strozor.game.Pause;

public class GameContainer implements Runnable {

    private Thread thread;
    private Window window;
    private GameRender gameRender;
    private GameManager gm;
    private Input input;
    private Settings settings;
    private AbstractGame menu, opt, game, pause;

    private boolean running = false;
    private int width, height;
    private float scale;
    private String title;

    private enum STATE{
        MENU,
        OPT,
        GAME,
        PAUSE,
        EXIT
    }

    public STATE State = STATE.MENU;
    private int lastState = 0;

    public GameContainer(AbstractGame game) {
        this.menu = new Menu();
        this.settings = new Settings();
        this.opt = new Options(settings);
        this.game = game;
        this.gm = (GameManager) game;
        this.pause = new Pause();
    }

    public synchronized void start() {
        window = new Window(this);
        gameRender = new GameRender(this);
        input = new Input(this);

        thread = new Thread(this);
        thread.run();
    }

    private synchronized void stop() {
        try {
            thread.join();
            running = false;
            System.exit(0);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void run() {
        boolean render;
        double startTime, passedTime, frameTime = 0, unprocessedTime = 0;
        double lastTime = System.nanoTime() / 1000000000.0;
        int fps = 0, frames = 0;
        double UPDATE_CAP = 1.0/60.0;

        running = true;

        while(running && State != STATE.EXIT) {
            render = false;

            startTime = System.nanoTime() / 1000000000.0;
            passedTime = startTime - lastTime;
            lastTime = startTime;

            unprocessedTime += passedTime;
            frameTime += passedTime;

            while(unprocessedTime >= UPDATE_CAP) {
                unprocessedTime -= UPDATE_CAP;
                render = true;

                if(State == STATE.MENU) {
                    menu.update(this, (float)UPDATE_CAP);
                } else if(State == STATE.OPT) {
                    opt.update(this, (float)UPDATE_CAP);
                } else if(State == STATE.GAME) {
                    game.update(this, (float)UPDATE_CAP);
                } else if(State == STATE.PAUSE) {
                    pause.update(this, (float)UPDATE_CAP);
                }

                input.update();

                if(frameTime >= 1.0) {
                    frameTime = 0;
                    fps = frames;
                    frames = 0;
                }
            }

            if(render) {
                gameRender.clear();

                if(State == STATE.GAME || State == STATE.PAUSE || (State == STATE.OPT && lastState == 2)) {
                    game.render(this, gameRender);
                    gameRender.setCamX(0);
                    gameRender.setCamY(0);
                    if (settings.isShowLights())
                        gameRender.process();
                    if(gm.getObject("player") != null)
                        gameRender.drawGameStates(gm);
                }

                if(State == STATE.MENU) {
                    menu.render(this, gameRender);
                } else if(State == STATE.OPT) {
                    opt.render(this, gameRender);
                } else if(State == STATE.PAUSE) {
                    pause.render(this, gameRender);
                }

                if(State == STATE.MENU || (State == STATE.OPT && lastState == 0)) {
                    gameRender.drawText(title + " Beta1.8", 0, getHeight(), 1, -1, 0xffababab);
                    gameRender.drawText("Strozor INC.", getWidth(), getHeight(), -1, -1, 0xffababab);
                }

                if(settings.isShowFps())
                    gameRender.drawText(fps + "fps", getWidth(), 0, -1, 1, 0xffababab);

                window.update();
                frames++;
            } else {
                try {
                    Thread.sleep(1);
                } catch(InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        stop();
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
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

    public Input getInput() {
        return input;
    }

    public Settings getSettings() {
        return settings;
    }

    public int getLastState() {
        return lastState;
    }

    public void setLastState(int lastState) {
        this.lastState = lastState;
    }

    public void setState(int value) {
        switch(value) {
            case -1: State = STATE.EXIT; break;
            case 0: State = STATE.MENU; break;
            case 1: State = STATE.GAME; break;
            case 2: State = STATE.PAUSE; break;
            case 3: State = STATE.OPT; break;
        }
    }
}
