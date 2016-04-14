package com.colt.supermario.sprites;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.math.Rectangle;
import com.colt.supermario.Boot;
import com.colt.supermario.scenes.HUD;
import com.colt.supermario.screens.ScreenPlay;

/**
 * Created by colt on 4/13/16.
 */

public class Coin extends InteractiveTileObject {

    private final int BLANK_COIN = 28;
    private static TiledMapTileSet tileSet;
    private AssetManager manager;

    public Coin(ScreenPlay screen, Rectangle bounds, AssetManager manager) {
        super(screen, bounds);
        this.manager = manager;
        tileSet = map.getTileSets().getTileSet("tileset"); //Name of the tileset from tmx file.
        fixture.setUserData(this);
        setCategoryFilter(Boot.COIN_BIT);
    }

    @Override
    public void onHeadHit() {
        if (getCell().getTile().getId() != BLANK_COIN) {
            HUD.addScore(100);
            getCell().setTile(tileSet.getTile(BLANK_COIN));
            manager.get("audio/coin.wav", Sound.class).play();
        } else {
            manager.get("audio/bump.wav", Sound.class).play();
        }
    }

}