package com.colt.supermario.sprites.tiles;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.colt.supermario.Boot;
import com.colt.supermario.screens.ScreenAbstract;
import com.colt.supermario.sprites.Mario;

/**
 * Created by colt on 4/23/16.
 */

public class Flagpole extends MapTileObject {

    private AssetManager manager;

    public Flagpole(ScreenAbstract screen, float x, float y, TiledMapTileMapObject mapObject, AssetManager manager) {
        super(screen, x, y, mapObject);
        this.manager = manager;
    }

    @Override
    protected void defineBody() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(getX(), getY());
        bodyDef.type = BodyDef.BodyType.StaticBody;

        body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(4 / Boot.PPM, 8 / Boot.PPM);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.filter.categoryBits = Boot.FLAGPOLE_BIT;
        fixtureDef.filter.maskBits = Boot.MARIO_BIT;
        fixtureDef.shape = shape;
        fixtureDef.isSensor = true;
        body.createFixture(fixtureDef).setUserData(this);

        shape.dispose();
    }

    @Override
    public void onHeadHit(Mario mario) {
        //Doesn't relly need it.
        onTouch(mario);
    }


    public void onTouch(Mario mario) {
        if (!screen.isFlagTouched()) {
            manager.get("audio/music.ogg", Music.class).stop();
            manager.get("audio/flag.wav", Music.class).play();
            screen.levelCompleted();
            mario.levelCompleted(body.getPosition().x - 4 / Boot.PPM); //Give pole position.
            screen.setFlagTouched(true);
        }

    }

}