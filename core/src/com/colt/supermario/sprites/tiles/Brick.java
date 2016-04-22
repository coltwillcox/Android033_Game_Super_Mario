package com.colt.supermario.sprites.tiles;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.colt.supermario.Boot;
import com.colt.supermario.hud.HUD;
import com.colt.supermario.screens.ScreenPlay;
import com.colt.supermario.sprites.Mario;
import com.colt.supermario.sprites.particles.Debris;
import com.colt.supermario.sprites.particles.ParticleDefinition;

/**
 * Created by colt on 4/13/16.
 */

//TODO: Breaking or bumping.
//TODO: Try to create debris in separate 1/4th?

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
        super.update(deltaTime);
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
        shape.setAsBox(8 / Boot.PPM, 8 / Boot.PPM); //Half-width, half-height.

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.filter.categoryBits = Boot.BRICK_BIT;
        fixtureDef.shape = shape;

        body.createFixture(fixtureDef).setUserData(this);

        shape.dispose();
    }

    @Override
    public void onHeadHit(Mario mario) {
        if (!mario.isBig()) {
            manager.get("audio/bump.wav", Sound.class).play();
            targetPosition = movablePosition;
        }
        else {
            manager.get("audio/breakblock.wav", Sound.class).play();
            HUD.addScore(50);
            for (int i = 0; i < 4; i++)
                screen.spawnParticle(new ParticleDefinition(new Vector2(body.getPosition().x - (getWidth() / 4), body.getPosition().y - (getHeight() / 4)), Debris.class));
            destroy = true;
        }
    }

}