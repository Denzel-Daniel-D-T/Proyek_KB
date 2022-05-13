package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class KleeBomb extends KleeAttacks {

    public KleeBomb(float X, float Y, float DX, float DY, int energy, float soundVolume) {
        super(X, Y, DX, DY, energy, soundVolume);
        this.InitializeAnimation();
    }

    public void InitializeAnimation() {
        SimpleGame parentGame = (SimpleGame) Gdx.app.getApplicationListener();
        AssetManager assetManager = parentGame.getAssetManager();

        Texture std = assetManager.get("kleeBomb.png", Texture.class);
        Texture hit = assetManager.get("kleeBombBoom.png", Texture.class);

        explode1 = assetManager.get("explode1.wav", Sound.class);
        explode2 = assetManager.get("explode2.wav", Sound.class);
        explode3 = assetManager.get("explode3.wav", Sound.class);
        explode4 = assetManager.get("explode4.wav", Sound.class);

        stdAnimationLeft = initAnimation(std, 20, 36, 8, 1,0.05f, true);
        stdAnimationRight = initAnimation(std, 20, 36, 8, 1,0.05f, false);
        hitAnimation = initAnimation(hit, 46, 40, 1, 1, 0.125f, false);

        stateTime = 0;
    }

    public void draw(SpriteBatch batch) {
        TextureRegion currentFrame = null;
        if (state == State.STD) {
            if (animationDirection == Direction.LEFT) {
                currentFrame = stdAnimationLeft.getKeyFrame(stateTime, true);
            }
            else if (animationDirection == Direction.RIGHT) {
                currentFrame = stdAnimationRight.getKeyFrame(stateTime, true);
            }
        }
        else if (state == State.HIT)
            currentFrame = hitAnimation.getKeyFrame(stateTime, false);

        if (state == State.STD)
            batch.draw(currentFrame, X - 10, Y - 18);
        else if (state == State.HIT)
            batch.draw(currentFrame, X - 23, Y - 20);
    }

    public void update() {
        super.update();

        if (state == State.HIT && stateTime > 0.125f)
            state = State.EXPLODED;
    }

    public boolean canHit(Enemy e) {
        if ((e.getState() == Enemy.State.NORMAL || e.getState() == Enemy.State.ANGRY) && state == State.STD) {
            float dx = X - e.getX();
            float dy = Y - e.getY();
            float d = dx * dx + dy * dy;
            return (d <= 256);
        }
        else return false;
    }
}
