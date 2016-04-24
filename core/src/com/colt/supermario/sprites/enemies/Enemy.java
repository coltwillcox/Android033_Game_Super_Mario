package com.colt.supermario.sprites.enemies;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.colt.supermario.screens.ScreenAbstract;
import com.colt.supermario.sprites.Mario;

/**
 * Created by colt on 4/14/16.
 */

public abstract class Enemy extends Sprite {

    protected ScreenAbstract screen;
    protected World world;

    public boolean destroyed;
    public Body body;
    public Vector2 velocity;

    //Constructor.
    public Enemy(ScreenAbstract screen, float x, float y) {
        this.screen = screen;
        this.world = screen.getWorld();

        destroyed = false;

        setPosition(x, y);
        defineEnemy();
        velocity = new Vector2(0.5f, 0);
        body.setActive(false);
    }

    public abstract void update(float deltaTime);

    protected abstract void defineEnemy();

    public abstract void onHeadHit(Mario mario);

    public abstract void onEnemyHit(Enemy enemy);

    public abstract void onWeaponHit();

    public void reverseVelocity(boolean x, boolean y) {
        if (x)
            velocity.x = -velocity.x;
        if (y)
            velocity.y = -velocity.y;
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    public abstract void die();

}