package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.ArrayList;
import java.util.Iterator;

public class Swarm {
    public enum State {
        ACTIVE,
        DEAD
    }
    private float X, Y;
    private int populationCount;
    private ArrayList<Enemy> population = new ArrayList<>();
    private State state;

    public Swarm(Enemy e, int count) {
        for (int i = 0; i < count; i++) {

            population.add(e);
        }
        populationCount = count;
        state = State.ACTIVE;
    }

    public void update() {
        if (populationCount == 0) {
            state = State.DEAD;
        }
    }

    public void draw(SpriteBatch batch) {
        for (Enemy e: population) {
            e.draw(batch);
        }
    }
}
