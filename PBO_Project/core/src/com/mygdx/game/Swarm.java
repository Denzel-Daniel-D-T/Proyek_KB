package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.Random;

public class Swarm {
    public enum State {
        ACTIVE,
        DEAD
    }
    private Random random = new Random();
    private int populationCount;
    private ArrayList<Enemy> population = new ArrayList<>();
    private State state;

    private float c1 = 0.8f;
    private float c2 = 0.1f;
    private float w = 0.95f;
    private float guidance = 0.9f;
    private float globalBestX;
    private float globalBestY;
    private float globalBestObjective = Float.MAX_VALUE;
//    private float reinertiaTime;

    public Swarm() {
        state = State.ACTIVE;
    }

    public void addEnemy(Enemy e) {
        population.add(e);
        populationCount++;
    }

    public void update() {
//        reinertiaTime += Gdx.graphics.getDeltaTime();
//        if (w < 1 && reinertiaTime > 3) {
//            w = 1.1f;
//            reinertiaTime = 0;
//        }
//        if (w > 1 && reinertiaTime > 0.8f) {
//            w = 0.95f;
//            reinertiaTime = 0;
//        }
        if (populationCount == 0) {
            state = State.DEAD;
        }
    }

    public int getPopulationCount() {
        return populationCount;
    }

    public State getState() {
        return state;
    }

    public void removeMember(Enemy e) {
        population.remove(e);
        populationCount--;
    }

    public void calculateSwarmIteration(Vector2 playerPos) {
        float delta = Gdx.graphics.getDeltaTime();
        for (Enemy e: population) {
            float rand1 = random.nextFloat();
            float rand2 = random.nextFloat();
            e.DX = w * e.DX + c1 * rand1 * (e.bestX - e.X) + c2 * rand2 * (globalBestX - e.X) + guidance * (playerPos.x - e.X);
            System.out.println(e.DX + ", " + e.bestX + ", " + globalBestX + ", " + rand1);
            e.DY = w * e.DY + c1 * rand1 * (e.bestY - e.Y) + c2 * rand2 * (globalBestY - e.Y) + guidance * (playerPos.y - e.Y);
//            System.out.println("(" + e.DX + ", " + e.DY + ")");

            e.X += e.DX * delta;
            e.Y += e.DY * delta;
//            System.out.println("(" + e.X + ", " + e.Y + ")");

            float objective = Vector2.dst2(e.X, e.Y, playerPos.x, playerPos.y);
            if (objective < e.objective) {
                e.objective = objective;
                e.bestX = e.X;
                e.bestY = e.Y;
            }
            if (e.objective < globalBestObjective) {
                globalBestObjective = e.objective;
                globalBestX = e.bestX;
                globalBestY = e.bestY;
            }
            System.out.println(e.objective);
        }
        System.out.println("===============\n" + globalBestObjective);
    }
}
