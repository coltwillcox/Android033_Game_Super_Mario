package com.colt.supermario.sprites.tiles;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.colt.supermario.Boot;
import com.colt.supermario.screens.ScreenAbstract;
import com.colt.supermario.sprites.Mario;

/**
 * Created by colt on 4/20/16.
 */

public abstract class MapTileObject extends Sprite {
    protected ScreenAbstract screen;
    protected World world;
    protected Body body;
    protected TiledMapTileMapObject mapObject;
    protected boolean destroy;
    protected boolean destroyed;

    public MapTileObject(ScreenAbstract screen, float x, float y, TiledMapTileMapObject mapObject) {
        this.screen = screen;
        this.world = screen.getWorld();
        this.mapObject = mapObject;

        destroy = false;
        destroyed = false;

        setPosition(x, y);
        defineBody();

        setRegion(mapObject.getTextureRegion());

        float width = 16 / Boot.PPM;
        float height = 16 / Boot.PPM;

        setBounds(x - width / 2, y - height / 2, width, height);
    }

    public void update(float delta) {
        if (destroy && !destroyed) {
            world.destroyBody(body);
            destroyed = true;
        }
    };

    protected abstract void defineBody();

    public abstract void onHeadHit(Mario mario);

    public void setDestroy() {
        destroy = true;
    }

    public boolean isDestroyed() {
        return destroyed;
    }

}