package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public abstract class Entity {
    enum Direction {
        UP, DOWN, LEFT, RIGHT, NE, SE, SW, NW
    }
    protected float stateTime;
    protected float X, Y, DX, DY, Speed;
    Direction animationDirection;
    Direction direction;

    public Entity() {

    }

    public Entity(float x, float y, float DX, float DY, float speed, Direction animationDirection, Direction direction) {
        X = x;
        Y = y;
        this.DX = DX;
        this.DY = DY;
        Speed = speed;
        this.animationDirection = animationDirection;
        this.direction = direction;
    }

    public Animation<TextureRegion> initAnimation(Texture texture, int tileWidth, int tileHeight, int frameCountX, int frameCountY, float frameDuration, boolean flipHorizontal) {
        TextureRegion[][] tmp = TextureRegion.split(texture, tileWidth, tileHeight);
        TextureRegion[] frames = new TextureRegion[frameCountX * frameCountY];
        int index = 0;

        for (int i = 0; i < frameCountY; i++) {
            for (int j = 0; j < frameCountX; j++) {
                frames[index] = tmp[i][j];
                frames[index].flip(flipHorizontal, false);
                index++;
            }
        }

        return new Animation<>(frameDuration, frames);
    }

    public abstract void InitializeAnimation();
    public abstract void draw(SpriteBatch batch);
    public abstract void update();

    public float getStateTime() {
        return stateTime;
    }

    public void setStateTime(float stateTime) {
        this.stateTime = stateTime;
    }

    public float getX() {
        return X;
    }

    public void setX(float x) {
        X = x;
    }

    public float getY() {
        return Y;
    }

    public void setY(float y) {
        Y = y;
    }

    public float getDX() {
        return DX;
    }

    public void setDX(float DX) {
        this.DX = DX;
    }

    public float getDY() {
        return DY;
    }

    public void setDY(float DY) {
        this.DY = DY;
    }

    public float getSpeed() {
        return Speed;
    }

    public void setSpeed(float speed) {
        Speed = speed;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public Direction getAnimationDirection() {
        return animationDirection;
    }

    public void setAnimationDirection(Direction animationDirection) {
        this.animationDirection = animationDirection;
    }
}
