package com.colt.supermario.sprites;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.colt.supermario.screens.ScreenPlay;

/**
 * Created by colt on 4/14/16.
 */

public abstract class Enemy extends Sprite {

    protected World world;
    protected ScreenPlay screen;
    public Body body;

    //Constructor.
    public Enemy(ScreenPlay screen, float x, float y) {
        this.world = screen.getWorld();
        this.screen = screen;
        setPosition(x, y);
        defineEnemy();
    }

    protected abstract void defineEnemy();

}