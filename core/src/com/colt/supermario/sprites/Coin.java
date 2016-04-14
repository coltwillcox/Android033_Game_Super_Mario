package com.colt.supermario.sprites;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.World;
import com.colt.supermario.Boot;

/**
 * Created by colt on 4/13/16.
 */

public class Coin extends InteractiveTileObject {

    public Coin(World world, TiledMap map, Rectangle bounds) {
        super(world, map, bounds);
        fixture.setUserData(this);
        setCategoryFilter(Boot.COIN_BIT);
    }

    @Override
    public void onHeadHit() {

    }

}