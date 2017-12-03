package com.strozor.engine;

import com.strozor.engine.gfx.Font;
import com.strozor.game.GameManager;
import com.strozor.game.Crea;
import com.strozor.view.*;

public class GameContainer implements Runnable {

    private Thread thread;
    private Window window;
    private GameRender gameRender;
    private GameManager gm;
    private Input input;
    private Settings settings;
    private View mainMenu, optsMenu, gameMenu, creaMenu, overMenu, credits;
    private AbstractGame game, crea;



    private boolean running = false;
    private int width, height;
    private float scale;
    private String title;

    private enum STATE{
        MAINMENU,
        OPTSMENU,
        GAME,
        GAMEMENU,
        OVERMENU,
        CREA,
        CREAMENU,
        CREDITS,
        EXIT
    }

    public STATE State = STATE.MAINMENU;
    private int currState = 0, lastState = 0;

    public GameContainer(AbstractGame game) {
        this.game = game;
        this.settings = new Settings();

        this.mainMenu = new MainMenu(settings);
        this.optsMenu = new OptsMenu(settings);
        this.gameMenu = new GameMenu(settings);
        this.overMenu = new OverMenu(settings);
        this.creaMenu = new CreaMenu(settings);
        this.credits = new Credits(settings);

        this.crea = new Crea(60, 30);
        this.gm = (GameManager) game;
    }

    public synchronized void start() {
        window = new Window(this);
        gameRender = new GameRender(this, settings);
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

                if(State == STATE.MAINMENU) {
                    mainMenu.update(this, (float)UPDATE_CAP);
                } else if(State == STATE.CREDITS) {
                    credits.update(this, (float)UPDATE_CAP);
                } else if(State == STATE.OPTSMENU) {
                    optsMenu.update(this, (float)UPDATE_CAP);
                } else if(State == STATE.GAMEMENU) {
                    gameMenu.update(this, (float)UPDATE_CAP);
                } else if(State == STATE.CREAMENU) {
                    creaMenu.update(this, (float)UPDATE_CAP);
                } else if(State == STATE.OVERMENU) {
                    overMenu.update(this, (float)UPDATE_CAP);
                } else if(State == STATE.GAME) {
                    game.update(this, (float)UPDATE_CAP);
                } else if(State == STATE.CREA) {
                    crea.update(this, (float)UPDATE_CAP);
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

                if(State == STATE.GAME || State == STATE.GAMEMENU || State == STATE.OVERMENU || (State == STATE.OPTSMENU && lastState == 2)) {
                    game.render(this, gameRender);
                    gameRender.setCoorCam(0, 0);
                    if(settings.isShowLights())
                        gameRender.process();
                    if(gm.getObject("player") != null) gameRender.drawGameStates(gm.getObject("player"));
                } else if(State == STATE.CREA || State == STATE.CREAMENU) {
                    crea.render(this, gameRender);
                }

                if(State == STATE.MAINMENU) {
                    mainMenu.render(this, gameRender);
                    gameRender.setCoorCam(0, 0);
                } else if(State == STATE.CREDITS) {
                    credits.render(this, gameRender);
                } else if(State == STATE.OPTSMENU) {
                    optsMenu.render(this, gameRender);
                } else if(State == STATE.GAMEMENU) {
                    gameMenu.render(this, gameRender);
                } else if(State == STATE.CREAMENU) {
                    creaMenu.render(this, gameRender);
                } else if(State == STATE.OVERMENU) {
                    overMenu.render(this, gameRender);
                }

                if(State == STATE.MAINMENU || (State == STATE.OPTSMENU && lastState == 0)) {
                    gameRender.drawText(title + " Beta1.9", 0, getHeight(), 1, -1, 0xffababab, Font.STANDARD);
                    gameRender.drawText("Strozor Inc.", getWidth(), getHeight(), -1, -1, 0xffababab, Font.STANDARD);
                }

                if(settings.isShowFps())
                    gameRender.drawText(fps + "fps", getWidth(), 0, -1, 1, 0xffababab, Font.STANDARD);

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

    public int getCurrState() {
        return currState;
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
            case 0: State = STATE.MAINMENU; break;
            case 1: State = STATE.GAME; break;
            case 2: State = STATE.GAMEMENU; break;
            case 3: State = STATE.OPTSMENU; break;
            case 4: State = STATE.CREA; break;
            case 5: State = STATE.CREAMENU; break;
            case 6: State = STATE.CREDITS; break;
            case 7: State = STATE.OVERMENU; break;
        }
        currState = value;
    }
}
