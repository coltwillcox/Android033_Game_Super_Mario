package com.colt.supermario.hud;

/**
 * Created by colt on 4/22/16.
 */

//TODO: Check W's errors.

public class ScoreOverhead {

    private float stateTime;
    private float x;
    private float y;
    private String scoreValue;
    private boolean destroyed;

    //Constructor.
    public ScoreOverhead(float x, float y, String scoreValue) {
        this.x = x;
        this.y = y;
        this.scoreValue = scoreValue;

        stateTime = 0;
        destroyed = false;
    }

    public void update(float deltaTime) {
        stateTime += deltaTime;
        y += deltaTime * 16;
        if (stateTime > 1)
            destroyed = true;
    }

    //Getters.
    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public String getScoreValue() {
        return scoreValue;
    }

    public boolean isDestroyed() {
        return destroyed;
    }

}