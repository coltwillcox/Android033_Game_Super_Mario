package com.colt.supermario.sprites.tiles;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.colt.supermario.Boot;
import com.colt.supermario.screens.ScreenPlay;

/**
 * Created by colt on 4/13/16.
 */

public class Brick extends MapTileObject {

    private AssetManager manager;
    private Vector2 originalPosition;
    private Vector2 movablePosition;
    private Vector2 targetPosition;

    public Brick(ScreenPlay screen, float x, float y, TiledMapTileMapObject mapObject, AssetManager manager) {
        super(screen, x, y, mapObject);
        this.manager = manager;

        originalPosition = new Vector2(x, y);
        movablePosition = new Vector2(x, y + 0.05f);
        targetPosition = originalPosition;
    }

    @Override
    public void update(float deltaTime) {
        float x = body.getPosition().x;
        float y = body.getPosition().y;
        Vector2 dist = new Vector2(x, y).sub(targetPosition);
        if (dist.len2() > 0.0001f)
            body.setTransform(new Vector2(x, y).lerp(targetPosition, 0.6f), 0);
        else {
            body.setTransform(targetPosition, 0);
            targetPosition = originalPosition;
        }

        setPosition(body.getPosition().x - getWidth() / 2, body.getPosition().y - getHeight() / 2);
    }

    @Override
    protected void defineBody() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(getX(), getY());
        bodyDef.type = BodyDef.BodyType.KinematicBody;

        body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(16 / Boot.PPM / 2, 16 / Boot.PPM / 2);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.filter.categoryBits = Boot.BRICK_BIT;
        fixtureDef.shape = shape;

        body.createFixture(fixtureDef).setUserData(this);

        shape.dispose();
    }

    public void onHeadHit() {
        targetPosition = movablePosition;
    }

}