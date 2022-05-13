package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.Random;

public abstract class KleeAttacks extends Entity implements KleeAttackProcessor {
    enum State {
        STD, HIT, EXPLODED
    }
    State state = State.STD;
    Sound explode1, explode2, explode3, explode4;
    Animation<TextureRegion> stdAnimationRight, stdAnimationLeft, hitAnimation;
    Random randomizer;
    float soundVolume;
    float startX;
    float startY;
    int energy;

    public KleeAttacks(float X, float Y, float DX, float DY, int energy, float soundVolume) {
        startX = X;
        startY = Y;
        this.X = X;
        this.Y = Y;
        this.DX = DX;
        this.DY = DY;
        this.energy = energy;
        this.soundVolume = soundVolume;
        Speed = 150;
        if (DX < 0) {
            animationDirection = Direction.LEFT;
            direction = Direction.LEFT;
        }
        else {
            animationDirection = Direction.RIGHT;
            direction = Direction.RIGHT;
        }
        randomizer = new Random();
    }

    public void update() {
        float delta = Gdx.graphics.getDeltaTime();
        stateTime += delta;

        X += DX * Speed * delta;
        Y += DY * Speed * delta;

        if (state == State.STD && ((X - startX) * (X - startX) + (Y - startY) * (Y - startY)) > 160000)
            Boom();
    }

    public void Boom() {
        state = State.HIT;
        stateTime = 0;
        DX = 0;
        DY = 0;

        if (energy != 0) {
            switch (randomizer.nextInt(4)) {
                case 0:
                    explode1.play(soundVolume);
                    break;
                case 1:
                    explode2.play(soundVolume);
                    break;
                case 2:
                    explode3.play(soundVolume);
                    break;
                case 3:
                    explode4.play(soundVolume);
                    break;
            }
        }
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public int getEnergy() {
        return energy;
    }

    public void setEnergy(int energy) {
        this.energy = energy;
    }
}
