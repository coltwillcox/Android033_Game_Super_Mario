package com.colt.supermario.sprites.enemies;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.colt.supermario.screens.ScreenPlay;

/**
 * Created by colt on 4/14/16.
 */

public abstract class Enemy extends Sprite {

    protected ScreenPlay screen;
    protected World world;
    public Body body;
    public Vector2 velocity;

    //Constructor.
    public Enemy(ScreenPlay screen, float x, float y) {
        this.screen = screen;
        this.world = screen.getWorld();
        setPosition(x, y);
        defineEnemy();
        velocity = new Vector2(0.5f, 0);
        body.setActive(false);
    }

    protected abstract void defineEnemy();

    public abstract void update(float deltaTime);

    public abstract void hitOnHead();

    public void reverseVelocity(boolean x, boolean y) {
        if (x)
            velocity.x = -velocity.x;
        if (y)
            velocity.y = -velocity.y;
    }

}