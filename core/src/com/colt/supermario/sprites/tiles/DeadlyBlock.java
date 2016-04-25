package com.colt.supermario.sprites.tiles;

import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.colt.supermario.Boot;
import com.colt.supermario.screens.ScreenAbstract;
import com.colt.supermario.sprites.Mario;

/**
 * Created by colt on 4/25/16.
 */

public class DeadlyBlock extends MapTileObject {

    public DeadlyBlock(ScreenAbstract screen, float x, float y, TiledMapTileMapObject mapObject) {
        super(screen, x, y, mapObject);
    }

    @Override
    protected void defineBody() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(getX(), getY());
        bodyDef.type = BodyDef.BodyType.StaticBody;

        body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(8 / Boot.PPM, 8 / Boot.PPM);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.filter.categoryBits = Boot.ENEMY_BIT;
        fixtureDef.shape = shape;
        body.createFixture(fixtureDef).setUserData(this);

        shape.dispose();
    }

    @Override
    public void onHeadHit(Mario mario) {
        //Not needed.
    }

}