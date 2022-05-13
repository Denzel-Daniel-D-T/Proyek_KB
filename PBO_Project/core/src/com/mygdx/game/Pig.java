package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Pig extends Enemy implements Angry {
    Animation<TextureRegion> normalLeftAnimation;

    public Pig(float x, float y, float DX, float DY, float speed, Direction animationDirection, Direction direction, int HP, int score) {
        super(x, y, DX, DY, speed, animationDirection, direction, HP, score);
        tileWidth = 36;
        tileHeight = 30;
        this.InitializeAnimation();
    }

    public void InitializeAnimation() {
        SimpleGame parentGame = (SimpleGame) Gdx.app.getApplicationListener();
        AssetManager assetManager = parentGame.getAssetManager();

        Texture walk = assetManager.get("Pig.png", Texture.class);
        Texture run = assetManager.get("PigAngry.png", Texture.class);
        Texture hit = assetManager.get("PigHit.png", Texture.class);

        normalLeftAnimation = initAnimation(walk, tileWidth, tileHeight, 16, 1, 0.05f, false);
        runLeftAnimation = initAnimation(run, tileWidth, tileHeight, 12, 1, 0.05f, false);
        hitLeftAnimation = initAnimation(hit, tileWidth, tileHeight, 5, 1, 0.05f, false);

        stateTime = 0;
    }

    public void draw(SpriteBatch batch) {
        TextureRegion currentFrame = null;
        if (state == State.NORMAL)
            currentFrame = normalLeftAnimation.getKeyFrame(stateTime, true);
        else if (state == State.ANGRY)
            currentFrame = runLeftAnimation.getKeyFrame(stateTime, true);
        else if (state == State.HIT)
            currentFrame = hitLeftAnimation.getKeyFrame(stateTime, false);

        batch.draw(currentFrame, X - (float) tileWidth / 2, Y - (float) tileHeight / 2);
    }

    public void update() {
        super.update();
        Angry();
    }

    public void Angry() {
        if (X < 320 && state != State.ANGRY && state != State.HIT && state != State.DEAD) {
            state = State.ANGRY;
            stateTime = 0;
            Speed *= 1.5;
            alreadyAngry = true;
        }
    }
}