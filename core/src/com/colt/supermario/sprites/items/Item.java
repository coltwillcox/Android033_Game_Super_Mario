package com.colt.supermario.sprites.items;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.colt.supermario.Boot;
import com.colt.supermario.screens.ScreenPlay;
import com.colt.supermario.sprites.Mario;

/**
 * Created by colt on 4/16/16.
 */

public abstract class Item extends Sprite {

    protected boolean destroy;
    protected boolean destroyed;
    protected ScreenPlay screen;
    protected World world;
    protected Body body;
    protected Vector2 velocity;

    //Constructor.
    public Item(ScreenPlay screen, float x, float y) {
        this.screen = screen;
        this.world = screen.getWorld();
        setPosition(x, y);
        setBounds(getX(), getY(), 16 / Boot.PPM, 16 / Boot.PPM);
        defineItem();
        destroy = false;
        destroyed = false;
    }

    public void update(float deltaTime) {
        if (destroy && !destroyed) {
            world.destroyBody(body);
            destroyed = true;
        }
    }

    public abstract void defineItem();

    @Override
    public void draw(Batch batch) {
        if (!destroyed)
            super.draw(batch);
    }

    public abstract void use(Mario mario);

    public void reverseVelocity(boolean x, boolean y) {
        if (x)
            velocity.x = -velocity.x;
        if (y)
            velocity.y = -velocity.y;
    }

    public void destroy() {
        destroy = true;
    }

    public boolean isDestroyed() {
        return destroyed;
    }

}