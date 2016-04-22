package com.colt.supermario.sprites.weapons;

/**
 * Created by colt on 4/18/16.
 */

public class FireballDefinition {

    public float x;
    public float y;
    public float velocity;
    public boolean fireRight;

    public FireballDefinition(float x, float y, float velocity, boolean fireRight) {
        this.x = x;
        this.y = y;
        this.velocity = velocity;
        this.fireRight = fireRight;
    }

}