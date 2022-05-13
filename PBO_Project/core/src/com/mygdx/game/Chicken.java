package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;

public class Chicken extends Enemy {

    public Chicken(float x, float y, float DX, float DY, float speed, Direction animationDirection, Direction direction, int HP, int score) {
        super(x, y, DX, DY, speed, animationDirection, direction, HP, score);
        tileWidth = 32;
        tileHeight = 34;
        this.InitializeAnimation();
    }

    public void InitializeAnimation() {
        SimpleGame parentGame = (SimpleGame) Gdx.app.getApplicationListener();
        AssetManager assetManager = parentGame.getAssetManager();

        Texture run = assetManager.get("Chicken.png", Texture.class);
        Texture hit = assetManager.get("ChickenHit.png", Texture.class);

        runLeftAnimation = initAnimation(run, tileWidth, tileHeight, 14, 1, 0.05f, false);
        hitLeftAnimation = initAnimation(hit, tileWidth, tileHeight, 5, 1, 0.05f, false);

        stateTime = 0;
    }
}
