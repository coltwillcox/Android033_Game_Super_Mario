package com.colt.supermario.sprites.particles;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.colt.supermario.screens.ScreenAbstract;

/**
 * Created by colt on 4/21/16.
 */

public abstract class Particle extends Sprite {

    protected float stateTime;
    protected boolean destroy;
    protected boolean destroyed;
    protected AssetManager manager;
    protected ScreenAbstract screen;
    protected World world;
    protected Body body;
    protected Vector2 velocity;

    //Constructor.
    public Particle(ScreenAbstract screen, float x, float y, AssetManager manager) {
        this.manager = manager;
        this.screen = screen;
        this.world = screen.getWorld();

        stateTime = 0;
        destroy = false;
        destroyed = false;

        setPosition(x, y);
        defineParticle();
    }

    public void update(float deltaTime) {
        if (destroy && !destroyed) {
            world.destroyBody(body);
            destroyed = true;
        }
    }

    public abstract void defineParticle();

    public void destroy() {
        destroy = true;
    }

    public boolean isDestroyed() {
        return destroyed;
    }

}