package com.colt.supermario.sprites;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.World;
import com.colt.supermario.Boot;
import com.colt.supermario.scenes.HUD;

/**
 * Created by colt on 4/13/16.
 */

public class Brick extends InteractiveTileObject {

    private AssetManager manager;

    public Brick(World world, TiledMap map, Rectangle bounds, AssetManager manager) {
        super(world, map, bounds);
        this.manager = manager;
        fixture.setUserData(this);
        setCategoryFilter(Boot.BRICK_BIT);
    }

    @Override
    public void onHeadHit() {
        setCategoryFilter(Boot.DESTROYED_BIT);
        HUD.addScore(200);
        getCell().setTile(null);
        manager.get("audio/breakblock.wav", Sound.class).play();
    }

}