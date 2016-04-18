package com.colt.supermario.sprites.tiles;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.MapObject;
import com.colt.supermario.Boot;
import com.colt.supermario.scenes.HUD;
import com.colt.supermario.screens.ScreenPlay;
import com.colt.supermario.sprites.Mario;

/**
 * Created by colt on 4/13/16.
 */

public class Brick extends InteractiveTileObject {

    private AssetManager manager;

    public Brick(ScreenPlay screen, MapObject object, AssetManager manager) {
        super(screen, object);
        this.manager = manager;
        fixture.setUserData(this);
        setCategoryFilter(Boot.BRICK_BIT);
    }

    @Override
    public void onHeadHit(Mario mario) {
        if (mario.isBig()) {
            manager.get("audio/breakblock.wav", Sound.class).play();
            setCategoryFilter(Boot.DESTROYED_BIT);
            HUD.addScore(200);
            getCell().setTile(null);
        } else {
            manager.get("audio/bump.wav", Sound.class).play();
        }
    }

}