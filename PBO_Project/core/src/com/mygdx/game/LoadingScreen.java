package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFontCache;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class LoadingScreen implements Screen, InputProcessor {
    Game parentGame;
    AssetManager assetManager;
    SpriteBatch batch;
    BitmapFontCache text, pressKeyText;
    OrthographicCamera camera;
    Viewport viewport;

    public LoadingScreen(Game g) {
        parentGame = g;
        Initialize();
    }

    protected void Initialize() {
        assetManager = ((SimpleGame)parentGame).getAssetManager();
        camera = new OrthographicCamera(SimpleGame.virtualWidth, SimpleGame.virtualHeight);
        camera.setToOrtho(true, SimpleGame.virtualWidth, SimpleGame.virtualHeight);
        viewport = new FitViewport(SimpleGame.virtualWidth, SimpleGame.virtualHeight, camera);
        batch = new SpriteBatch();

        text = new BitmapFontCache(((SimpleGame)parentGame).getLoadingFont());
        text.setColor(Color.WHITE);
        text.setText("Loading 0%", 260, 280);

        pressKeyText = new BitmapFontCache(((SimpleGame)parentGame).getLoadingFont());
        pressKeyText.setColor(Color.WHITE);
        pressKeyText.setText("PRESS ANY KEY TO CONTINUE...", 220, 340);
        pressKeyText.setAlphas(0);

    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(this);
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
    }

    public void draw() {
        text.draw(batch);
        pressKeyText.draw(batch);
    }

    public void update() {
        if(assetManager.update()) {
            pressKeyText.setAlphas(1);
        }
        float progress = assetManager.getProgress() * 100;
        String loadText = String.format("Loading %.2f%%", progress);
        text.setText(loadText, 260, 280);

    }

    public void SwitchToMenuScreen() {
        if(assetManager.isFinished()) {
            parentGame.setScreen(new MenuScreen(parentGame));
            this.dispose();
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
        System.out.println("LoadingScreen Disposed");
    }

    @Override
    public boolean keyDown(int i) {
        return false;
    }

    @Override
    public boolean keyUp(int i) {
        this.SwitchToMenuScreen();
        return true;
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
        this.SwitchToMenuScreen();
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
