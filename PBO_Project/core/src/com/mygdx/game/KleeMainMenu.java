package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class KleeMainMenu extends Entity {
    Animation<TextureRegion> idleAnimation;

    public KleeMainMenu(float X, float Y) {
    this.X = X;
    this.Y = Y;
    this.InitializeAnimation();
    }

    public void InitializeAnimation() {
        SimpleGame parentGame = (SimpleGame) Gdx.app.getApplicationListener();
        AssetManager assetManager = parentGame.getAssetManager();

        Texture idle = assetManager.get("kleeMM_idle.png", Texture.class);

        idleAnimation = initAnimation(idle, 60, 95, 2, 1,0.4f, true);

        stateTime = 0;
    }

    public void draw(SpriteBatch batch) {
        TextureRegion currentFrame = idleAnimation.getKeyFrame(stateTime, true);
        batch.draw(currentFrame, X - 30, Y - 48);
    }

    public void update() {
        float delta = Gdx.graphics.getDeltaTime();
        stateTime += delta;
    }
}
