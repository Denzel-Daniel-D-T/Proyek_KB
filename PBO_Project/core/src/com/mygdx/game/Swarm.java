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
    private float guidance = 1;
    private float globalBestX;
    private float globalBestY;
    private float globalBestObjective = Float.MAX_VALUE;
    private float reinertiaTime;
    private float separateTime;
    private float totalTime;

    private boolean separate = false;

    public float startPosX;
    public float startPosY;

    float avgX = 0, avgY = 0;

    public Swarm(float startPosX, float startPosY) {
        this.startPosX = startPosX;
        this.startPosY = startPosY;
        state = State.ACTIVE;
    }

    public void addEnemy(Enemy e) {
        population.add(e);
        populationCount++;
    }

    public void update() {
        reinertiaTime += Gdx.graphics.getDeltaTime();
        totalTime += Gdx.graphics.getDeltaTime();
        if (reinertiaTime > 1) {
            globalBestObjective = Float.MAX_VALUE;

            avgX = 0;
            avgY = 0;

            for (Enemy e: population) {
                e.objective = Float.MAX_VALUE;
                avgX += e.X;
                avgY += e.Y;
            }

            avgX /= populationCount;
            avgY /= populationCount;

            separate = true;

            reinertiaTime = 0;
        }
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
        if (population.contains(e)) {
            population.remove(e);
            populationCount--;
        }
    }

    public void calculateSwarmIteration(Vector2 playerPos) {
        float delta = Gdx.graphics.getDeltaTime();
        for (Enemy e: population) {
            float rand1 = random.nextFloat();
            float rand2 = random.nextFloat();

            e.DX = w * e.DX + c1 * rand1 * (e.bestX - e.X) + c2 * rand2 * (globalBestX - e.X) + guidance * (playerPos.x - e.X) / Vector2.len(playerPos.x - e.X, playerPos.y - e.Y);
            e.DY = w * e.DY + c1 * rand1 * (e.bestY - e.Y) + c2 * rand2 * (globalBestY - e.Y) + guidance * (playerPos.y - e.Y) / Vector2.len(playerPos.x - e.X, playerPos.y - e.Y);

            e.X += (Math.min(e.DX, 100) + Math.cos(Math.toRadians(totalTime * 90)) * 200) * delta * e.Speed / 100f;
            e.Y += (Math.min(e.DY, 100) + Math.sin(Math.toRadians(totalTime * 90)) * 200) * delta * e.Speed / 100f;

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
        }
        if (separate) {
            for (Enemy e: population) {
                e.X += (e.X - avgX) * delta;
                e.Y += (e.Y - avgY) * delta;
            }
            separateTime += delta;
            if (separateTime > 0.25f) {
                separateTime = 0;
                separate = false;
            }
        }
//        System.out.println(globalBestObjective + "(" + globalBestX + ", " + globalBestY + ")");
    }
}
