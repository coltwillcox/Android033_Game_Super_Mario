package com.colt.supermario.sprites;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Rectangle;
import com.colt.supermario.Boot;
import com.colt.supermario.scenes.HUD;
import com.colt.supermario.screens.ScreenPlay;

/**
 * Created by colt on 4/13/16.
 */

public class Brick extends InteractiveTileObject {

    private AssetManager manager;

    public Brick(ScreenPlay screen, Rectangle bounds, AssetManager manager) {
        super(screen, bounds);
        this.manager = manager;
        fixture.setUserData(this);
        setCategoryFilter(Boot.BRICK_BIT);
    }

    @Override
    public void onHeadHit() {
        manager.get("audio/breakblock.wav", Sound.class).play();
        setCategoryFilter(Boot.DESTROYED_BIT);
        HUD.addScore(200);
        getCell().setTile(null);
    }

}