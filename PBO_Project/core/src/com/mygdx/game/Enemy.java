package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public abstract class Enemy extends Entity implements EnemyAttackProcessor, AttackedProcessor {
    enum State {
        NORMAL, ANGRY, HIT, DEAD
    }

    State state = State.NORMAL;
    int HP, score, tileWidth, tileHeight;
    Animation<TextureRegion> runLeftAnimation, runRightAnimation, hitLeftAnimation, hitRightAnimation;
    boolean alreadyAngry = false;

    public Enemy(float x, float y, float DX, float DY, float speed, Direction animationDirection, Direction direction, int HP, int score) {
        super(x, y, DX, DY, speed, animationDirection, direction);
        this.HP = HP;
        this.score = score;
    }

    public void draw(SpriteBatch batch) {
        TextureRegion currentFrame = null;
        if (state == State.NORMAL) {
            if (animationDirection == Direction.LEFT) {
                currentFrame = runLeftAnimation.getKeyFrame(stateTime, true);
            }
            else if (animationDirection == Direction.RIGHT) {
                currentFrame = runRightAnimation.getKeyFrame(stateTime, true);
            }
        }
        else if (state == State.HIT) {
            if (animationDirection == Direction.LEFT) {
                currentFrame = hitLeftAnimation.getKeyFrame(stateTime, false);
            }
            else if (animationDirection == Direction.RIGHT) {
                currentFrame = hitRightAnimation.getKeyFrame(stateTime, false);
            }
        }

        batch.draw(currentFrame, X - (float) tileWidth / 2, Y - (float) tileHeight / 2);
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

        if (state == State.HIT && stateTime > 0.25f) {
            HP -= 1;
            if (HP <= 0)
                state = State.DEAD;
            else {
                DX = -1;
                if (!(this instanceof Pig)) {
                    state = State.NORMAL;
                }
                else {
                    if (!alreadyAngry) {
                        alreadyAngry = true;
                        Speed *= 1.5;
                    }
                    state = State.ANGRY;
                }
            }
        }
    }

    public boolean canHit(Klee p) {
        if (p.getState() == Klee.State.HIT)
            return false;

        float dx = X - p.getX();
        float dy = Y - p.getY();
        float d = dx * dx + dy * dy;
        return (d <= Math.pow((float) tileWidth / 2, 2));
    }

    public void getHit() {
        if (state == State.NORMAL || state == State.ANGRY) {
            state = State.HIT;
            stateTime = 0;
            DX = 0;
            DY = 0;
        }
    }

    public int getScore() {
        return score;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }
}
