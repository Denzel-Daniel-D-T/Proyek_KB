package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Klee extends Entity implements AttackedProcessor {
    enum State {
        IDLE, RUN, HIT, WIN
    }
    Sound soundHit, soundQ;
    Animation<TextureRegion> idleLeftAnimation, runLeftAnimation, idleRightAnimation, runRightAnimation, loseAnimation, winAnimation;
    State state = State.IDLE;
    float soundVolume, dashMultiplier;

    public Klee(float soundVolume) {
        X = 0;
        Y = 0;
        DX = 0;
        DY = 0;
        Speed = 150;
        animationDirection = Direction.RIGHT;
        direction = Direction.RIGHT;
        this.soundVolume = soundVolume;
        this.InitializeAnimation();
    }

    public void InitializeAnimation() {
        SimpleGame parentGame = (SimpleGame) Gdx.app.getApplicationListener();
        AssetManager assetManager = parentGame.getAssetManager();

        Texture idle = assetManager.get("kleeIdle.png", Texture.class);
        Texture run = assetManager.get("kleeRun.png", Texture.class);
        Texture lose = assetManager.get("kleeLose.png", Texture.class);
        Texture win = assetManager.get("kleeWin.png", Texture.class);
        soundHit = assetManager.get("collect.wav", Sound.class);
        soundQ = assetManager.get("pressQ.wav", Sound.class);

        idleRightAnimation = initAnimation(idle, 52, 65, 1, 1,0.25f, true);
        idleLeftAnimation = initAnimation(idle, 52, 65, 1, 1, 0.25f, false);
        runRightAnimation = initAnimation(run, 52, 65, 3, 1, 0.1f, true);
        runLeftAnimation = initAnimation(run, 52, 65, 3, 1, 0.1f, false);
        loseAnimation = initAnimation(lose, 66, 77, 7, 1, 0.2f, true);
        winAnimation = initAnimation(win, 64, 69, 8, 1, 0.2f, false);

        stateTime = 0;
    }

    public void draw(SpriteBatch batch) {
        TextureRegion currentFrame = null;
        if (state == State.RUN && animationDirection == Direction.LEFT)
            currentFrame = runLeftAnimation.getKeyFrame(stateTime, true);
        else if (state == State.RUN && animationDirection == Direction.RIGHT)
            currentFrame = runRightAnimation.getKeyFrame(stateTime, true);
        else if (state == State.IDLE && animationDirection == Direction.LEFT)
            currentFrame = idleLeftAnimation.getKeyFrame(stateTime, true);
        else if (state == State.IDLE && animationDirection == Direction.RIGHT)
            currentFrame = idleRightAnimation.getKeyFrame(stateTime, true);
        else if (state == State.HIT)
            currentFrame = loseAnimation.getKeyFrame(stateTime, false);
        else if (state == State.WIN)
            currentFrame = winAnimation.getKeyFrame(stateTime, false);

        if (state == State.IDLE || state == State.RUN)
            batch.draw(currentFrame, X - 26, Y - 32);
        else if (state == State.HIT)
            batch.draw(currentFrame, X - 33, Y - 38);
        else if (state == State.WIN)
            batch.draw(currentFrame, X - 32, Y - 35);
    }

    public void update() {
        float delta = Gdx.graphics.getDeltaTime();
        stateTime += delta;

        X += DX * Speed * (dashMultiplier + 1) * delta;
        Y += DY * Speed * (dashMultiplier + 1) * delta;

        if (dashMultiplier > 0.1f) {
            dashMultiplier *= 0.8f;
        }
        else {
            dashMultiplier = 0;
        }

        if (stateTime > 1.4f && state == State.HIT) {
            DX = 0;
        }
    }

    public void SetMove(Direction d) {
        direction = d;
        state = State.RUN;
        if (animationDirection == Direction.LEFT && (d == Direction.RIGHT || d == Direction.SE || d == Direction.NE)) {
            animationDirection = Direction.RIGHT;
            stateTime = 0;
        }
        else if (animationDirection == Direction.RIGHT && (d == Direction.LEFT || d == Direction.SW || d == Direction.NW)) {
            animationDirection = Direction.LEFT;
            stateTime = 0;
        }
        switch (d) {
            case RIGHT:
                DX = 1;
                DY = 0;
                break;
            case LEFT:
                DX = -1;
                DY = 0;
                break;
            case UP:
                DX = 0;
                DY = 1;
                break;
            case DOWN:
                DX = 0;
                DY = -1;
                break;
            case NE:
                DX = (float) Math.sqrt(2) / 2;
                DY = (float) Math.sqrt(2) / 2;
                break;
            case SE:
                DX = (float) Math.sqrt(2) / 2;
                DY = -(float) Math.sqrt(2) / 2;
                break;
            case SW:
                DX = -(float) Math.sqrt(2) / 2;
                DY = -(float) Math.sqrt(2) / 2;
                break;
            case NW:
                DX = -(float) Math.sqrt(2) / 2;
                DY = (float) Math.sqrt(2) / 2;
                break;
        }
    }

    void Stop() {
        if (state != State.IDLE) {
            DX = 0;
            DY = 0;
            state = State.IDLE;
        }
    }

    public void getHit() {
        if (state == State.HIT || dashMultiplier > 0)
            return;

        state = State.HIT;
        stateTime = 0;
        DX = -0.01f;
        DY = 0;
        soundHit.play(soundVolume);
    }

    public void setWin() {
        if (state == State.HIT)
            return;

        state = State.WIN;
        stateTime = 0;
        DX = 0;
        DY = 0;
    }

    public int result() {
        if (state == State.HIT && stateTime > 2.5f)
            return -1;
        else if (state == State.WIN && stateTime > 2.7f)
            return 1;
        return 0;
    }

    public void setState(State state) {
        this.state = state;
    }

    public State getState() {
        return state;
    }

    public Sound getSoundQ() {
        return soundQ;
    }

    public void setDashMultiplier(float dashMultiplier) {
        this.dashMultiplier = dashMultiplier;
    }
}
