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

    public KleeAttacks(float X, float Y, float DX, float DY, int energy, float speed, float soundVolume) {
        startX = X;
        startY = Y;
        this.X = X;
        this.Y = Y;
        this.DX = DX;
        this.DY = DY;
        this.energy = energy;
        this.soundVolume = soundVolume;
        Speed = speed;
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

        if (animationDirection == Direction.LEFT && DX > 0) {
            direction = Direction.RIGHT;
            animationDirection = Direction.RIGHT;
        }
        else if (animationDirection == Direction.RIGHT && DX < 0) {
            direction = Direction.LEFT;
            animationDirection = Direction.LEFT;
        }

        X += DX * Speed * delta;
        Y += DY * Speed * delta;
    }

    public void Boom(float distance) {
        state = State.HIT;
        stateTime = 0;
        DX = 0;
        DY = 0;

        if (energy != 0) {
            distance /= 300;

            // Attenuation function from:
            // http://www.cemyuksel.com/research/pointlightattenuation/
            soundVolume *= 2 / (distance * distance + 2 + distance * Math.sqrt(distance * distance + 2));

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

    public float getStartX() {
        return startX;
    }

    public float getStartY() {
        return startY;
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
