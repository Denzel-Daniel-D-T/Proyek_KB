package com.mygdx.game;

import com.badlogic.gdx.*;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class ResultScreen implements Screen, InputProcessor {
    Game parentGame;
    AssetManager assetManager;
    SpriteBatch batch;
    OrthographicCamera camera, stageCamera;
    Viewport viewport;

    Stage stage;
    Label gameOver, scoreLabel;
    TextButton returnButton;
    boolean alive;
    int score;

    InputMultiplexer multiInput;

    public ResultScreen(Game g, boolean alive, int score) {
        this.score = score;
        this.alive = alive;
        parentGame = g;
        Initialize();
    }

    protected void Initialize() {
        assetManager = ((SimpleGame) parentGame).getAssetManager();
        camera = new OrthographicCamera(SimpleGame.virtualWidth, SimpleGame.virtualHeight);
        camera.setToOrtho(false, SimpleGame.virtualWidth, SimpleGame.virtualHeight);
        viewport = new FitViewport(SimpleGame.virtualWidth, SimpleGame.virtualHeight, camera);
        batch = new SpriteBatch();

        multiInput = new InputMultiplexer();
        multiInput.addProcessor(this);

        stageCamera = new OrthographicCamera(SimpleGame.virtualWidth, SimpleGame.virtualHeight);
        stageCamera.setToOrtho(false, SimpleGame.virtualWidth, SimpleGame.virtualHeight);
        stage = new Stage(new FitViewport(SimpleGame.virtualWidth, SimpleGame.virtualHeight, stageCamera));

        multiInput.addProcessor(stage);

        Skin mySkin = assetManager.get("uiskin.json", Skin.class);

        if (alive)
            gameOver = new Label("You Won!", mySkin);
        else
            gameOver = new Label("Game Over!", mySkin);
        Label.LabelStyle style = new Label.LabelStyle(gameOver.getStyle());
        style.font = assetManager.get("bigfont.ttf", BitmapFont.class);
        gameOver.setStyle(style);
        gameOver.setWidth(640);
        gameOver.setX(0);
        gameOver.setY(500);
        gameOver.setAlignment(Align.center);
        gameOver.setColor(Color.BLACK);
        stage.addActor(gameOver);

        scoreLabel = new Label("Score: " + score, mySkin);
        style.font = assetManager.get("smallfont.ttf", BitmapFont.class);
        scoreLabel.setStyle(style);
        scoreLabel.setWidth(640);
        scoreLabel.setPosition(0, 440);
        scoreLabel.setAlignment(Align.center);
        scoreLabel.setColor(Color.BLACK);
        stage.addActor(scoreLabel);

        returnButton = new TextButton("Return to Main Menu", mySkin);
        returnButton.setHeight(40);
        returnButton.setWidth(200);
        returnButton.setPosition(320 - returnButton.getWidth() / 2, 10);
        returnButton.addListener(new InputListener(){
            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                if (x >= 0 && y >= 0 && x <= event.getTarget().getWidth() && y <= event.getTarget().getHeight()) {
                    parentGame.setScreen(new MenuScreen(parentGame));
                    ResultScreen.this.dispose();
                }
            }
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
        });
        stage.addActor(returnButton);
    }

    @Override
    public boolean keyDown(int i) {
        return false;
    }

    @Override
    public boolean keyUp(int i) {
        return false;
    }

    @Override
    public boolean keyTyped(char c) {
        return false;
    }

    @Override
    public boolean touchDown(int i, int i1, int i2, int i3) {
        return false;
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

    @Override
    public void show() {
        Gdx.input.setInputProcessor(multiInput);
    }

    @Override
    public void render(float v) {
        ScreenUtils.clear(0, 0, 0, 1);
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        draw();
        batch.end();
        update();
        stage.act();
        stage.draw();
    }

    public void draw() {
        Texture background;
        if (alive)
            background = assetManager.get("win_bg.png", Texture.class);
        else
            background = assetManager.get("lose_bg.png", Texture.class);

        batch.draw(background, 0, 0);
    }

    public void update() {

    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        stage.getViewport().update(width, height);
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
        System.out.println("ResultScreen Disposed");
    }
}
