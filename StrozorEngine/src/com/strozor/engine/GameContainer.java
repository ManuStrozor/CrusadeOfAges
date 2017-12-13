package com.strozor.engine;

import com.strozor.engine.gfx.Font;
import com.strozor.game.GameManager;
import com.strozor.game.Edit;
import com.strozor.view.*;

public class GameContainer implements Runnable {

    private Thread thread;
    private Window window;
    private GameRender gameRender;
    private GameManager gm;
    private Input input;
    private Settings s;
    private Data data;
    private View
            mainMenu,
            options,
            pausedGame,
            pausedEdit,
            gameOver,
            credits,
            creativeMode,
            inputDialog,
            stats;
    private AbstractGame game, edit;

    private boolean running = false;
    private int width, height;
    private float scale;
    private String title;

    private enum STATE{
        MAINMENU,
        OPTSMENU,
        GAME,
        PAUSEDGAME,
        GAMEOVER,
        EDIT,
        PAUSEDEDIT,
        CREATIVEMODE,
        INPUTDIALOG,
        CREDITS,
        STATS,
        EXIT
    }

    private STATE State = STATE.MAINMENU;
    private int currState = 0, lastState = 0;

    public GameContainer(AbstractGame game, Settings settings, Data data) {
        this.game = game;
        this.s = settings;
        this.data = data;

        this.mainMenu = new MainMenu(s);
        this.options = new Options(s);
        this.pausedGame = new PausedGame(s);
        this.gameOver = new GameOver(s);
        this.pausedEdit = new PausedEdit(s);
        this.credits = new Credits(s);
        this.creativeMode = new CreativeMode(s);
        this.inputDialog = new InputDialog(s);
        this.stats = new Stats(s);

        this.edit = new Edit(60, 30);
        this.gm = (GameManager) game;
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

                if(State == STATE.MAINMENU) {
                    mainMenu.update(this, (float)UPDATE_CAP);
                } else if(State == STATE.CREDITS) {
                    credits.update(this, (float)UPDATE_CAP);
                } else if(State == STATE.OPTSMENU) {
                    options.update(this, (float)UPDATE_CAP);
                } else if(State == STATE.PAUSEDGAME) {
                    pausedGame.update(this, (float)UPDATE_CAP);
                } else if(State == STATE.PAUSEDEDIT) {
                    pausedEdit.update(this, (float)UPDATE_CAP);
                } else if(State == STATE.GAMEOVER) {
                    gameOver.update(this, (float)UPDATE_CAP);
                } else if(State == STATE.GAME) {
                    game.update(this, (float)UPDATE_CAP);
                } else if(State == STATE.EDIT) {
                    edit.update(this, (float)UPDATE_CAP);
                } else if(State == STATE.CREATIVEMODE) {
                    creativeMode.update(this, (float)UPDATE_CAP);
                } else if(State == STATE.INPUTDIALOG) {
                    inputDialog.update(this, (float)UPDATE_CAP);
                } else if(State == STATE.STATS) {
                    stats.update(this, (float)UPDATE_CAP);
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

                if(State == STATE.GAME || State == STATE.PAUSEDGAME || State == STATE.GAMEOVER || (State == STATE.STATS && lastState == 2)) {
                    game.render(this, gameRender);
                    gameRender.setCoorCam(0, 0);
                    if(s.isShowLights()) gameRender.process();
                    if(gm.getObject("player") != null)
                        gameRender.drawGameStates(this, gm.getObject("player"));
                } else if(State == STATE.EDIT || State == STATE.PAUSEDEDIT) {
                    edit.render(this, gameRender);
                }

                if(State == STATE.MAINMENU) {
                    mainMenu.render(this, gameRender);
                    gameRender.setCoorCam(0, 0);
                } else if(State == STATE.CREDITS) {
                    credits.render(this, gameRender);
                } else if(State == STATE.OPTSMENU) {
                    options.render(this, gameRender);
                } else if(State == STATE.PAUSEDGAME) {
                    pausedGame.render(this, gameRender);
                } else if(State == STATE.PAUSEDEDIT) {
                    pausedEdit.render(this, gameRender);
                } else if(State == STATE.GAMEOVER) {
                    gameOver.render(this, gameRender);
                } else if(State == STATE.CREATIVEMODE) {
                    creativeMode.render(this, gameRender);
                    gameRender.setCoorCam(0, 0);
                } else if(State == STATE.INPUTDIALOG) {
                    creativeMode.render(this, gameRender);
                    inputDialog.render(this, gameRender);
                } else if(State == STATE.STATS) {
                    stats.render(this, gameRender);
                }

                if(State == STATE.MAINMENU || (State == STATE.OPTSMENU && lastState == 0)) {
                    gameRender.drawText(title + " 2.0.1/beta", 0, getHeight(), 1, -1, 0xffababab, Font.STANDARD);
                    gameRender.drawText("Strozor Inc.", getWidth(), getHeight(), -1, -1, 0xffababab, Font.STANDARD);
                }

                if(s.isShowFps())
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

    public Input getInput() {
        return input;
    }

    public Settings getSettings() {
        return s;
    }

    public Data getData() {
        return data;
    }

    public int getLastState() {
        return lastState;
    }

    public void setLastState(int lastState) {
        this.lastState = lastState;
    }

    public int getCurrState() {
        return currState;
    }

    public void setState(int value) {
        switch(value) {
            case -1: State = STATE.EXIT; break;
            case 0: State = STATE.MAINMENU; break;
            case 1: State = STATE.GAME; break;
            case 2: State = STATE.PAUSEDGAME; break;
            case 3: State = STATE.OPTSMENU; break;
            case 4: State = STATE.EDIT; break;
            case 5: State = STATE.PAUSEDEDIT; break;
            case 6: State = STATE.CREDITS; break;
            case 7: State = STATE.GAMEOVER; break;
            case 8: State = STATE.CREATIVEMODE; break;
            case 9: State = STATE.INPUTDIALOG; break;
            case 10: State = STATE.STATS; break;
        }
        currState = value;
    }
}
