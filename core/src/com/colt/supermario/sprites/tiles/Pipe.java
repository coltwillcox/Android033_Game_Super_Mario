package com.colt.supermario.sprites.tiles;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.colt.supermario.Boot;
import com.colt.supermario.screens.ScreenPlay;
import com.colt.supermario.sprites.Mario;

/**
 * Created by colt on 4/20/16.
 */

public class Pipe extends MapTileObject {

    private AssetManager manager;

    public Pipe(ScreenPlay screen, float x, float y, TiledMapTileMapObject mapObject, AssetManager manager) {
        super(screen, x, y, mapObject);
        this.manager = manager;
    }

    @Override
    public void update(float deltaTime) {

    }

    @Override
    protected void defineBody() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(getX(), getY());
        bodyDef.type = BodyDef.BodyType.StaticBody;

        body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(16 / Boot.PPM / 2, 16 / Boot.PPM / 2);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.filter.categoryBits = Boot.OBJECT_BIT;
        fixtureDef.shape = shape;

        body.createFixture(fixtureDef).setUserData(this);

        shape.dispose();
    }

    @Override
    public void onHeadHit(Mario mario) {

    }

}