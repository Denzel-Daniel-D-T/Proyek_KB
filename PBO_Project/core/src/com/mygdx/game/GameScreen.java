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
    Label textLabel, scoreText, energyText, clickCDText, eCDText, qCDText, enemyCountText, cText, eText, qText, pausedText, diffText, staminaText, windStrengthText, windDirText, weatherText;
    Button okButton, resumeButton, exitButton;
    TextureRegion clickCDFilter, eCDFilter, qEnergyFilter;
    Texture background, ui, clickButton, eButton, qButton, blackMask, qMask, dumTexture;

    Klee player;
    ArrayList<Enemy> enemyList;
    ArrayList<KleeBomb> kleeBombList, kBtoRemove;
    ArrayList<KleeE> kleeEList, kEtoRemove;
    ArrayList<Swarm> swarmList;
    IntSet pressedKeys;
    Random randomizer;
    State state;

    Weather.Season season;


    int score;
    int difficulty;
    int enemyCount;
    int gameState;
    int checkWin;
    int energy;
    int explodeDistance = 160000;
    int gaIteration = 0;
    int d5_ultTimer = 0;

    float clickCD;
    float eCD;
    float qCD;
    float clickCD_MAX;
    float eCD_MAX;
    float qCD_MAX;
    float weatherTimer;
    float playTime;
    float enemyTimer = Float.MAX_VALUE;
    float skillEnergyMul;
    float gaStatMul;
    float stamina;
    float maxStamina;
    float staminaCD;
    float staminaCD_MAX;
    float kleeAtkSpeed;

    float[] parent1;
    float[] parent2;

    ArrayList<float[]> offsprings = new ArrayList<>();

    public GameScreen(Game g, int difficulty) {
        parentGame = g;
        this.difficulty = difficulty;
        Initialize();
    }

    protected void Initialize() {
        randomizer = new Random();

        season = Weather.randomEnum(Weather.Season.class);
        Weather.difficulty = difficulty;

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

        String diffName;
        switch(difficulty) {
            case 1:
                clickCD_MAX = 0.5f;
                eCD_MAX = 10;
                qCD_MAX = 10;
                skillEnergyMul = 1;
                gaStatMul = 0.5f;
                maxStamina = 100;
                stamina = maxStamina;
                staminaCD_MAX = 2;
                kleeAtkSpeed = 150;
                parent1 = new float[]{50, 1, 4};
                parent2 = new float[]{40, 1, 4};
                diffName = "Easy";
                break;
            case 2:
                clickCD_MAX = 0.5f;
                eCD_MAX = 10;
                qCD_MAX = 10;
                skillEnergyMul = 1;
                gaStatMul = 0.75f;
                maxStamina = 145;
                stamina = maxStamina;
                staminaCD_MAX = 2;
                kleeAtkSpeed = 150;
                parent1 = new float[]{70, 1, 5};
                parent2 = new float[]{50, 1, 4};
                diffName = "Normal";
                break;
            case 3:
                clickCD_MAX = 0.4f;
                eCD_MAX = 8;
                qCD_MAX = 10;
                skillEnergyMul = 1;
                gaStatMul = 0.9f;
                maxStamina = 190;
                stamina = maxStamina;
                staminaCD_MAX = 1.5f;
                kleeAtkSpeed = 180;
                parent1 = new float[]{90, 1, 5};
                parent2 = new float[]{60, 1, 8};
                diffName = "Hard";
                break;
            case 4:
                clickCD_MAX = 0.2f;
                eCD_MAX = 5;
                qCD_MAX = 8;
                skillEnergyMul = 1.25f;
                gaStatMul = 1;
                maxStamina = 240;
                stamina = maxStamina;
                staminaCD_MAX = 1;
                kleeAtkSpeed = 240;
                parent1 = new float[]{110, 1, 9};
                parent2 = new float[]{80, 2, 7};
                diffName = "Insane";
                break;
            case 5:
                clickCD_MAX = 0.15f;
                eCD_MAX = 3;
                qCD_MAX = 6;
                skillEnergyMul = 1.5f;
                gaStatMul = 1;
                maxStamina = -1;
                staminaCD_MAX = 0;
                kleeAtkSpeed = 300;
                parent1 = new float[]{120, 2, 11};
                parent2 = new float[]{90, 2, 9};
                diffName = "Frenzy";
                break;
            default:
                clickCD_MAX = 0;
                eCD_MAX = 0;
                qCD_MAX = 0;
                skillEnergyMul = 20;
                gaStatMul = 1;
                maxStamina = -1;
                staminaCD_MAX = 0;
                kleeAtkSpeed = 150;
                parent1 = new float[]{110, 1, 9};
                parent2 = new float[]{80, 2, 7};
                diffName = "DEBUG";
        }

        offsprings.add(parent1);
        offsprings.add(parent2);

        Label.LabelStyle style;

        //region Stage Actors

        scoreText = new Label("Score: 0", mySkin);
        style = new Label.LabelStyle(scoreText.getStyle());
        style.font = assetManager.get("uiFont.ttf", BitmapFont.class);
        scoreText.setStyle(style);
        scoreText.setPosition(620 - scoreText.getWidth(), 7 + 88 - scoreText.getHeight() / 2);
        scoreText.setAlignment(Align.right);
        scoreText.setColor(Color.WHITE);
        stage.addActor(scoreText);

        energyText = new Label("Energy: 0", mySkin);
        style = new Label.LabelStyle(energyText.getStyle());
        style.font = assetManager.get("uiFont.ttf", BitmapFont.class);
        energyText.setStyle(style);
        energyText.setPosition(620 - energyText.getWidth(), 7 + 66 - energyText.getHeight() / 2);
        energyText.setAlignment(Align.right);
        energyText.setColor(Color.WHITE);
        stage.addActor(energyText);

        enemyCountText = new Label("Enemies Alive: " + enemyCount, mySkin);
        style = new Label.LabelStyle(enemyCountText.getStyle());
        style.font = assetManager.get("uiFont.ttf", BitmapFont.class);
        enemyCountText.setStyle(style);
        enemyCountText.setPosition(620 - enemyCountText.getWidth(), 7 + 44 - enemyCountText.getHeight() / 2);
        enemyCountText.setAlignment(Align.right);
        enemyCountText.setColor(Color.WHITE);
        stage.addActor(enemyCountText);

        diffText = new Label("Difficulty: " + diffName, mySkin);
        style = new Label.LabelStyle(diffText.getStyle());
        style.font = assetManager.get("uiFont.ttf", BitmapFont.class);
        diffText.setStyle(style);
        diffText.setPosition(620 - diffText.getWidth(), 7 + 22 - diffText.getHeight() / 2);
        diffText.setAlignment(Align.right);
        diffText.setColor(Color.WHITE);
        stage.addActor(diffText);

        staminaText = new Label("Stamina: " + stamina, mySkin);
        style = new Label.LabelStyle(staminaText.getStyle());
        style.font = assetManager.get("uiFont.ttf", BitmapFont.class);
        staminaText.setStyle(style);
        staminaText.setPosition(620 - staminaText.getWidth() - diffText.getWidth() - 35, 7 + 88 - staminaText.getHeight() / 2);
        staminaText.setAlignment(Align.right);
        staminaText.setColor(Color.WHITE);
        stage.addActor(staminaText);

        windStrengthText = new Label("Wind Spd: " + "-", mySkin);
        style = new Label.LabelStyle(windStrengthText.getStyle());
        style.font = assetManager.get("uiFont.ttf", BitmapFont.class);
        windStrengthText.setStyle(style);
        windStrengthText.setPosition(620 - windStrengthText.getWidth() - diffText.getWidth() - 35, 7 + 66 - windStrengthText.getHeight() / 2);
        windStrengthText.setAlignment(Align.right);
        windStrengthText.setColor(Color.WHITE);
        stage.addActor(windStrengthText);

        windDirText = new Label("Wind Dir: " + "-", mySkin);
        style = new Label.LabelStyle(windDirText.getStyle());
        style.font = assetManager.get("uiFont.ttf", BitmapFont.class);
        windDirText.setStyle(style);
        windDirText.setPosition(620 - windDirText.getWidth() - diffText.getWidth() - 35, 7 + 44 - windDirText.getHeight() / 2);
        windDirText.setAlignment(Align.right);
        windDirText.setColor(Color.WHITE);
        stage.addActor(windDirText);

        weatherText = new Label("Rain: " + "-", mySkin);
        style = new Label.LabelStyle(weatherText.getStyle());
        style.font = assetManager.get("uiFont.ttf", BitmapFont.class);
        weatherText.setStyle(style);
        weatherText.setPosition(620 - weatherText.getWidth() - diffText.getWidth() - 35, 7 + 22 - weatherText.getHeight() / 2);
        weatherText.setAlignment(Align.right);
        weatherText.setColor(Color.WHITE);
        stage.addActor(weatherText);

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

        cText = new Label("LClick / F", mySkin);
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
        infoWindow.setSize(480, 360);
        infoWindow.setPosition(320 - infoWindow.getWidth() / 2, 300 - infoWindow.getHeight() / 2);
        infoWindow.setMovable(false);
        infoWindow.setModal(true);
        infoWindow.setResizable(false);
        infoWindow.setVisible(true);
        infoWindow.getTitleLabel().setAlignment(Align.center);
        stage.addActor(infoWindow);

        textLabel = new Label("(W) Move Up\n(S) Move Down\n(A) Move Left\n(D) Move Right\n(Left Click) Shoot Projectiles\n(E) Cast Skill\n(Q) Cast Ultimate\n(LShift) Dash (Immune to damage while dashing)\n\n(ESC) Pause Game", mySkin);
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
        swarmList = new ArrayList<>();

        player = new Klee(((SimpleGame) parentGame).getSoundVolume());
        player.setX(SimpleGame.virtualWidth / 2.0f);
        player.setY(SimpleGame.virtualHeight / 2.0f + 60);
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
        enemyTimer += delta;

        if (enemyTimer > 3) {
            calculateGA();
            if (difficulty == 5) {
                player.setSpeed(Math.max(parent1[0] * 1.6f, 150));
            }
            int spawnCount = randomizer.nextInt(3) + 1 + ((int)playTime / 60);
            for (int i = 0; i < spawnCount; i++) {
                int enemyType = randomizer.nextInt(100);
                float randomAngle = randomizer.nextFloat() * 360;
                float enemyX = (float)Math.cos(Math.toRadians(randomAngle)) * 500 + player.X;
                float enemyY = (float)Math.sin(Math.toRadians(randomAngle)) * 500 + player.Y;

                int checkParent = randomizer.nextInt(2);
                float[] chosenChromosome;
                if (checkParent == 0) {
                    chosenChromosome = parent1;
                }
                else {
                    chosenChromosome = parent2;
                }

                if (enemyType < 40) {
                    Bat bat = new Bat(enemyX, enemyY, 0, 0, chosenChromosome[0], Entity.Direction.LEFT, Entity.Direction.LEFT, (int)chosenChromosome[1], 10, false);
                    enemyList.add(bat);
                    enemyCount++;
                }
                else if (enemyType < 65) {
                    Chicken chicken = new Chicken(enemyX, enemyY, 0, 0, chosenChromosome[0], Entity.Direction.LEFT, Entity.Direction.LEFT, (int)chosenChromosome[1] + 2, 20, false);
                    enemyList.add(chicken);
                    enemyCount++;
                }
                else if (enemyType < 80) {
                    Pig pig = new Pig(enemyX, enemyY, 0, 0, chosenChromosome[0] * 0.75f, Entity.Direction.LEFT, Entity.Direction.LEFT, (int)chosenChromosome[1] + 3, 40, false);
                    enemyList.add(pig);
                    enemyCount++;
                }
                else if (enemyType < 95) {
                    Snail snail = new Snail(enemyX, enemyY, 0, 0, chosenChromosome[0] * 0.25f, Entity.Direction.LEFT, Entity.Direction.LEFT, (int)(Math.max(chosenChromosome[1] * 2, chosenChromosome[1] + 5)), 80, false);
                    enemyList.add(snail);
                    enemyCount++;
                }
                else {
                    Swarm testSwarm = new Swarm(enemyX, enemyY);
                    for (int j = 0; j < chosenChromosome[2]; j++) {
                        Bat testBat = new Bat(testSwarm.startPosX + randomizer.nextFloat() * 160 - 80, testSwarm.startPosY + randomizer.nextFloat() * 160 - 80, 0, 0, chosenChromosome[0], Entity.Direction.LEFT, Entity.Direction.LEFT, (int)Math.max((chosenChromosome[1] / 2.0f), 1), 20, true);
                        testBat.setObjective(new Vector2(player.X, player.Y));
                        testSwarm.addEnemy(testBat);
                        enemyList.add(testBat);
                        enemyCount++;
                    }
                    swarmList.add(testSwarm);
                }
            }
            updateEnemyCount();
            enemyTimer = 0;
        }

        //region Weather System
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

            windStrengthText.setText("Wind Str: " + (float) Math.round(Weather.getWindStrength() * 3.6f * 100) / 100 + " km/h");
            String compassText = "-";
            if (Weather.getWindDirection() < 22.5f) {
                compassText = "E";
            }
            else if (Weather.getWindDirection() < 67.5f) {
                compassText = "NE";
            }
            else if (Weather.getWindDirection() < 112.5f) {
                compassText = "N";
            }
            else if (Weather.getWindDirection() < 157.5f) {
                compassText = "NW";
            }
            else if (Weather.getWindDirection() < 202.5f) {
                compassText = "W";
            }
            else if (Weather.getWindDirection() < 247.5f) {
                compassText = "SW";
            }
            else if (Weather.getWindDirection() < 292.5f) {
                compassText = "S";
            }
            else if (Weather.getWindDirection() < 337.5f) {
                compassText = "SE";
            }
            else if (Weather.getWindDirection() <= 360) {
                compassText = "E";
            }
            windDirText.setText("Wind Dir: " + (float) Math.round(Weather.getWindDirection() * 10) / 10 + " " + compassText);
            String weatherName = "-";
            switch (Weather.getRain()) {
                case NONE:
                    weatherName = "None";
                    break;
                case LIGHT:
                    weatherName = "Light";
                    break;
                case HEAVY:
                    weatherName = "Heavy";
                    break;
            }
            weatherText.setText("Rain: " + weatherName);

            switch (Weather.getRain()) {
                case LIGHT:
                    explodeDistance = 102400;
                    break;
                case HEAVY:
                    explodeDistance = 57600;
                    break;
                default:
                    explodeDistance = 160000;
            }

            weatherTimer = 0;
        }
        Weather.setPlayTime(playTime);
        //endregion

        //region Controls
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
        //endregion

        player.update();
        gameState = player.result();

        //region Control Updates

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

        if (qCD > 0) {
            qCD -= delta;
            if (difficulty == 5) {
                if (player.getState() != Klee.State.HIT && player.getState() != Klee.State.WIN && d5_ultTimer % 8 == 0) {
                    kleeClick();
                }
                d5_ultTimer++;
            }
        }
        else if (qCD < 0)
            qCD = 0;
        else if (qCD == 0 && d5_ultTimer > 0)
            d5_ultTimer = 0;

        updateQ();
        
        if (staminaCD > 0)
            staminaCD -= delta;
        else if (staminaCD < 0)
            staminaCD = 0;
        
        if (maxStamina != -1) {
            if (stamina < maxStamina && staminaCD == 0)
                stamina += delta * 25;
            else if (stamina > maxStamina)
                stamina = maxStamina;
            else if (stamina < 0)
                stamina = 0;

            staminaText.setText("Stamina: " + (float) Math.round(stamina * 10) / 10);
        }
        else {
            staminaText.setText("Stamina: Infinite");
        }

        //endregion

        for (KleeBomb k: kleeBombList) {
            k.setDX(k.getDX() + Weather.getWindStrength() * (float) Math.cos(Math.toRadians(Weather.getWindDirection())) * delta);
            k.setDY(k.getDY() + Weather.getWindStrength() * (float) Math.sin(Math.toRadians(Weather.getWindDirection())) * delta);
            k.update();
            if (k.getState() == KleeAttacks.State.STD && ((k.getX() - k.getStartX()) * (k.getX() - k.getStartX()) + (k.getY() - k.getStartY()) * (k.getY() - k.getStartY())) > explodeDistance) {
                k.Boom(Vector2.dst(k.getX(), k.getY(), player.X, player.Y));
            }
        }

        for (KleeE k: kleeEList) {
            k.setDX(k.getDX() + Weather.getWindStrength() * (float) Math.cos(Math.toRadians(Weather.getWindDirection())) * delta * 0.5f);
            k.setDY(k.getDY() + Weather.getWindStrength() * (float) Math.sin(Math.toRadians(Weather.getWindDirection())) * delta * 0.5f);
            k.update();
            if (k.getState() == KleeAttacks.State.STD && ((k.getX() - k.getStartX()) * (k.getX() - k.getStartX()) + (k.getY() - k.getStartY()) * (k.getY() - k.getStartY())) > explodeDistance) {
                k.Boom(Vector2.dst(k.getX(), k.getY(), player.X, player.Y));
                int amount = 10;
                if (difficulty == 5) {
                    amount = 20;
                }
                for (int i = 0; i < amount; i++) {
                    float x, y;
                    x = k.getX() - 160 + randomizer.nextInt(320);
                    y = k.getY() - 160 + randomizer.nextInt(320);
                    KleeBomb kB = new KleeBomb(x, y, 0, 0, (int)(8 * skillEnergyMul), kleeAtkSpeed, ((SimpleGame) parentGame).getSoundVolume());
                    kleeBombList.add(kB);
                }
            }
        }

        Iterator<Swarm> swarmIterator = swarmList.iterator();

        while (swarmIterator.hasNext()) {
            Swarm s = swarmIterator.next();
            s.update();
            s.calculateSwarmIteration(new Vector2(player.X, player.Y));

            if (s.getState() == Swarm.State.DEAD) {
                System.out.println("Swarm eliminated");
                swarmIterator.remove();
            }
        }

        Iterator<Enemy> enemyIterator = enemyList.iterator();

        while (enemyIterator.hasNext()) {
            Enemy e = enemyIterator.next();
            e.update();

            if (!e.isMemberOfSwarm) {
                Vector2 enemyDir = new Vector2(player.getX() - e.getX(), player.getY() - e.getY());
                enemyDir = enemyDir.nor();
                e.setDX(enemyDir.x);
                e.setDY(enemyDir.y);
            }

            if (e.canHit(player) && difficulty != 5) {
                player.getHit();
            }
            if (e.getState() == Enemy.State.DEAD) {
                for (Swarm s: swarmList) {
                    s.removeMember(e);
                }
                enemyIterator.remove();
                addScore(e.getScore());
                enemyCount--;
                updateEnemyCount();
            }
            for (KleeBomb k: kleeBombList) {
                if (k.canHit(e)) {
                    e.getHit();
                    addEnergy(k.getEnergy());
                    k.Boom(Vector2.dst(k.getX(), k.getY(), player.X, player.Y));
                }
            }
            for (KleeE k: kleeEList) {
                if (k.canHit(e)) {
                    e.getHit();
                    addEnergy(k.getEnergy());
                    k.Boom(Vector2.dst(k.getX(), k.getY(), player.X, player.Y));
                    int amount = 10;
                    if (difficulty == 5) {
                        amount = 20;
                    }
                    for (int i = 0; i < amount; i++) {
                        float x, y;
                        x = k.getX() - 160 + randomizer.nextInt(320);
                        y = k.getY() - 160 + randomizer.nextInt(320);
                        KleeBomb kB = new KleeBomb(x, y, 0, 0, (int)(8 * skillEnergyMul), kleeAtkSpeed, ((SimpleGame) parentGame).getSoundVolume());
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

        if (gameState == -1) {
            parentGame.setScreen(new ResultScreen(parentGame, score));
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
            case 5:
                add *= 3;
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
        enemyCountText.setText("Enemies Alive: " + enemyCount);
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

    public void calculateGA() {
        float[] child1 = {parent1[0], parent2[1], parent1[2]};
        float[] child2 = {parent2[0], parent1[1], parent2[2]};

        int moddedGA = gaIteration % 3;
        switch (moddedGA) {
            case 0:
                child1[0] *= 1 + randomizer.nextFloat() * 0.2f * gaStatMul - 0.1f * gaStatMul;
                child2[0] *= 1 + randomizer.nextFloat() * 0.2f * gaStatMul - 0.1f * gaStatMul;
                break;
            case 1:
                child1[1] *= 1 + randomizer.nextFloat() * 0.1f * gaStatMul - 0.05f * gaStatMul;
                child2[1] *= 1 + randomizer.nextFloat() * 0.1f * gaStatMul - 0.05f * gaStatMul;
                break;
            case 2:
                child1[2] *= 1 + randomizer.nextFloat() * 0.05f * gaStatMul - 0.025f * gaStatMul;
                child2[2] *= 1 + randomizer.nextFloat() * 0.05f * gaStatMul - 0.025f * gaStatMul;
                break;
            default:
                System.out.println("wtf");
        }

        ArrayList<float[]> tempList = new ArrayList<>();
        tempList.add(child1);
        tempList.add(child2);

        for (float[] child: tempList) {
            if (offsprings.size() < 5) {
                offsprings.add(child);
            }
            else {
                float childScore = child[0] + child[1] * 20 + child[2] * 10;

                float worstScore = offsprings.get(0)[0] + offsprings.get(0)[1] * 20 + offsprings.get(0)[2] * 10;
                int worstIndex = 0;

                float bestScore = offsprings.get(0)[0] + offsprings.get(0)[1] * 20 + offsprings.get(0)[2] * 10;
                int bestIndex = 0;
                for (int i = 1; i < 5; i++) {
                    float checkScore = offsprings.get(i)[0] + offsprings.get(i)[1] * 20 + offsprings.get(i)[2] * 10;
                    if (worstScore > checkScore) {
                        worstScore = checkScore;
                        worstIndex = i;
                    }
                    if (bestScore < checkScore) {
                        bestScore = checkScore;
                        bestIndex = i;
                    }
                }
                if (worstScore < childScore) {
                    offsprings.set(worstIndex, child);
                }
                parent1 = offsprings.get(bestIndex);
                
                ArrayList<float[]> tempTemp = new ArrayList<>(offsprings);
                tempTemp.remove(bestIndex);

                bestScore = tempTemp.get(0)[0] + tempTemp.get(0)[1] * 20 + tempTemp.get(0)[2] * 10;
                bestIndex = 0;
                for (int i = 1; i < 4; i++) {
                    float checkScore = tempTemp.get(i)[0] + tempTemp.get(i)[1] * 20 + tempTemp.get(i)[2] * 10;
                    if (bestScore < checkScore) {
                        bestScore = checkScore;
                        bestIndex = i;
                    }
                }
                parent2 = tempTemp.get(bestIndex);
            }
        }

        gaIteration++;
    }

    public void kleeClick() {
        if (viewport.unproject(new Vector2(Gdx.input.getX(), 0)).x > player.getX()) {
            player.setDirection(Entity.Direction.RIGHT);
            player.setAnimationDirection(Entity.Direction.RIGHT);
        } else {
            player.setDirection(Entity.Direction.LEFT);
            player.setAnimationDirection(Entity.Direction.LEFT);
        }
        Vector2 dir = new Vector2(Gdx.input.getX(), Gdx.input.getY());
        dir = viewport.unproject(dir);
        dir.x -= player.getX();
        dir.y -= player.getY();

        dir = dir.nor();

        if (difficulty != 5) {
            KleeBomb kB = new KleeBomb(player.getX(), player.getY(), dir.x, dir.y, (int)(4 * skillEnergyMul), kleeAtkSpeed, ((SimpleGame) parentGame).getSoundVolume());
            kleeBombList.add(kB);
        }
        else {
            dir = dir.rotateDeg(-30);
            for (int i = 0; i < 3; i++) {
                KleeBomb kB = new KleeBomb(player.getX(), player.getY(), dir.x, dir.y, (int)(4 * skillEnergyMul), kleeAtkSpeed, ((SimpleGame) parentGame).getSoundVolume());
                dir = dir.rotateDeg(30);
                kleeBombList.add(kB);
            }
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

        if (i == Input.Keys.F && player.getState() != Klee.State.HIT && player.getState() != Klee.State.WIN && !(clickCD > 0)) {
            kleeClick();
            clickCD = clickCD_MAX;
        }

        if ((i == Input.Keys.A || i == Input.Keys.W || i == Input.Keys.D || i == Input.Keys.S) && pressedKeys.size < 2) {
            pressedKeys.add(i);
        }

        if (i == Input.Keys.SHIFT_LEFT && pressedKeys.notEmpty()) {
            if (stamina > 18 && staminaCD == 0 || maxStamina == -1) {
                player.setDashMultiplier(6.0f);
                if (maxStamina != -1) {
                    stamina -= 18;
                    staminaCD = staminaCD_MAX;
                }
            }
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

            KleeE kE = new KleeE(player.getX(), player.getY(), dir.x, dir.y, (int)(12 * skillEnergyMul), kleeAtkSpeed, ((SimpleGame) parentGame).getSoundVolume());
            kleeEList.add(kE);
            eCD = eCD_MAX;
        }

        if (i == Input.Keys.Q && qCD <= 0 && energy >= 80) {
            player.getSoundQ().play(player.soundVolume * 0.6f);
            for (Enemy e: enemyList) {
                KleeBomb k = new KleeBomb(e.getX(), e.getY(), 0, 0, 0, kleeAtkSpeed, ((SimpleGame) parentGame).getSoundVolume());
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

        kleeClick();
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
