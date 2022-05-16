package com.mygdx.game;

import com.badlogic.gdx.*;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.IntSet;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class GameScreen implements Screen, InputProcessor {
    enum State {
        RUNNING, PAUSED
    }
    Game parentGame;
    AssetManager assetManager;
    SpriteBatch batch, stationaryBatch;
    OrthographicCamera camera, stageCamera;
    Viewport viewport;
    InputMultiplexer multiInput;
    Stage stage;
    Window infoWindow, pauseWindow;
    Label textLabel, scoreText, energyText, clickCDText, eCDText, qCDText, enemyCountText, cText, eText, qText, pausedText, diffText;
    Button okButton, resumeButton, exitButton;
    TextureRegion clickCDFilter, eCDFilter, qEnergyFilter;
    Texture background, line, ui, clickButton, eButton, qButton, blackMask, qMask, dumTexture;

    Klee player;
    ArrayList<Enemy> enemyList;
    ArrayList<KleeBomb> kleeBombList, kBtoRemove;
    ArrayList<KleeE> kleeEList, kEtoRemove;
    IntSet pressedKeys;
    Random randomizer;
    State state;

    Weather.Season season;


    int score, difficulty, enemyCount, gameState, checkWin, randomEnemy, energy;
    float clickCD, eCD, qCD, clickCD_MAX, eCD_MAX, qCD_MAX, weatherTimer, playTime;

    public GameScreen(Game g) {
        parentGame = g;
        difficulty = 0;
        Initialize();
    }

    protected void Initialize() {
        randomizer = new Random();

        season = Weather.randomEnum(Weather.Season.class);

        state = State.PAUSED;
        pressedKeys = new IntSet(2);

        assetManager = ((SimpleGame) parentGame).getAssetManager();
        camera = new OrthographicCamera(SimpleGame.virtualWidth, SimpleGame.virtualHeight);
        camera.setToOrtho(false, SimpleGame.virtualWidth, SimpleGame.virtualHeight);
        viewport = new FitViewport(SimpleGame.virtualWidth, SimpleGame.virtualHeight, camera);
        batch = new SpriteBatch();
        stationaryBatch = new SpriteBatch();

        multiInput = new InputMultiplexer();

        stageCamera = new OrthographicCamera(SimpleGame.virtualWidth, SimpleGame.virtualHeight);
        stageCamera.setToOrtho(false, SimpleGame.virtualWidth, SimpleGame.virtualHeight);
        stage = new Stage(new FitViewport(SimpleGame.virtualWidth, SimpleGame.virtualHeight, stageCamera));
        multiInput.addProcessor(stage);
        multiInput.addProcessor(this);

        background = assetManager.get("bg.png", Texture.class);
        line = assetManager.get("Line.png", Texture.class);
        ui = assetManager.get("gameUI.png", Texture.class);
        clickButton = assetManager.get("button_Click.png");
        eButton = assetManager.get("button_E.png", Texture.class);
        qButton = assetManager.get("button_Q.png", Texture.class);
        blackMask = assetManager.get("buttonEMask.png", Texture.class);
        qMask = assetManager.get("buttonQMask.png", Texture.class);
        dumTexture = assetManager.get("blank.png", Texture.class);

        clickCDFilter = new TextureRegion(blackMask, 0, 0, 72, 0);
        eCDFilter = new TextureRegion(blackMask, 0, 0, 72, 0);
        qEnergyFilter = new TextureRegion(qMask, 0, 0, 72, 0);

        Skin mySkin = assetManager.get("uiskin.json", Skin.class);

        int enemyStart;
        String diffName;
        switch(difficulty) {
            case 1:
                clickCD_MAX = 0.5f;
                eCD_MAX = 10;
                qCD_MAX = 10;
                enemyStart = 30;
                diffName = "Easy";
                break;
            case 2:
                clickCD_MAX = 0.5f;
                eCD_MAX = 10;
                qCD_MAX = 10;
                enemyStart = 50;
                diffName = "Normal";
                break;
            case 3:
                clickCD_MAX = 0.4f;
                eCD_MAX = 8;
                qCD_MAX = 10;
                enemyStart = 80;
                diffName = "Hard";
                break;
            case 4:
                clickCD_MAX = 0.2f;
                eCD_MAX = 5;
                qCD_MAX = 8;
                enemyStart = 160;
                diffName = "Insane";
                break;
            default:
                clickCD_MAX = 0.01f;
                eCD_MAX = 1;
                qCD_MAX = 10;
                enemyStart = 40;
                diffName = "DEBUG";
        }

        Label.LabelStyle style;

        //region Stage Actors

        scoreText = new Label("Score: 0", mySkin);
        style = new Label.LabelStyle(scoreText.getStyle());
        style.font = assetManager.get("uiFont.ttf", BitmapFont.class);
        scoreText.setStyle(style);
        scoreText.setPosition(616 - scoreText.getWidth(), 7 + 88 - scoreText.getHeight() / 2);
        scoreText.setAlignment(Align.right);
        scoreText.setColor(Color.WHITE);
        stage.addActor(scoreText);

        energyText = new Label("Energy: 0", mySkin);
        style = new Label.LabelStyle(energyText.getStyle());
        style.font = assetManager.get("uiFont.ttf", BitmapFont.class);
        energyText.setStyle(style);
        energyText.setPosition(616 - energyText.getWidth(), 7 + 66 - energyText.getHeight() / 2);
        energyText.setAlignment(Align.right);
        energyText.setColor(Color.WHITE);
        stage.addActor(energyText);

        enemyCountText = new Label("Enemies Left: " + enemyCount, mySkin);
        style = new Label.LabelStyle(enemyCountText.getStyle());
        style.font = assetManager.get("uiFont.ttf", BitmapFont.class);
        enemyCountText.setStyle(style);
        enemyCountText.setPosition(616 - enemyCountText.getWidth(), 7 + 44 - enemyCountText.getHeight() / 2);
        enemyCountText.setAlignment(Align.right);
        enemyCountText.setColor(Color.WHITE);
        stage.addActor(enemyCountText);

        diffText = new Label("Difficulty: " + diffName, mySkin);
        style = new Label.LabelStyle(diffText.getStyle());
        style.font = assetManager.get("uiFont.ttf", BitmapFont.class);
        diffText.setStyle(style);
        diffText.setPosition(616 - diffText.getWidth(), 7 + 22 - diffText.getHeight() / 2);
        diffText.setAlignment(Align.right);
        diffText.setColor(Color.WHITE);
        stage.addActor(diffText);

        clickCDText = new Label("", mySkin);
        style = new Label.LabelStyle(clickCDText.getStyle());
        style.font = assetManager.get("uiFont.ttf", BitmapFont.class);
        clickCDText.setStyle(style);
        clickCDText.setPosition(17 + 36 - clickCDText.getWidth() / 2, 36 + 36 - clickCDText.getHeight() / 2);
        clickCDText.setAlignment(Align.center);
        clickCDText.setColor(Color.WHITE);
        stage.addActor(clickCDText);

        eCDText = new Label("", mySkin);
        style = new Label.LabelStyle(eCDText.getStyle());
        style.font = assetManager.get("uiFont.ttf", BitmapFont.class);
        eCDText.setStyle(style);
        eCDText.setPosition(113 + 36 - eCDText.getWidth() / 2, 36 + 36 - eCDText.getHeight() / 2);
        eCDText.setAlignment(Align.center);
        eCDText.setColor(Color.WHITE);
        stage.addActor(eCDText);

        qCDText = new Label("", mySkin);
        style = new Label.LabelStyle(qCDText.getStyle());
        style.font = assetManager.get("uiFont.ttf", BitmapFont.class);
        qCDText.setStyle(style);
        qCDText.setPosition(209 + 36 - qCDText.getWidth() / 2, 36 + 36 - qCDText.getHeight() / 2);
        qCDText.setAlignment(Align.center);
        qCDText.setColor(Color.WHITE);
        stage.addActor(qCDText);

        cText = new Label("Left Click", mySkin);
        style = new Label.LabelStyle(cText.getStyle());
        style.font = assetManager.get("uiFont.ttf", BitmapFont.class);
        cText.setStyle(style);
        cText.setPosition(17 + 36 - cText.getWidth() / 2, 12);
        cText.setAlignment(Align.center);
        cText.setColor(Color.WHITE);
        stage.addActor(cText);

        eText = new Label("E", mySkin);
        style = new Label.LabelStyle(eText.getStyle());
        style.font = assetManager.get("uiFont.ttf", BitmapFont.class);
        eText.setStyle(style);
        eText.setPosition(113 + 36 - 6, 12);
        eText.setAlignment(Align.center);
        eText.setColor(Color.WHITE);
        stage.addActor(eText);

        qText = new Label("Q", mySkin);
        style = new Label.LabelStyle(qText.getStyle());
        style.font = assetManager.get("uiFont.ttf", BitmapFont.class);
        qText.setStyle(style);
        qText.setPosition(209 + 36 - 7, 14);
        qText.setAlignment(Align.center);
        qText.setColor(Color.WHITE);
        stage.addActor(qText);

        infoWindow = new Window("Controls", mySkin);
        infoWindow.setSize(480, 160);
        infoWindow.setPosition(320 - infoWindow.getWidth() / 2, 300 - infoWindow.getHeight() / 2);
        infoWindow.setMovable(false);
        infoWindow.setModal(true);
        infoWindow.setResizable(false);
        infoWindow.setVisible(true);
        infoWindow.getTitleLabel().setAlignment(Align.center);
        stage.addActor(infoWindow);

        textLabel = new Label("Lorem ipsum dolor sit amet, consectetur adipiscing elit.\nPress the button below to start.", mySkin);
        textLabel.setPosition(infoWindow.getWidth() / 2 - textLabel.getWidth() / 2, 80);
        infoWindow.addActor(textLabel);

        okButton = new TextButton("Done", mySkin);
        okButton.setSize(120, 36);
        okButton.setPosition(infoWindow.getWidth() / 2 - okButton.getWidth() / 2,20);
        okButton.addListener(new InputListener(){
            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                if (x >= 0 && y >= 0 && x <= event.getTarget().getWidth() && y <= event.getTarget().getHeight()) {
                    state = State.RUNNING;
                    infoWindow.setVisible(false);
                    multiInput.removeProcessor(stage);
                }
            }
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
        });
        infoWindow.addActor(okButton);

        pauseWindow = new Window("Paused", mySkin);
        pauseWindow.setSize(400, 160);
        pauseWindow.setPosition(320 - pauseWindow.getWidth() / 2, 300 - pauseWindow.getHeight() / 2);
        pauseWindow.setMovable(false);
        pauseWindow.setModal(true);
        pauseWindow.setResizable(false);
        pauseWindow.setVisible(false);
        pauseWindow.getTitleLabel().setAlignment(Align.center);
        stage.addActor(pauseWindow);

        exitButton = new TextButton("Main Menu", mySkin);
        exitButton.setSize(120, 36);
        exitButton.setPosition(pauseWindow.getWidth() / 3 - exitButton.getWidth() / 2,20);
        exitButton.addListener(new InputListener(){
            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                if (x >= 0 && y >= 0 && x <= event.getTarget().getWidth() && y <= event.getTarget().getHeight()) {
                    parentGame.setScreen(new MenuScreen(parentGame));
                    GameScreen.this.dispose();
                }
            }
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
        });
        pauseWindow.addActor(exitButton);

        resumeButton = new TextButton("Resume", mySkin);
        resumeButton.setSize(120, 36);
        resumeButton.setPosition(pauseWindow.getWidth() * 2 / 3 - resumeButton.getWidth() / 2,20);
        resumeButton.addListener(new InputListener(){
            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                if (x >= 0 && y >= 0 && x <= event.getTarget().getWidth() && y <= event.getTarget().getHeight()) {
                    state = State.RUNNING;
                    pauseWindow.setVisible(false);
                    multiInput.removeProcessor(stage);
                    multiInput.addProcessor(GameScreen.this);
                }
            }
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
        });
        pauseWindow.addActor(resumeButton);

        pausedText = new Label("Game Paused.", mySkin);
        pausedText.setPosition(pauseWindow.getWidth() / 2 - pausedText.getWidth() / 2, 80);
        pauseWindow.addActor(pausedText);

        //endregion

        score = 0;
        enemyCount = 0;
        checkWin = 0;
        eCD = 0;
        energy = 0;

        enemyList = new ArrayList<>();
        kleeBombList = new ArrayList<>();
        kBtoRemove = new ArrayList<>();
        kleeEList = new ArrayList<>();
        kEtoRemove = new ArrayList<>();

        player = new Klee(((SimpleGame) parentGame).getSoundVolume());
        player.setX(SimpleGame.virtualWidth / 2.0f);
        player.setY(SimpleGame.virtualHeight / 2.0f);

        for (int i = 0; i < enemyStart; i++) {
            randomEnemy = randomizer.nextInt(20);
            int x, y, dx = -1, dy = 0, Speed, baseSpeed, ceilingSpeed;
            y = 150 + randomizer.nextInt(425);
            switch (difficulty) {
                case 1:
                    x = 700 + randomizer.nextInt(960);
                    player.setSpeed(200);
                    baseSpeed = 20;
                    ceilingSpeed = 30;
                    switch (randomEnemy) {
                        case 0: case 1: case 2: case 3:
                            Speed = baseSpeed + randomizer.nextInt(ceilingSpeed);
                            Chicken c = new Chicken(x, y, dx, dy, Speed, Entity.Direction.LEFT, Entity.Direction.LEFT, 2, 30);
                            enemyList.add(c);
                            break;
                        case 4: case 5: case 6: case 7: case 8: case 9: case 10: case 11:
                        case 12: case 13: case 14: case 15: case 16: case 17: case 18: case 19:
                            Speed = baseSpeed + randomizer.nextInt(ceilingSpeed);
                            Bat b = new Bat(x, y, dx, dy, Speed, Entity.Direction.LEFT, Entity.Direction.LEFT, 1, 20);
                            enemyList.add(b);
                            break;
                    }
                    break;
                case 3:
                    x = 700 + randomizer.nextInt(1920);
                    baseSpeed = 40;
                    ceilingSpeed = 45;
                    switch (randomEnemy) {
                        case 0: case 1: case 2: case 3: case 4: case 5:
                            Speed = baseSpeed + randomizer.nextInt(ceilingSpeed);
                            Chicken c = new Chicken(x, y, dx, dy, Speed, Entity.Direction.LEFT, Entity.Direction.LEFT, 2, 30);
                            enemyList.add(c);
                            break;
                        case 6: case 7: case 8: case 9: case 10: case 11:
                        case 12: case 13: case 14: case 15: case 16: case 17:
                            Speed = baseSpeed + randomizer.nextInt(ceilingSpeed);
                            Bat b = new Bat(x, y, dx, dy, Speed, Entity.Direction.LEFT, Entity.Direction.LEFT, 1, 20);
                            enemyList.add(b);
                            break;
                        case 18: case 19:
                            Speed = baseSpeed + randomizer.nextInt(ceilingSpeed * 3 / 5);
                            Pig p = new Pig(x, y, dx, dy, Speed, Entity.Direction.LEFT, Entity.Direction.LEFT, 2, 50);
                            enemyList.add(p);
                            break;
                    }
                    break;
                case 4:
                    x = 700 + randomizer.nextInt(2560);
                    baseSpeed = 55;
                    ceilingSpeed = 55;
                    switch (randomEnemy) {
                        case 0: case 1: case 2: case 3: case 4: case 5:
                            Speed = baseSpeed + randomizer.nextInt(ceilingSpeed);
                            Chicken c = new Chicken(x, y, dx, dy, Speed, Entity.Direction.LEFT, Entity.Direction.LEFT, 2, 30);
                            enemyList.add(c);
                            break;
                        case 6: case 7: case 8: case 9: case 10: case 11:
                        case 12: case 13: case 14: case 15: case 16:
                            Speed = baseSpeed + randomizer.nextInt(ceilingSpeed);
                            Bat b = new Bat(x, y, dx, dy, Speed, Entity.Direction.LEFT, Entity.Direction.LEFT, 1, 20);
                            enemyList.add(b);
                            break;
                        case 17: case 18:
                            Speed = baseSpeed + randomizer.nextInt(ceilingSpeed * 3 / 5);
                            Pig p = new Pig(x, y, dx, dy, Speed, Entity.Direction.LEFT, Entity.Direction.LEFT, 2, 50);
                            enemyList.add(p);
                            break;
                        case 19:
                            x = x / 4 + 700;
                            Speed = (baseSpeed + randomizer.nextInt(ceilingSpeed)) / 3;
                            Snail s = new Snail(x, y, dx, dy, Speed, Entity.Direction.LEFT, Entity.Direction.LEFT, 10, 50);
                            enemyList.add(s);
                    }
                    break;
                default:
                    x = 700 + randomizer.nextInt(1280);
                    baseSpeed = 30;
                    ceilingSpeed = 37;
                    switch (randomEnemy) {
                        case 0: case 1: case 2: case 3: case 4: case 5:
                            Speed = baseSpeed + randomizer.nextInt(ceilingSpeed);
                            Chicken c = new Chicken(x, y, dx, dy, Speed, Entity.Direction.LEFT, Entity.Direction.LEFT, 2, 30);
                            enemyList.add(c);
                            break;
                        case 6: case 7: case 8: case 9: case 10: case 11: case 12:
                        case 13: case 14: case 15: case 16: case 17: case 18: case 19:
                            Speed = baseSpeed + randomizer.nextInt(ceilingSpeed);
                            Bat b = new Bat(x, y, dx, dy, Speed, Entity.Direction.LEFT, Entity.Direction.LEFT, 1, 20);
                            enemyList.add(b);
                            break;
                    }
            }
            enemyCount++;
        }
        updateEnemyCount();
    }
    @Override
    public void show() {
        Gdx.input.setInputProcessor(multiInput);
    }

    @Override
    public void render(float v) {
        ScreenUtils.clear(0, 0, 0, 1);
        camera.position.x += (player.getX() - camera.position.x) * 5 * v;
        camera.position.y += (player.getY() - camera.position.y - 60) * 5 * v;
        camera.update();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        draw();
        batch.end();

        stationaryBatch.begin();
        stationaryBatch.draw(ui, 0, 0);
        stationaryBatch.draw(clickButton, 17, 36);
        stationaryBatch.draw(eButton, 113, 36);
        stationaryBatch.draw(qButton, 209, 36);
        stationaryBatch.draw(clickCDFilter, 17, 36);
        stationaryBatch.draw(eCDFilter, 113, 36);
        stationaryBatch.draw(qEnergyFilter, 209,36);
        stationaryBatch.end();
        stage.draw();


        switch (state) {
            case RUNNING:
                update();
                break;
            case PAUSED:
                break;
        }
    }

    public void draw() {
        for (int i = -2; i < 3; i++) {
            for (int j = -2; j < 3; j++) {
                batch.draw(background, j * background.getWidth(), i * background.getHeight());
            }
        }

        player.draw(batch);

        for (Enemy e: enemyList)
            e.draw(batch);

        for (KleeBomb kB: kleeBombList)
            kB.draw(batch);

        for (KleeE kE: kleeEList)
            kE.draw(batch);

        batch.draw(dumTexture, -1, -1);
    }

    public void update() {
        float delta = Gdx.graphics.getDeltaTime();
        playTime += delta;

        weatherTimer += delta;
        if (weatherTimer > 10) {
            Weather.resetWeather();

            Weather.setWeather(season);
            boolean cycleFinished = false;
            while (!cycleFinished) {
                cycleFinished = Weather.calculateWeather();
                System.out.println("Cycle Finished");
            }
            System.out.println("All Cycles Finished");

            System.out.println("Season:" + Weather.getSeason());
            System.out.println("Wind Strength:" + Weather.getWindStrength());
            System.out.println("Wind Direction:" + Weather.getWindDirection());
            System.out.println("Rain:" + Weather.getRain());
            System.out.println("Cloud:" + Weather.getCloud());
            System.out.println("Prev. Cloud:" + Weather.getPrevCloud());
            System.out.println("Precipitation:" + Weather.getPrecipitation());
            System.out.println("Prev. Precipitation:" + Weather.getPrevPrecipitation());
            System.out.println("Precipitation Change:" + Weather.getChange());

            weatherTimer = 0;
        }
        Weather.setPlayTime(playTime);

        if (player.getState() == Klee.State.IDLE || player.getState() == Klee.State.RUN) {
            if (pressedKeys.contains(Input.Keys.A)) {
                if (pressedKeys.contains(Input.Keys.W)) {
                    player.SetMove(Entity.Direction.NW);
                } else if (pressedKeys.contains(Input.Keys.S)) {
                    player.SetMove(Entity.Direction.SW);
                } else if (pressedKeys.contains(Input.Keys.D)) {
                    player.Stop();
                } else {
                    player.SetMove(Entity.Direction.LEFT);
                }
            } else if (pressedKeys.contains(Input.Keys.D)) {
                if (pressedKeys.contains(Input.Keys.W)) {
                    player.SetMove(Entity.Direction.NE);
                } else if (pressedKeys.contains(Input.Keys.S)) {
                    player.SetMove(Entity.Direction.SE);
                } else if (pressedKeys.contains(Input.Keys.A)) {
                    player.Stop();
                } else {
                    player.SetMove(Entity.Direction.RIGHT);
                }
            } else if (pressedKeys.contains(Input.Keys.W)) {
                player.SetMove(Entity.Direction.UP);
            } else if (pressedKeys.contains(Input.Keys.S)) {
                player.SetMove(Entity.Direction.DOWN);
            } else {
                player.Stop();
            }
        }

        player.update();
        gameState = player.result();
        Iterator<Enemy> enemyIterator = enemyList.iterator();

        if (clickCD > 0)
            clickCD -= delta;
        else if (clickCD < 0)
            clickCD = 0;

        updateClick();

        if (eCD > 0)
            eCD -= delta;
        else if (eCD < 0)
            eCD = 0;
        
        updateE();

        if (qCD > 0)
            qCD -= delta;
        else if (qCD < 0)
            qCD = 0;

        updateQ();

        for (KleeBomb k: kleeBombList) {
            k.setDX(k.getDX() + Weather.getWindStrength() * (float) Math.cos(Math.toRadians(Weather.getWindDirection())) * delta);
            k.setDY(k.getDY() + Weather.getWindStrength() * (float) Math.sin(Math.toRadians(Weather.getWindDirection())) * delta);
            k.update();
            if (k.getState() == KleeAttacks.State.STD && ((k.getX() - k.getStartX()) * (k.getX() - k.getStartX()) + (k.getY() - k.getStartY()) * (k.getY() - k.getStartY())) > 160000) {
                k.Boom();
            }
        }

        for (KleeE k: kleeEList) {
            k.setDX(k.getDX() + Weather.getWindStrength() * (float) Math.cos(Math.toRadians(Weather.getWindDirection())) * delta * 0.5f);
            k.setDY(k.getDY() + Weather.getWindStrength() * (float) Math.sin(Math.toRadians(Weather.getWindDirection())) * delta * 0.5f);
            k.update();
            if (k.getState() == KleeAttacks.State.STD && ((k.getX() - k.getStartX()) * (k.getX() - k.getStartX()) + (k.getY() - k.getStartY()) * (k.getY() - k.getStartY())) > 160000) {
                k.Boom();
                for (int i = 0; i < 10; i++) {
                    float x, y;
                    x = k.getX() - 160 + randomizer.nextInt(320);
                    y = k.getY() - 160 + randomizer.nextInt(320);
                    KleeBomb kB = new KleeBomb(x, y, 0, 0, 8, ((SimpleGame) parentGame).getSoundVolume());
                    kleeBombList.add(kB);
                }
            }
        }

        while (enemyIterator.hasNext()) {
            Enemy e = enemyIterator.next();
            e.update();
            Vector2 enemyDir = new Vector2(player.getX() - e.getX(), player.getY() - e.getY());
            enemyDir = enemyDir.nor();
            e.setDX(enemyDir.x * 2);
            e.setDY(enemyDir.y * 2);
            if (e.canHit(player)) {
                player.getHit();
            }
            if (e.getState() == Enemy.State.DEAD) {
                enemyIterator.remove();
                addScore(e.getScore());
                enemyCount--;
                updateEnemyCount();
            }
            for (KleeBomb k: kleeBombList) {
                if (k.canHit(e)) {
                    e.getHit();
                    addEnergy(k.getEnergy());
                    k.Boom();
                }
            }
            for (KleeE k: kleeEList) {
                if (k.canHit(e)) {
                    e.getHit();
                    addEnergy(k.getEnergy());
                    k.Boom();
                    for (int i = 0; i < 10; i++) {
                        float x, y;
                        x = k.getX() - 160 + randomizer.nextInt(320);
                        y = k.getY() - 160 + randomizer.nextInt(320);
                        KleeBomb kB = new KleeBomb(x, y, 0, 0, 8, ((SimpleGame) parentGame).getSoundVolume());
                        kleeBombList.add(kB);
                    }
                }
            }
        }

        for (KleeBomb k: kleeBombList) {
            if (k.getState() == KleeBomb.State.EXPLODED)
                kBtoRemove.add(k);
        }
        kleeBombList.removeAll(kBtoRemove);

        for (KleeE k: kleeEList) {
            if (k.getState() == KleeE.State.EXPLODED)
                kEtoRemove.add(k);
        }
        kleeEList.removeAll(kEtoRemove);

        if (enemyCount <= 0 && checkWin == 0) {
            checkWin = 1;
//            player.setWin();
        }

        if (gameState == -1) {
            parentGame.setScreen(new ResultScreen(parentGame, false, score));
            this.dispose();
        }
        else if (gameState == 1) {
            parentGame.setScreen(new ResultScreen(parentGame, true, score));
            this.dispose();
        }
    }

    public void addScore(int add) {
        switch (difficulty) {
            case 1:
                add *= 0.5;
                break;
            case 3:
                add *= 1.5;
                break;
            case 4:
                add *= 2;
                break;
        }
        score += add;
        scoreText.setText("Score: " + score);
    }

    public void addEnergy(int add) {
        if (energy < 80)
            energy += add;
        if (energy > 80)
            energy = 80;
        energyText.setText("Energy: " + energy);
    }

    public void updateEnemyCount() {
        enemyCountText.setText("Enemies Left: " + enemyCount);
    }

    public void updateClick() {
        float setHeight;

        if (clickCD >= 0)
            setHeight = clickCD / clickCD_MAX * 72;
        else
            setHeight = 0;

        clickCDFilter.setRegionY((int) setHeight);
        clickCDFilter.setRegionHeight((int) setHeight);
        if (clickCD > 0) {
            float clickCDTemp = (float) Math.round(clickCD * 10) / 10;
            clickCDText.setText("" + clickCDTemp);
        }
        else {
            clickCDText.setText("");
        }
    }
    
    public void updateE() {
        float setHeight;

        if (eCD >= 0)
            setHeight = eCD / eCD_MAX * 72;
        else
            setHeight = 0;

        eCDFilter.setRegionY((int) setHeight);
        eCDFilter.setRegionHeight((int) setHeight);
        if (eCD > 0) {
            float eCDTemp = (float) Math.round(eCD * 10) / 10;
            eCDText.setText("" + eCDTemp);
        }
        else {
            eCDText.setText("");
        }
    }

    public void updateQ() {
        float setHeight = (float) energy / 80 * 72;
        qEnergyFilter.setRegionY((int) setHeight);
        qEnergyFilter.setRegionHeight((int) setHeight);
        if (qCD > 0) {
            float qCDTemp = (float) Math.round(qCD * 10) / 10;
            qCDText.setText("" + qCDTemp);
        }
        else {
            qCDText.setText("");
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        batch.dispose();
        System.out.println("GameScreen Disposed");
    }

    @Override
    public boolean keyDown(int i) {
        if (player.getState() == Klee.State.HIT || player.getState() == Klee.State.WIN)
            return false;

        if ((i == Input.Keys.A || i == Input.Keys.W || i == Input.Keys.D || i == Input.Keys.S) && pressedKeys.size < 2) {
            pressedKeys.add(i);
        }

        if (i == Input.Keys.SHIFT_LEFT) {
            player.setDashMultiplier(6.0f);
        }

        if (i == Input.Keys.E && eCD <= 0) {
            if (viewport.unproject(new Vector2(Gdx.input.getX(), 0)).x > player.getX()) {
            player.setDirection(Entity.Direction.RIGHT);
            player.setAnimationDirection(Entity.Direction.RIGHT);
            }
            else {
                player.setDirection(Entity.Direction.LEFT);
                player.setAnimationDirection(Entity.Direction.LEFT);
            }

            Vector2 dir = new Vector2(Gdx.input.getX(), Gdx.input.getY());
            dir = viewport.unproject(dir);
            dir.x -= player.getX();
            dir.y -= player.getY();

            dir = dir.nor();

            KleeE kE = new KleeE(player.getX(), player.getY(), dir.x, dir.y, 12, ((SimpleGame) parentGame).getSoundVolume());
            kleeEList.add(kE);
            eCD = eCD_MAX;
        }

        if (i == Input.Keys.Q && qCD <= 0 && energy >= 80) {
            player.getSoundQ().play(player.soundVolume);
            for (Enemy e: enemyList) {
                KleeBomb k = new KleeBomb(e.getX(), e.getY(), 0, 0, 0, ((SimpleGame) parentGame).getSoundVolume());
                kleeBombList.add(k);
            }
            energy -= 80;
            energyText.setText("Energy: " + energy);
            qCD = qCD_MAX;
        }

        if (i == Input.Keys.ESCAPE) {
            if (state == State.RUNNING) {
                state = State.PAUSED;
                multiInput.removeProcessor(this);
                multiInput.addProcessor(stage);
                pauseWindow.setVisible(true);
            }
        }
        return true;
    }

    @Override
    public boolean keyUp(int i) {
        if (player.getState() == Klee.State.HIT || player.getState() == Klee.State.WIN)
            return false;

        if (i == Input.Keys.A || i == Input.Keys.W || i == Input.Keys.D || i == Input.Keys.S) {
            pressedKeys.remove(i);
        }
        return true;
    }

    @Override
    public boolean keyTyped(char c) {
        return false;
    }

    @Override
    public boolean touchDown(int x, int y, int pointer, int button) {
        if (player.getState() == Klee.State.HIT || player.getState() == Klee.State.WIN || clickCD > 0)
            return false;

        if (viewport.unproject(new Vector2(Gdx.input.getX(), 0)).x > player.getX()) {
            player.setDirection(Entity.Direction.RIGHT);
            player.setAnimationDirection(Entity.Direction.RIGHT);
        }
        else {
            player.setDirection(Entity.Direction.LEFT);
            player.setAnimationDirection(Entity.Direction.LEFT);
        }

        Vector2 dir = new Vector2(Gdx.input.getX(), Gdx.input.getY());
        dir = viewport.unproject(dir);
        dir.x -= player.getX();
        dir.y -= player.getY();

        dir = dir.nor();

        System.out.println(dir);

        KleeBomb kB = new KleeBomb(player.getX(), player.getY(), dir.x, dir.y, 4, ((SimpleGame) parentGame).getSoundVolume());
        kleeBombList.add(kB);
        clickCD = clickCD_MAX;
        return true;
    }

    @Override
    public boolean touchUp(int i, int i1, int i2, int i3) {
        return false;
    }

    @Override
    public boolean touchDragged(int i, int i1, int i2) {
        return false;
    }

    @Override
    public boolean mouseMoved(int i, int i1) {
        return false;
    }

    @Override
    public boolean scrolled(float v, float v1) {
        return false;
    }
}
